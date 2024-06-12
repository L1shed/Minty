package keystrokesmod.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.MotionSkidder;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.script.classes.Vec3;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.NoSuchElementException;

public class MotionModifier extends Module {
    private final ButtonSetting waitForDamage;
    private final ButtonSetting stopAtOnGround;
    private final ButtonSetting positionMove;
    private final ButtonSetting motionMove;
    private int enabledTicks = 0;
    public MotionModifier() {
        super("MotionModifier", category.movement);
        this.registerSetting(new DescriptionSetting("modifies your motion from MotionSkidder module."));
        this.registerSetting(waitForDamage = new ButtonSetting("Wait for damage", true));
        this.registerSetting(stopAtOnGround = new ButtonSetting("Stop at onGround", true));
        this.registerSetting(positionMove = new ButtonSetting("Position move", false));
        this.registerSetting(motionMove = new ButtonSetting("Motion move", true));
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
        enabledTicks++;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPreMotion(PreMotionEvent event) {
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

            if (motionMove.isToggled()) {
                mc.thePlayer.motionX = motion.x();
                mc.thePlayer.motionY = motion.y();
                mc.thePlayer.motionZ = motion.z();
            }
            if (positionMove.isToggled()) {
                event.setPosX(event.getPosX() + motion.x());
                event.setPosY(event.getPosY() + motion.z());
                event.setPosZ(event.getPosZ() + motion.z());
            }
            event.setYaw(moveData.getYaw());
            event.setPitch(moveData.getPitch());
            event.setOnGround(moveData.isOnGround());
        } catch (IndexOutOfBoundsException e) {
            disable();
        } catch (NoSuchElementException e) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No valid motions saved."));
            ModuleManager.motionModifier.setEnabled(false);
            enabledTicks = 0;
        }
    }
}
