package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.utility.Utils;
import keystrokesmod.module.impl.minigames.DuelsStats;

public class NameHider extends Module {
   public static DescriptionSetting a;
   public static String n = "raven";

   public NameHider() {
      super("Name Hider", Module.category.other);
      this.registerSetting(a = new DescriptionSetting(Utils.uf("command") + ": cname [name]"));
   }

   public static String getFakeName(String s) {
      if (mc.thePlayer != null) {
         s = DuelsStats.nk.isEmpty() ? s.replace(mc.thePlayer.getName(), n) : s.replace(DuelsStats.nk, n);
      }

      return s;
   }
}
