package keystrokesmod.module.impl.world;

import akka.japi.Pair;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.anticheats.utils.phys.Vec2;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerRotation;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.AimSimulator;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockIn extends Module {
    private static final String[] rotationModes = new String[]{"None", "Block", "Strict"};
    private final SliderSetting rotationMode;
    private final SliderSetting aimSpeed;
    private final ButtonSetting lookView;
    private final SliderSetting placeDelay;
    private final ButtonSetting silentSwing;

    private Vec2 currentRot = null;
    private long lastPlace = 0;

    public BlockIn() {
        super("Block-In", category.world);
        this.registerSetting(new DescriptionSetting("make you block in."));
        this.registerSetting(rotationMode = new SliderSetting("Rotation mode", rotationModes, 1));
        this.registerSetting(aimSpeed = new SliderSetting("Aim speed", 5, 0, 5, 0.05));
        this.registerSetting(lookView = new ButtonSetting("Look view", false));
        this.registerSetting(placeDelay = new SliderSetting("Place delay", 50, 0, 500, 1, "ms"));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing", false));
    }

    @Override
    public void onDisable() {
        currentRot = null;
        lastPlace = 0;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreMotion(PreMotionEvent event) {
        if (currentRot == null) return;
        if (rotationMode.getInput() == 0) {
            currentRot = null;
            return;
        }
        if (!lookView.isToggled()) {
            event.setYaw(currentRot.x);
            event.setPitch(currentRot.y);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRender(TickEvent.RenderTickEvent event) {
        try {
            if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
                Utils.sendMessage("No blocks found.");
                disable();
                return;
            }
        } catch (Exception e) {
            Utils.sendMessage("No blocks found.");
            disable();
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlace < placeDelay.getInput()) return;

        int placed = 0;
        boolean rotating = false;
        for (BlockPos blockPos : getBlockInBlocks()) {
            if (currentTime - lastPlace < placeDelay.getInput()) return;
            if (!BlockUtils.replaceable(blockPos)) continue;

            Pair<BlockPos, EnumFacing> placeSideBlock;
            try {
                placeSideBlock = getPlaceSide(blockPos).orElseThrow(NoSuchElementException::new);
            } catch (NoSuchElementException e) {
                continue;
            }

            Vec3 hitPos = new Vec3(
                    placeSideBlock.first().getX() + 0.5,
                    placeSideBlock.first().getY() + 1,
                    placeSideBlock.first().getZ() + 0.5
            );
            Vec2 rotation = new Vec2(PlayerRotation.getYaw(hitPos), PlayerRotation.getPitch(hitPos));

            if ((int) rotationMode.getInput() == 2) {
                MovingObjectPosition hitResult = RotationUtils.rayCast(4.5, rotation.x, rotation.y);
                if (hitPos.distanceTo(hitResult.hitVec) > 0.05) continue;
            }

            if (currentRot == null) {
                currentRot = new Vec2(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            }
            if (rotationMode.getInput() != 0 && !currentRot.equals(rotation)) {
                if (aimSpeed.getInput() == 5) {
                    currentRot = rotation;
                } else {
                    currentRot = new Vec2(
                            AimSimulator.rotMove(rotation.x, currentRot.x, (float) aimSpeed.getInput()),
                            AimSimulator.rotMove(rotation.y, currentRot.y, (float) aimSpeed.getInput())
                    );
                    rotating = true;
                }

                if (lookView.isToggled()) {
                    mc.thePlayer.rotationYaw = currentRot.x;
                    mc.thePlayer.rotationPitch = currentRot.y;
                }
                if (rotating) return;
            }

            if (rotationMode.getInput() == 0 || currentRot.equals(rotation)) {
                mc.playerController.onPlayerRightClick(
                        mc.thePlayer, mc.theWorld,
                        mc.thePlayer.getHeldItem(),
                        placeSideBlock.first(), placeSideBlock.second(),
                        hitPos.toVec3()
                );

                if (silentSwing.isToggled()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                } else {
                    mc.thePlayer.swingItem();
                    mc.getItemRenderer().resetEquippedProgress();
                }

                lastPlace = currentTime;
                placed++;
            }
        }
        if (placed == 0) disable();
    }

    private @NotNull Set<BlockPos> getBlockInBlocks() {
        Set<BlockPos> surroundBlocks = BlockUtils.getSurroundBlocks(mc.thePlayer);

        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos block : surroundBlocks) {
            maxX = Math.max(block.getX(), maxX);
            maxY = Math.max(block.getY(), maxY);
            maxZ = Math.max(block.getZ(), maxZ);
        }

        surroundBlocks.add(new BlockPos(maxX - 1, maxY, maxZ));
        return surroundBlocks;
    }

    private @NotNull Optional<Pair<BlockPos, EnumFacing>> getPlaceSide(@NotNull BlockPos blockPos) {
        final List<BlockPos> possible = Arrays.asList(
                blockPos.down(), blockPos.east(), blockPos.west(),
                blockPos.north(), blockPos.south(), blockPos.up()
        );

        for (BlockPos pos : possible) {
            if (BlockUtils.getBlockState(pos).getBlock().isFullBlock()) {
                EnumFacing facing;
                if (pos.getY() < blockPos.getY()) facing = EnumFacing.UP;
                else if (pos.getX() > blockPos.getX()) facing = EnumFacing.EAST;
                else if (pos.getX() < blockPos.getX()) facing = EnumFacing.WEST;
                else if (pos.getZ() < blockPos.getZ()) facing = EnumFacing.NORTH;
                else if (pos.getZ() > blockPos.getZ()) facing = EnumFacing.SOUTH;
                else facing = EnumFacing.DOWN;

                return Optional.of(new Pair<>(pos, facing));
            }
        }
        return Optional.empty();
    }
}
