package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.PostVelocityEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class JumpReset extends Module {
    private final SliderSetting minDelay;
    private final SliderSetting maxDelay;
    private final SliderSetting chance;
    private final ButtonSetting targetNearbyCheck;
    private final ButtonSetting ignoreLiquid;

    public JumpReset() {
        super("Jump Reset", category.combat);
        this.registerSetting(minDelay = new SliderSetting("Min delay", 0, 0, 150, 1, "ms"));
        this.registerSetting(maxDelay = new SliderSetting("Max delay", 0, 0, 150, 1, "ms"));
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1, "%"));
        this.registerSetting(targetNearbyCheck = new ButtonSetting("Target nearby check", false));
        this.registerSetting(ignoreLiquid = new ButtonSetting("Ignore liquid", true));
    }

    @SubscribeEvent
    public void onVelocity(PostVelocityEvent event) {
        if (Utils.nullCheck()) {
            if (chance.getInput() == 0)
                return;
            if (mc.thePlayer.maxHurtTime <= 0)
                return;
            if (ignoreLiquid.isToggled() && Utils.inLiquid())
                return;
            if (targetNearbyCheck.isToggled() && !Utils.isTargetNearby())
                return;

            if (chance.getInput() != 100) {
                double ch = Math.random();
                if (ch >= chance.getInput() / 100)
                    return;
            }

            long delay = (long) (Math.random() * (maxDelay.getInput() - minDelay.getInput()) + minDelay.getInput());
            if (delay == 0) {
                if (mc.thePlayer.onGround) mc.thePlayer.jump();
            } else {
                Raven.getExecutor().schedule(() -> {
                    if (mc.thePlayer.onGround) mc.thePlayer.jump();
                }, delay, TimeUnit.MILLISECONDS);
            }
        }
    }
}
