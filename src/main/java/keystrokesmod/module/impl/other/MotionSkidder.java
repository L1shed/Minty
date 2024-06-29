package keystrokesmod.module.impl.other;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.anticheats.utils.phys.Vec2;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.script.classes.Vec3;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MotionSkidder extends Module {
    private final ModeSetting mode;
    private final ButtonSetting includeRotation;
    private final ButtonSetting waitForDamage;
    private final ButtonSetting stopAtOnGround;
    private AbstractClientPlayer target = null;
    private static List<MoveData> moves = null;
    public MotionSkidder() {
        super("MotionSkidder", category.other);
        this.registerSetting(new DescriptionSetting("Tries to skid other's motion."));
        this.registerSetting(mode = new ModeSetting("Mode", new String[]{"Motion", "Position"}, 1));
        this.registerSetting(includeRotation = new ButtonSetting("Include rotation", true));
        this.registerSetting(waitForDamage = new ButtonSetting("Wait for damage", true));
        this.registerSetting(stopAtOnGround = new ButtonSetting("Stop at onGround", true));
    }

    @Override
    public void onEnable() {
        target = null;
        moves = null;
        mc.thePlayer.addChatMessage(new ChatComponentText("Type a name of player to skid his motion."));
    }

    @SubscribeEvent
    public void onPacketSend(@NotNull SendPacketEvent event) {
        if (event.getPacket() instanceof C01PacketChatMessage && ModuleManager.motionSkidder.isEnabled() && target == null) {
            event.setCanceled(true);
            try {
                target = (AbstractClientPlayer) mc.theWorld.playerEntities.stream()
                        .filter(player -> Objects.equals(player.getName(), ((C01PacketChatMessage) event.getPacket()).getMessage()))
                        .findAny()
                        .orElseThrow(NoSuchElementException::new);
                mc.thePlayer.addChatMessage(new ChatComponentText("Set target: " + ChatFormatting.GRAY + target.getName()));
            } catch (NoSuchElementException | ClassCastException e) {
                mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.DARK_RED + "Invalid player name!"));
                ModuleManager.motionSkidder.setEnabled(false);
            }
        }
    }

    public static Optional<List<MoveData>> getMoves() {
        return Optional.ofNullable(moves);
    }

    @Override
    public void onDisable() {
        target = null;
        if (moves != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.GREEN + "Skidded motion successfully! (" + moves.size() + " ticks)"));
        } else {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No motions has been skidded."));
        }
    }

    @Override
    public void onUpdate() {
        if (target == null) return;
        if (moves == null && waitForDamage.isToggled() && target.hurtTime <= 0) return;
        if (moves != null && stopAtOnGround.isToggled() && target.onGround && target.hurtTime <= 0) {
            disable();
            return;
        }

        if (moves == null) {
            moves = new ArrayList<>();
            mc.thePlayer.addChatMessage(new ChatComponentText("Start record motions..."));
        }

        Vec3 deltaMove;
        if (mode.getInput() == 1) {
            deltaMove = new Vec3(target.posX - target.lastTickPosX, target.posY - target.lastTickPosY, target.posZ - target.lastTickPosZ);
        } else {
            deltaMove = new Vec3(target.motionX, target.motionY, target.motionZ);
        }

        if (includeRotation.isToggled()) {
            moves.add(new MoveData(deltaMove, target.rotationYaw, target.rotationPitch, target.onGround));
        } else {
            moves.add(new MoveData(deltaMove, target.onGround));
        }
    }

    public static class MoveData {
        private final Vec3 deltaMove;
        private final Vec2 rotation;
        private final boolean onGround;

        public MoveData(Vec3 deltaMove, float yaw, float pitch, boolean onGround) {
            this.deltaMove = deltaMove;
            this.rotation = new Vec2(yaw, pitch);
            this.onGround = onGround;
        }
        public MoveData(Vec3 deltaMove, boolean onGround) {
            this.deltaMove = deltaMove;
            this.rotation = null;
            this.onGround = onGround;
        }

        public Vec3 getDeltaMove() {
            return deltaMove;
        }
        public float getYaw() {
            return rotation == null ? mc.thePlayer.rotationYaw : rotation.x;
        }
        public float getPitch() {
            return rotation == null ? mc.thePlayer.rotationPitch : rotation.y;
        }
        public boolean isOnGround() {
            return onGround;
        }
    }
}
