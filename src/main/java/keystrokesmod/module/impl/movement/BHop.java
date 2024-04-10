package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import org.lwjgl.input.Keyboard;

public class BHop extends Module {
    private SliderSetting mode;
    public static SliderSetting speed;
    private ButtonSetting waterDisable;
    private ButtonSetting sneakDisable;
    private ButtonSetting stopMotion;
    private String[] modes = new String[]{"Strafe", "Ground"};

    public BHop() {
        super("Bhop", Module.category.movement);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 2.0, 0.5, 8.0, 0.1));
        this.registerSetting(waterDisable = new ButtonSetting("Disable in water", true));
        this.registerSetting(sneakDisable = new ButtonSetting("Disable while sneaking", true));
        this.registerSetting(stopMotion = new ButtonSetting("Stop motion", false));
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    public void onUpdate() {
        if ((mc.thePlayer.isInWater() && waterDisable.isToggled()) || (mc.thePlayer.isSneaking() && sneakDisable.isToggled())) {
            return;
        }
        switch ((int) mode.getInput()) {
            case 0:
                if (Utils.isStrafing()) {
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                    mc.thePlayer.setSprinting(true);
                    Utils.setMotion(Utils.getHorizontalSpeed() + 0.005 * speed.getInput());
                    break;
                }
                break;
            case 1:
                if (!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && Utils.isStrafing() && mc.currentScreen != null) {
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
                    Utils.setMotion(horizontalSpeed);
                }
                break;
        }
    }

    public void onDisable() {
        if (stopMotion.isToggled()) {
            final double motionX = 0.0;
            mc.thePlayer.motionZ = motionX;
            mc.thePlayer.motionY = motionX;
            mc.thePlayer.motionX = motionX;
        }
    }
}
