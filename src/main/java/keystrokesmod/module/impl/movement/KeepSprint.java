package keystrokesmod.module.impl.movement;

import keystrokesmod.module.*;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.Entity;

public class KeepSprint extends Module {
   public static DescriptionSetting a;
   public static SliderSetting b;
   public static ButtonSetting c;

   public KeepSprint() {
      super("KeepSprint", Module.category.movement, 0);
      this.registerSetting(a = new DescriptionSetting(new String("Default is 40% motion reduction.")));
      this.registerSetting(b = new SliderSetting("Slow %", 40.0D, 0.0D, 40.0D, 1.0D));
      this.registerSetting(c = new ButtonSetting("Only reduce reach hits", false));
   }

   public static void sl(Entity en) {
      double dist;
      if (c.isToggled() && ModuleManager.reach.isEnabled() && !mc.thePlayer.capabilities.isCreativeMode) {
         dist = mc.objectMouseOver.hitVec.distanceTo(mc.getRenderViewEntity().getPositionEyes(1.0F));
         double val;
         if (dist > 3.0D) {
            val = (100.0D - (double)((float)b.getInput())) / 100.0D;
         } else {
            val = 0.6D;
         }

         mc.thePlayer.motionX *= val;
         mc.thePlayer.motionZ *= val;
      } else {
         dist = (100.0D - (double)((float)b.getInput())) / 100.0D;
         mc.thePlayer.motionX *= dist;
         mc.thePlayer.motionZ *= dist;
      }
   }
}
