package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;

public class Gui extends Module {
    public static ButtonSetting removePlayerModel, translucentBackground, removeWatermark, rainBowOutlines;

    public Gui() {
        super("Gui", Module.category.client, 54);
        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", true));
        this.registerSetting(removePlayerModel = new ButtonSetting("Remove player model", false));
        this.registerSetting(removeWatermark = new ButtonSetting("Remove watermark", false));
        this.registerSetting(translucentBackground = new ButtonSetting("Translucent background", true));
    }

    public void onEnable() {
        if (Utils.nullCheck() && mc.currentScreen != Raven.clickGui) {
            mc.displayGuiScreen(Raven.clickGui);
            Raven.clickGui.initMain();
        }

        this.disable();
    }
}
