package keystrokesmod.module.impl.fun;

import keystrokesmod.event.RotationEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;


public class Spin extends Module {
    private final SliderSetting speed = new SliderSetting("Speed", 30, 10, 45, 5);
    private final ButtonSetting constantPitch = new ButtonSetting("Constant pitch", true);
    private final SliderSetting pitch = new SliderSetting("Pitch", 90, -90, 90, 5, constantPitch::isToggled);

    private Float lastYaw = null;
    private Float lastPitch = null;

    public Spin() {
        super("Spin", category.fun);
        this.registerSetting(speed, constantPitch, pitch);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRotation(@NotNull RotationEvent event) {
        if (lastYaw == null) {
            lastPitch = event.getPitch();
        }
        event.setYaw(lastYaw = lastYaw + (float) speed.getInput());


        if (constantPitch.isToggled()) {
            event.setPitch((float) pitch.getInput());
        } else {
            if (lastPitch == null) {
                lastPitch = event.getPitch();
            }
            event.setPitch(lastPitch = lastPitch + (float) speed.getInput());
        }
    }

    @Override
    public void onDisable() {
        lastYaw = null;
        lastPitch = null;
    }
}

