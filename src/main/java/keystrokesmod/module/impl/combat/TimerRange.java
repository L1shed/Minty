package keystrokesmod.module.impl.combat;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TimerRange extends Module {
    private final SliderSetting lagTicks;
    private final SliderSetting timerTicks;
    private final SliderSetting minRange;
    private final SliderSetting maxRange;
    private final SliderSetting delay;

    private int hasLag = 0;
    private long lastTimerTime = 0;
    private long lastLagTime = 0;
    public TimerRange() {
        super("TimerRange", category.combat);
        this.registerSetting(new DescriptionSetting("Use timer help you to beat opponent."));
        this.registerSetting(new DescriptionSetting("Only work with KillAura."));
        this.registerSetting(lagTicks = new SliderSetting("Lag ticks", 2, 0, 10, 1));
        this.registerSetting(timerTicks = new SliderSetting("Timer ticks", 2, 0, 10, 1));
        this.registerSetting(minRange = new SliderSetting("Min range", 3.6, 0, 8, 0.1));
        this.registerSetting(maxRange = new SliderSetting("Max range", 5, 0, 8, 0.1));
        this.registerSetting(delay = new SliderSetting("Delay", 500, 0, 4000, 1));
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent e) {
        if (!shouldStart()) return;

        if (hasLag < lagTicks.getInput()) {
            Utils.getTimer().timerSpeed = 0.0F;
            if (System.currentTimeMillis() - lastLagTime >= 50) {
                hasLag++;
                lastLagTime = System.currentTimeMillis();
            }
            return;
        }

        Utils.getTimer().timerSpeed = 1.0F;
        for (int i = 0; i < timerTicks.getInput(); i++) {
            mc.thePlayer.onUpdate();
        }

        hasLag = 0;
        lastTimerTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        lastTimerTime = 0;
        lastLagTime = 0;
        hasLag = 0;
    }

    private boolean shouldStart() {
        if (System.currentTimeMillis() - lastTimerTime < delay.getInput()) return false;
        if (KillAura.target == null) return false;
        double distance = new Vec3(KillAura.target).distanceTo(mc.thePlayer);
        return distance >= minRange.getInput() && distance <= maxRange.getInput();
    }

    @Override
    public String getInfo() {
        return String.valueOf((int) timerTicks.getInput());
    }
}
