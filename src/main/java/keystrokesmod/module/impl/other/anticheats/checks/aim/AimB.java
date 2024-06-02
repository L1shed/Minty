package keystrokesmod.module.impl.other.anticheats.checks.aim;

import keystrokesmod.module.impl.other.Anticheat;
import keystrokesmod.module.impl.other.anticheats.Check;
import keystrokesmod.module.impl.other.anticheats.TRPlayer;
import keystrokesmod.module.impl.other.anticheats.config.AdvancedConfig;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class AimB extends Check {
    public static final Set<Integer> STEP = new HashSet<>();
    public AimB(@NotNull TRPlayer player) {
        super("AimB", player);
        STEP.add(25);
        STEP.add(40);
        STEP.add(45);
        STEP.add(60);
        STEP.add(90);
        STEP.add(120);
        STEP.add(135);
        STEP.add(180);
    }

    @Override
    public void _onTick() {
        boolean flagPitch = false;
        boolean flagYaw = false;
        float stepPitch = 0, stepYaw = 0;
        for (int step : STEP) {
            if (Math.abs(Math.abs(player.lastRot.x - player.currentRot.x) - step) < AdvancedConfig.aimBMinDiffPitch) {
                flagPitch = true;
                stepPitch = player.lastRot.x - player.currentRot.x;
            }
            if (Math.abs(Math.abs(player.lastRot.y - player.currentRot.y) - step) < AdvancedConfig.aimBMinDiffYaw) {
                flagYaw = true;
                stepYaw = player.lastRot.y - player.currentRot.y;
            }
            if (flagPitch && flagYaw) break;
        }

        if (flagPitch && flagYaw) {
            flag(String.format("perfect step aim. deltaYaw: %.1f  deltaPitch: %.1f", stepYaw, stepPitch));
        } else if (flagPitch) {
            flag(String.format("perfect pitch step aim. deltaPitch: %.1f", stepPitch));
        } else if (flagYaw) {
            flag(String.format("perfect pitch step aim. deltaYaw: %.1f", stepYaw));
        }
    }

    @Override
    public int getAlertBuffer() {
        return AdvancedConfig.aimBAlertBuffer;
    }

    @Override
    public boolean isDisabled() {
        return !Anticheat.getAimCheck().isToggled() || !Anticheat.getExperimentalMode().isToggled();
    }
}
