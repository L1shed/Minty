package keystrokesmod.module.impl.fun;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.RotationEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;


public class Spin extends Module {
    private final SliderSetting speed = new SliderSetting("Speed", 25, -30, 30, 1);
    private final ButtonSetting constantPitch = new ButtonSetting("Constant pitch", true);
    private final SliderSetting pitch = new SliderSetting("Pitch", 90, -90, 90, 5, constantPitch::isToggled);
    private final ButtonSetting cancelSprint = new ButtonSetting("Cancel sprint", false);

    private Float lastYaw = null;
    private Float lastPitch = null;
    public boolean pitchReserve = false;

    public Spin() {
        super("Spin", category.fun);
        this.registerSetting(speed, constantPitch, pitch, cancelSprint);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRotation(@NotNull RotationEvent event) {
        if (lastYaw == null) {
            lastYaw = event.getYaw();
        }
        event.setYaw(lastYaw += (float) speed.getInput());


        if (constantPitch.isToggled()) {
            event.setPitch((float) pitch.getInput());
        } else {
            if (lastPitch == null) {
                lastPitch = event.getPitch();
            }

            pitchCheck();
            lastPitch += (float) speed.getInput() * (pitchReserve ? -1 : 1);
            pitchCheck();
            event.setPitch(lastPitch);
        }
    }

    private void pitchCheck() {
        if (lastPitch >= 90) {
            lastPitch = 90f;
            pitchReserve = true;
        } else if (lastPitch <= -90) {
            lastPitch = -90f;
            pitchReserve = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreMotion(PreMotionEvent event) {
        if (cancelSprint.isToggled()) {
            event.setSprinting(false);
        }
    }

    @Override
    public void onDisable() {
        lastYaw = null;
        lastPitch = null;
        pitchReserve = false;
    }
}

