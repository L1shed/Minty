package keystrokesmod.module.impl.movement;

import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

public class Timer extends Module {
    public static SliderSetting a;
    public static ButtonSetting b;

    public Timer() {
        super("Timer", Module.category.movement, 0);
        a = new SliderSetting("Speed", 1.0D, 0.5D, 2.5D, 0.01D);
        b = new ButtonSetting("Strafe only", false);
        this.registerSetting(a);
        this.registerSetting(b);
    }

    public void onUpdate() {
        if (!(mc.currentScreen instanceof ClickGui)) {
            if (b.isToggled() && mc.thePlayer.moveStrafing == 0.0F) {
                Utils.resetTimer();
                return;
            }

            Utils.getTimer().timerSpeed = (float) a.getInput();
        } else {
            Utils.resetTimer();
        }

    }

    public void onDisable() {
        Utils.resetTimer();
    }
}
