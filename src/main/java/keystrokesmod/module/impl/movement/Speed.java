package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

public class Speed extends Module {
    private final SliderSetting mode;
    public static SliderSetting speed;
    private final ButtonSetting liquidDisable;
    private final ButtonSetting sneakDisable;
    private final ButtonSetting stopMotion;
    private final String[] modes = new String[]{"Strafe", "Ground"};
    public boolean hopping;
    private int offGroundTicks = 0;

    public Speed() {
        super("Speed", Module.category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 1.0, 0.5, 3.0, 0.1));
        this.registerSetting(liquidDisable = new ButtonSetting("Disable in liquid", true));
        this.registerSetting(sneakDisable = new ButtonSetting("Disable while sneaking", true));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    public void onUpdate() {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        if (((mc.thePlayer.isInWater()
                || mc.thePlayer.isInLava()) && liquidDisable.isToggled())
                || (mc.thePlayer.isSneaking() && sneakDisable.isToggled())
                || ModuleManager.scaffold.isEnabled()
        ) {
            return;
        }
        switch ((int) mode.getInput()) {
            case 0:
                if (!Utils.jumpDown() && Utils.isMoving() && mc.currentScreen == null) {
                    mc.thePlayer.setSprinting(true);
                    switch (offGroundTicks) {
                        case 0:
                            MoveUtil.strafe(0.415);
                            mc.thePlayer.motionY = 0.42;
                            hopping = true;
                            break;
                        case 10:
                            MoveUtil.strafe(0.315);
                            mc.thePlayer.motionY = -0.28;
                            break;
                        case 11:
                            MoveUtil.strafe();
                            break;
                        case 12:
                            MoveUtil.stop();
                            break;
                    }
                }
                break;
            case 1:
                if (!Utils.jumpDown() && Utils.isMoving() && mc.currentScreen == null) {
                    if (!mc.thePlayer.onGround) {
                        break;
                    }
                    mc.thePlayer.jump();
                    mc.thePlayer.setSprinting(true);
                    double horizontalSpeed = Utils.getHorizontalSpeed();
                    double additionalSpeed = 0.4847 * ((speed.getInput() - 1.0) / 3.0 + 1.0);
                    if (horizontalSpeed < additionalSpeed) {
                        horizontalSpeed = additionalSpeed;
                    }
                    Utils.setSpeed(horizontalSpeed);
                    hopping = true;
                }
                break;
        }
    }

    public void onDisable() {
        if (stopMotion.isToggled()) {
            MoveUtil.stop();
        }
        hopping = false;
    }
}
