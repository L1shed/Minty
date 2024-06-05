package keystrokesmod.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.MotionSkidder;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.script.classes.Vec3;
import net.minecraft.util.ChatComponentText;

import java.util.NoSuchElementException;

public class MotionModifier extends Module {
    private final ButtonSetting waitForDamage;
    private final ButtonSetting stopAtOnGround;
    private int enabledTicks = 0;
    public MotionModifier() {
        super("MotionModifier", category.movement);
        this.registerSetting(new DescriptionSetting("modifies your motion from MotionSkidder module."));
        this.registerSetting(waitForDamage = new ButtonSetting("Wait for damage", true));
        this.registerSetting(stopAtOnGround = new ButtonSetting("Stop at onGround", true));
    }

    @Override
    public void onEnable() {
        enabledTicks = 0;
    }

    @Override
    public void onDisable() {
        enabledTicks = 0;
        mc.thePlayer.addChatMessage(new ChatComponentText("motions modifies done!"));
    }

    @Override
    public void onUpdate() {
        if (!MotionSkidder.getMoves().isPresent()) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No valid motions saved."));
            ModuleManager.motionModifier.setEnabled(false);
        }
        if (waitForDamage.isToggled() && mc.thePlayer.hurtTime <= 0) return;
        if (stopAtOnGround.isToggled() && mc.thePlayer.onGround && mc.thePlayer.hurtTime <= 0) {
            disable();
            return;
        }

        try {
            MotionSkidder.MoveData moveData = MotionSkidder.getMoves().get().get(enabledTicks);
            Vec3 motion = moveData.getDeltaMove();
            if (enabledTicks == 0) {
                mc.thePlayer.addChatMessage(new ChatComponentText("Start modifying motions..."));
            }

            mc.thePlayer.motionX = motion.x();
            mc.thePlayer.motionY = motion.y();
            mc.thePlayer.motionZ = motion.z();
            mc.thePlayer.rotationYaw = moveData.getYaw();
            mc.thePlayer.rotationPitch = moveData.getPitch();
            mc.thePlayer.onGround = moveData.isOnGround();
        } catch (IndexOutOfBoundsException e) {
            disable();
            return;
        } catch (NoSuchElementException e) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No valid motions saved."));
            ModuleManager.motionModifier.setEnabled(false);
            enabledTicks = 0;
            return;
        }

        enabledTicks++;
    }
}
