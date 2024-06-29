package keystrokesmod.module.impl.world;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.anticheats.utils.phys.Vec2;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerRotation;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.*;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockIn extends Module {
    private static final String[] rotationModes = new String[]{"None", "Block", "Strict"};
    private final ModeSetting rotationMode;
    private final SliderSetting aimSpeed;
    private final ButtonSetting lookView;
    private final SliderSetting placeDelay;
    private final ButtonSetting silentSwing;
    private final ButtonSetting autoSwitch;

    private Vec2 currentRot = null;
    private long lastPlace = 0;

    private int lastSlot = -1;

    public BlockIn() {
        super("Block-In", category.world);
        this.registerSetting(new DescriptionSetting("make you block in."));
        this.registerSetting(rotationMode = new ModeSetting("Rotation mode", rotationModes, 2));
        this.registerSetting(aimSpeed = new SliderSetting("Aim speed", 5, 0, 5, 0.05));
        this.registerSetting(lookView = new ButtonSetting("Look view", false));
        this.registerSetting(placeDelay = new SliderSetting("Place delay", 50, 0, 500, 1, "ms"));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing", false));
        this.registerSetting(autoSwitch = new ButtonSetting("Auto switch", true));
    }

    @Override
    public void onDisable() {
        currentRot = null;
        lastPlace = 0;
        if (autoSwitch.isToggled() && lastSlot != -1) {
            mc.thePlayer.inventory.currentItem = lastSlot;
        }
        lastSlot = -1;
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (autoSwitch.isToggled() && lastSlot == -1) {
            int slot = ContainerUtils.getSlot(ItemBlock.class);
            lastSlot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.inventory.currentItem = slot;
        }

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

            Triple<BlockPos, EnumFacing, Vec3> placeSideBlock;
            try {
                placeSideBlock = getPlaceSide(blockPos).orElseThrow(NoSuchElementException::new);
            } catch (NoSuchElementException e) {
                continue;
            }

            Vec3 hitPos = placeSideBlock.getRight();
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
                        placeSideBlock.getLeft(), placeSideBlock.getMiddle(),
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
        return BlockUtils.getSurroundBlocks(mc.thePlayer);
    }

    private @NotNull Optional<Triple<BlockPos, EnumFacing, Vec3>> getPlaceSide(@NotNull BlockPos blockPos) {
        final List<BlockPos> possible = Arrays.asList(
                blockPos.down(), blockPos.east(), blockPos.west(),
                blockPos.north(), blockPos.south(), blockPos.up()
        );

        for (BlockPos pos : possible) {
            if (BlockUtils.getBlockState(pos).getBlock().isFullBlock()) {
                EnumFacing facing;
                Vec3 hitPos;
                if (pos.getY() < blockPos.getY()) {
                    facing = EnumFacing.UP;
                    hitPos = new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                } else if (pos.getX() > blockPos.getX()) {
                    facing = EnumFacing.WEST;
                    hitPos = new Vec3(pos.getX(), pos.getY() + 0.5, pos.getZ() + 0.5);
                } else if (pos.getX() < blockPos.getX()) {
                    facing = EnumFacing.EAST;
                    hitPos = new Vec3(pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 0.5);
                } else if (pos.getZ() < blockPos.getZ()) {
                    facing = EnumFacing.SOUTH;
                    hitPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1);
                } else if (pos.getZ() > blockPos.getZ()) {
                    facing = EnumFacing.NORTH;
                    hitPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ());
                } else {
                    facing = EnumFacing.DOWN;
                    hitPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                }

                return Optional.of(Triple.of(pos, facing, hitPos));
            }
        }
        return Optional.empty();
    }
}
