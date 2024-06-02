package keystrokesmod.module.impl.other.anticheats.checks.aim;

import keystrokesmod.module.impl.other.Anticheat;
import keystrokesmod.module.impl.other.anticheats.Check;
import keystrokesmod.module.impl.other.anticheats.TRPlayer;
import keystrokesmod.module.impl.other.anticheats.config.AdvancedConfig;
import org.jetbrains.annotations.NotNull;

public class InvalidPitch extends Check {
    public InvalidPitch(@NotNull TRPlayer player) {
        super("InvalidPitch", player);
    }

    @Override
    public void _onTick() {
        if (player.currentRot.x > 90 || player.currentRot.x < -90) {
            flag(String.format("pitch: %.2f",player.currentRot.x));
        }
    }

    @Override
    public int getAlertBuffer() {
        return AdvancedConfig.invalidPitchAlertBuffer;
    }

    @Override
    public boolean isDisabled() {
        return !Anticheat.getAimCheck().isToggled();
    }
}
