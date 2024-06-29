package keystrokesmod.module.impl.movement;

import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.utils.ModeOnly;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Timer extends Module {
    private final ModeSetting mode;
    public static SliderSetting speed;
    private final SliderSetting maxBalance;
    private final SliderSetting costMultiplier;
    private final ButtonSetting autoDisable;
    public static ButtonSetting strafeOnly;

    private long balance = 0;
    private long startTime = -1;
    private BalanceState balanceState = BalanceState.NONE;

    public Timer() {
        super("Timer", Module.category.movement, 0);
        this.registerSetting(mode = new ModeSetting("Mode", new String[]{"Normal", "Balance"}, 0));
        final ModeOnly mode1 = new ModeOnly(mode, 1);
        this.registerSetting(speed = new SliderSetting("Speed", 1.00, 0.01, 10.0, 0.01));
        this.registerSetting(maxBalance = new SliderSetting("Max balance", 3000, 0, 10000, 10, "ms", mode1));
        this.registerSetting(costMultiplier = new SliderSetting("Cost multiplier", 1, 0.5, 5, 0.05, "x", mode1));
        this.registerSetting(autoDisable = new ButtonSetting("Auto disable", true, mode1));
        this.registerSetting(strafeOnly = new ButtonSetting("Strafe only", false));
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (mc.currentScreen instanceof ClickGui) {
            reset();
        } else {
            if (strafeOnly.isToggled() && mc.thePlayer.moveStrafing == 0.0F) {
                reset();
                return;
            }

            switch ((int) mode.getInput()) {
                case 0:
                    Utils.getTimer().timerSpeed = (float) speed.getInput();
                    break;
                case 1:
                    final long currentTime = System.currentTimeMillis();
                    switch (balanceState) {
                        case NONE:
                            startTime = currentTime;
                            Utils.getTimer().timerSpeed = 0;
                            balanceState = BalanceState.WAITING;
                            break;
                        case WAITING:
                            if (balance >= maxBalance.getInput()) {
                                balance = (long) maxBalance.getInput();
                                balanceState = BalanceState.TIMER;
                                startTime = currentTime;
                            } else {
                                balance = currentTime - startTime;
                                break;
                            }
                        case TIMER:
                            balance -= (long) ((currentTime - startTime) * speed.getInput() * costMultiplier.getInput());
                            if (balance <= 0) {
                                reset();
                                if (autoDisable.isToggled())
                                    disable();
                                break;
                            }
                            startTime = currentTime;
                            Utils.getTimer().timerSpeed = (float) speed.getInput();
                            break;
                    }
                    break;
            }
        }
    }

    private void reset() {
        Utils.resetTimer();
        balance = 0;
        balanceState = BalanceState.NONE;
    }

    @Override
    public String getInfo() {
        switch ((int) mode.getInput()) {
            case 0:
                return String.valueOf(speed.getInput());
            case 1:
                return String.valueOf(balance);
            default:
                return mode.getOptions()[(int) mode.getInput()];
        }
    }

    @Override
    public void onDisable() {
        reset();
    }

    enum BalanceState {
        NONE,
        WAITING,
        TIMER
    }
}
