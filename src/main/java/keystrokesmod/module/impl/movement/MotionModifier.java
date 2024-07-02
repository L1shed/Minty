package keystrokesmod.module.impl.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.MotionSkidder;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.Utils;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.NoSuchElementException;

public class MotionModifier extends Module {
    private final ButtonSetting waitForDamage;
    private final ButtonSetting stopAtOnGround;
    private final ButtonSetting positionMove;
    private final ButtonSetting motionMove;
    private final ButtonSetting speedMode;
    private final ButtonSetting debug;
    private int enabledTicks = 0;
    public MotionModifier() {
        super("MotionModifier", category.movement);
        this.registerSetting(new DescriptionSetting("modifies your motion from MotionSkidder module."));
        this.registerSetting(waitForDamage = new ButtonSetting("Wait for damage", true));
        this.registerSetting(stopAtOnGround = new ButtonSetting("Stop at onGround", true));
        this.registerSetting(positionMove = new ButtonSetting("Position move", false));
        this.registerSetting(motionMove = new ButtonSetting("Motion move", true));
        this.registerSetting(speedMode = new ButtonSetting("Speed mode", false));
        this.registerSetting(debug = new ButtonSetting("Debug", false));
    }

    @Override
    public void onEnable() {
        enabledTicks = 0;
        if (!MotionSkidder.getMoves().isPresent()) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No valid motions saved."));
            ModuleManager.motionModifier.setEnabled(false);
        }
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

            if (debug.isToggled()) {
                StringBuilder stringBuilder = new StringBuilder();
                if (!speedMode.isToggled()) stringBuilder.append(motion.x()).append(", ");
                stringBuilder.append(motion.y());
                if (!speedMode.isToggled()) stringBuilder.append(", ").append(motion.z());
                Utils.sendMessage(stringBuilder.toString());
            }
            if (motionMove.isToggled()) {
                if (!speedMode.isToggled()) mc.thePlayer.motionX = motion.x();
                mc.thePlayer.motionY = motion.y();
                if (!speedMode.isToggled()) mc.thePlayer.motionZ = motion.z();
            }
            if (positionMove.isToggled()) {
                mc.thePlayer.setPosition(
                        mc.thePlayer.posX + (speedMode.isToggled() ? 0 : motion.x()),
                        mc.thePlayer.posY + motion.y(),
                        mc.thePlayer.posZ + (speedMode.isToggled() ? 0 : motion.z())
                );
            }
            event.setYaw(moveData.getYaw());
            event.setPitch(moveData.getPitch());
            event.setOnGround(moveData.isOnGround());
            mc.thePlayer.onGround = moveData.isOnGround();
        } catch (IndexOutOfBoundsException e) {
            if (speedMode.isToggled()) {
                enabledTicks = 0;
            } else {
                disable();
            }
        } catch (NoSuchElementException e) {
            mc.thePlayer.addChatMessage(new ChatComponentText(ChatFormatting.RED + "No valid motions saved."));
            ModuleManager.motionModifier.setEnabled(false);
            enabledTicks = 0;
        }
    }
}
