package keystrokesmod.module.impl.other.anticheats.checks.aim;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.impl.other.Anticheat;
import keystrokesmod.module.impl.other.anticheats.Check;
import keystrokesmod.module.impl.other.anticheats.TRPlayer;
import keystrokesmod.module.impl.other.anticheats.config.AdvancedConfig;
import keystrokesmod.module.impl.world.Scaffold;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * so this is what Raven Bs scaffold do.
 * <p>
 * "自相矛盾" said like this
 * @see Scaffold#getYaw()
 * @see Scaffold#onPreMotion(PreMotionEvent)
 */
public class AimC extends Check {
    private static final Set<Float> SCAFFOLD_YAW = new HashSet<>();
    public AimC(@NotNull TRPlayer player) {
        super("AimC", player);
        SCAFFOLD_YAW.add(180f);
        SCAFFOLD_YAW.add(90f);
        SCAFFOLD_YAW.add(-90f);
        SCAFFOLD_YAW.add(135f);
        SCAFFOLD_YAW.add(-135f);
        SCAFFOLD_YAW.add(0f);
        SCAFFOLD_YAW.add(45f);
        SCAFFOLD_YAW.add(-45f);
    }

    @Override
    public void _onTick() {
        float deltaYaw = player.currentRot.y - player.lastRot.y;
        float deltaPitch = player.currentRot.x - player.lastRot.x;

        if (player.currentRot.x == 85) {
            if (SCAFFOLD_YAW.contains(player.currentRot.y)) {
                if (SCAFFOLD_YAW.contains(player.lastRot.y)) {
                    flag("Scaffold-like rotation. Type: RotateWithMovement");
                } else {
                    if (deltaYaw < AdvancedConfig.aimCMinDeltaYaw || deltaPitch < AdvancedConfig.aimCMinDeltaPitch) return;
                    flag("Scaffold-like rotation. Type: StartScaffolding");
                }
            }
        }
    }

    @Override
    public int getAlertBuffer() {
        return AdvancedConfig.aimCAlertBuffer;
    }

    @Override
    public boolean isDisabled() {
        return !Anticheat.getAimCheck().isToggled();
    }
}
