package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;

public class NoSlow extends Module {
   private DescriptionSetting defaultSlowed;
   public static SliderSetting mode;
   public static SliderSetting slowed;
   public static ButtonSetting disableBow;
   public static ButtonSetting disablePotions;
   public static ButtonSetting swordOnly;
   public static ButtonSetting vanillaSword;
   private String[] modes = new String[]{"Vanilla", "Post"};

   public NoSlow() {
      super("NoSlow", Module.category.movement, 0);
      this.registerSetting(defaultSlowed = new DescriptionSetting("Default is 80% motion reduction."));
      this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
      this.registerSetting(slowed = new SliderSetting("Slow %", 80.0D, 0.0D, 80.0D, 1.0D));
      this.registerSetting(disableBow = new ButtonSetting("Disable bow", false));
      this.registerSetting(disablePotions = new ButtonSetting("Disable potions", false));
      this.registerSetting(swordOnly = new ButtonSetting("Sword only", false));
      this.registerSetting(vanillaSword = new ButtonSetting("Vanilla sword", false));
   }

   public static float getSlowed() {
      if (mc.thePlayer.getHeldItem() == null || ModuleManager.noSlow == null || !ModuleManager.noSlow.isEnabled()) {
         return 0.2f;
      }
      else {
         if (swordOnly.isToggled() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            return 0.2f;
         }
         if (mc.thePlayer.getHeldItem().getItem() instanceof ItemBow && disableBow.isToggled()) {
            return 0.2f;
         }
         else if (mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion && disablePotions.isToggled()) {
            return 0.2f;
         }
      }
      float val = (100.0F - (float) slowed.getInput()) / 100.0F;
      return val;
   }

   @Override
   public String getInfo() {
      return modes[(int) mode.getInput()];
   }
}
