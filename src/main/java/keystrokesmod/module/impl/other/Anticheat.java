package keystrokesmod.module.impl.other;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.anticheats.PlayerManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author xia__mc
 * @see <a href="https://github.com/Nova-Committee/CheatDetector">CheatDetector Github</a>
 */
public class Anticheat extends Module {
    private static SliderSetting latency;
    private static SliderSetting threshold;
    private static ButtonSetting disableInLobby;
    private static ButtonSetting checkForSelf;
    private static ButtonSetting checkForTeammates;
    private static SliderSetting vlClearTime;
    private static ButtonSetting noAlertBuffer;
    private static ButtonSetting shouldPing;
    private static ModeSetting autoReport;
    private static ButtonSetting experimentalMode;
    private static ButtonSetting aimCheck;
    private static ButtonSetting combatCheck;
    private static ButtonSetting movementCheck;
    private static ButtonSetting scaffoldingCheck;

    private PlayerManager manager = new PlayerManager();
    public Anticheat() {
        super("Anticheat", category.other);
        this.registerSetting(new DescriptionSetting("Tries to detect cheaters."));
        this.registerSetting(latency = new SliderSetting("Latency compensation", 600.0, 0.0, 1000.0, 1.0, "ms"));
        this.registerSetting(threshold = new SliderSetting("Movement threshold", 1.0, 0.0, 3.0, 0.01, "blocks"));
        this.registerSetting(disableInLobby = new ButtonSetting("Disable in lobby", true));
        this.registerSetting(checkForSelf = new ButtonSetting("Check for self", true));
        this.registerSetting(checkForTeammates = new ButtonSetting("Check for teammates", true));
        this.registerSetting(vlClearTime = new SliderSetting("VL clear time", 6000, -1, 12000, 1, "ticks"));
        this.registerSetting(noAlertBuffer = new ButtonSetting("Remove alert buffer", false));
        this.registerSetting(shouldPing = new ButtonSetting("Should ping", true));
        this.registerSetting(autoReport = new ModeSetting("Auto report", new String[]{"None", "/wdr", "/report"}, 0));
        this.registerSetting(experimentalMode = new ButtonSetting("Experimental mode", true));
        this.registerSetting(aimCheck = new ButtonSetting("Aim checks", true));
        this.registerSetting(combatCheck = new ButtonSetting("Combat checks", true));
        this.registerSetting(movementCheck = new ButtonSetting("Movement checks", true));
        this.registerSetting(scaffoldingCheck = new ButtonSetting("Scaffolding checks", true));
    }

    public static SliderSetting getLatency() {
        return latency;
    }

    public static SliderSetting getThreshold() {
        return threshold;
    }

    public static ButtonSetting getDisableInLobby() {
        return disableInLobby;
    }

    public static ButtonSetting getCheckForSelf() {
        return checkForSelf;
    }

    public static ButtonSetting getCheckForTeammates() {
        return checkForTeammates;
    }

    public static SliderSetting getVlClearTime() {
        return vlClearTime;
    }

    public static ButtonSetting getNoAlertBuffer() {
        return noAlertBuffer;
    }

    public static ButtonSetting getShouldPing() {
        return shouldPing;
    }

    public static ModeSetting getAutoReport() {
        return autoReport;
    }

    public static ButtonSetting getExperimentalMode() {
        return experimentalMode;
    }

    public static ButtonSetting getAimCheck() {
        return aimCheck;
    }

    public static ButtonSetting getCombatCheck() {
        return combatCheck;
    }

    public static ButtonSetting getMovementCheck() {
        return movementCheck;
    }

    public static ButtonSetting getScaffoldingCheck() {
        return scaffoldingCheck;
    }

    public void onUpdate() {
        if (mc.isSingleplayer()) {
            return;
        }

        manager.update(Raven.mc);
    }

    @SubscribeEvent
    public void onEntityJoin(@NotNull EntityJoinWorldEvent e) {
        if (e.entity == mc.thePlayer) {
            manager = null;
            manager = new PlayerManager();
        }
    }

    public void onDisable() {
        manager = null;
        manager = new PlayerManager();
    }
}
