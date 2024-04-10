package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;

public class Fly extends Module {
    private final Fly.VanFly vanFly = new Fly.VanFly();
    private final Fly.GliFly gliFly = new Fly.GliFly();
    public static SliderSetting mode;
    public static SliderSetting speed;
    private String[] modes = new String[]{"Vanilla", "Glide"};

    public Fly() {
        super("Fly", category.movement);
        this.registerSetting(mode = new SliderSetting("Value", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 2.0D, 1.0D, 5.0D, 0.1D));
    }

    public void onEnable() {
        switch ((int) mode.getInput()) {
            case 0:
                this.vanFly.onEnable();
                break;
            case 1:
                this.gliFly.onEnable();
        }

    }

    public void onDisable() {
        switch ((int) mode.getInput()) {
            case 0:
                this.vanFly.onDisable();
                break;
            case 1:
                this.gliFly.onDisable();
        }

    }

    public void onUpdate() {
        switch ((int) mode.getInput()) {
            case 0:
                this.vanFly.update();
                break;
            case 1:
                this.gliFly.update();
        }

    }

    class GliFly {
        boolean opf = false;

        public void onEnable() {
        }

        public void onDisable() {
            this.opf = false;
        }

        public void update() {
            if (mc.thePlayer.movementInput.moveForward > 0.0F) {
                if (!this.opf) {
                    this.opf = true;
                    if (mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                    }
                } else {
                    if (mc.thePlayer.onGround || mc.thePlayer.isCollidedHorizontally) {
                        Fly.this.disable();
                        return;
                    }

                    double s = 1.94D * Fly.speed.getInput();
                    double r = Math.toRadians(mc.thePlayer.rotationYaw + 90.0F);
                    mc.thePlayer.motionX = s * Math.cos(r);
                    mc.thePlayer.motionZ = s * Math.sin(r);
                }
            }

        }
    }

    class VanFly {
        public void onEnable() {
        }

        public void onDisable() {
            if (mc.thePlayer == null) {
                return;
            }
            if (mc.thePlayer.capabilities.isFlying) {
                mc.thePlayer.capabilities.isFlying = false;
            }

            mc.thePlayer.capabilities.setFlySpeed(0.05F);
        }

        public void update() {
            mc.thePlayer.motionY = 0.0D;
            mc.thePlayer.capabilities.setFlySpeed((float) (0.05000000074505806D * Fly.speed.getInput()));
            mc.thePlayer.capabilities.isFlying = true;
        }
    }
}
