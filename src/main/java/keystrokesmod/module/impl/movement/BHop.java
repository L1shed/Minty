package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;

public class BHop extends Module {
   public static SliderSetting a;
   private final double bspd = 0.0025D;

   public BHop() {
      super("Bhop", Module.category.movement, 0);
      this.registerSetting(a = new SliderSetting("Speed", 2.0D, 1.0D, 15.0D, 0.2D));
   }

   public void onUpdate() {
      if (!ModuleManager.fly.isEnabled() && Utils.isStrafing() && !mc.thePlayer.isInWater()) {
         KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
         mc.thePlayer.noClip = true;
         if (mc.thePlayer.onGround) {
            mc.thePlayer.jump();
         }

         mc.thePlayer.setSprinting(true);
         double spd = 0.0025D * a.getInput();
         double m = (double)((float)(Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ) + spd));
         Utils.ss2(m);
      }
   }
}
