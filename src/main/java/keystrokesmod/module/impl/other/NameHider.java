package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.minigames.DuelsStats;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.utility.Utils;

public class NameHider extends Module {
    public static DescriptionSetting a;
    public static String n = "raven";

    public NameHider() {
        super("Name Hider", Module.category.other);
        this.registerSetting(a = new DescriptionSetting(Utils.uf("command") + ": cname [name]"));
    }

    public static String getFakeName(String s) {
        if (mc.thePlayer != null) {
            s = DuelsStats.nick.isEmpty() ? s.replace(mc.thePlayer.getName(), n) : s.replace(DuelsStats.nick, n);
        }

        return s;
    }
}
