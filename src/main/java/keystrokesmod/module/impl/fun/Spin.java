package keystrokesmod.module.impl.fun;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.entity.EntityPlayerSP;


public class Spin extends Module {
        public SliderSetting a;
        public SliderSetting b;
        private float yaw;

        public Spin() {
            super("Spin", category.fun, 0);
            this.registerSetting(a = new SliderSetting("Rotation", 360.0D, 30.0D, 360.0D, 1.0D));
            this.registerSetting(b = new SliderSetting("Speed", 25.0D, 1.0D, 60.0D, 1.0D));
        }

        public void onEnable() {
            this.yaw = mc.thePlayer.rotationYaw;
        }

        public void onDisable() {
            this.yaw = 0.0F;
        }

        public void onUpdate() {
            double left = (double) this.yaw + a.getInput() - (double) mc.thePlayer.rotationYaw;
            EntityPlayerSP var10000;
            if (left < b.getInput()) {
                var10000 = mc.thePlayer;
                var10000.rotationYaw = (float) ((double) var10000.rotationYaw + left);
                this.disable();
            } else {
                var10000 = mc.thePlayer;
                var10000.rotationYaw = (float) ((double) var10000.rotationYaw + b.getInput());
                if ((double) mc.thePlayer.rotationYaw >= (double) this.yaw + a.getInput()) {
                    this.disable();
                }
            }

        }
    }

