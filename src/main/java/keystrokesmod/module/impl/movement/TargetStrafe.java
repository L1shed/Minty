package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PrePlayerInput;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.utility.RotationUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static keystrokesmod.module.ModuleManager.speed;

public class TargetStrafe extends Module {
    private final ButtonSetting onlySpeed;

    public static float getMovementYaw() {
        return movementYaw != null ? movementYaw : mc.thePlayer.rotationYaw;
    }

    private static Float movementYaw = null;

    public TargetStrafe() {
        super("TargetStrafe", category.movement);
        this.registerSetting(new DescriptionSetting("Strafes around the target."));
        this.registerSetting(onlySpeed = new ButtonSetting("Only Speed", true));
    }

    private float normalize(float yaw) {
        while (yaw > 180) {
            yaw -= 360;
        }
        while (yaw < -180) {
            yaw += 360;
        }
        return yaw;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPreInput(PrePlayerInput input) {
        if (KillAura.target == null || (onlySpeed.isToggled() && !speed.isEnabled())) {
            movementYaw = null;
            return;
        }

        float current = normalize(mc.thePlayer.rotationYaw);
        float toTarget = RotationUtils.getRotations(KillAura.target)[0];
        float diff = toTarget - current;

        if (diff == 0) return;
//        if (diff > -22.5 && diff < 22.5) {  // 0
//            input.setForward(1);
//            input.setStrafe(0);
//        } else if (diff > 22.5 && diff < 67.5) {  // 45
//            input.setForward(1);
//            input.setStrafe(1);
//        } else if (diff < -22.5 && diff > -67.5) {  // -45
//            input.setForward(1);
//            input.setStrafe(-1);
//        } else if (diff > 67.5 && diff < 112.5) {  // 90
//            input.setForward(0);
//            input.setStrafe(1);
//        } else if (diff < -67.5 && diff > -112.5) {  // -90
//            input.setForward(0);
//            input.setStrafe(-1);
//        } else if (diff > 112.5 && diff < 157.5) {  // 135
//            input.setForward(-1);
//            input.setStrafe(1);
//        } else if (diff < -112.5 && diff > -157.5) {  // -135
//            input.setForward(-1);
//            input.setStrafe(-1);
//        } else if (diff > 157.5 || diff < -157.5) {  // 180
//            input.setForward(-1);
//            input.setStrafe(0);
//        }

        input.setYaw(toTarget);
        movementYaw = toTarget;
    }

    @Override
    public void onDisable() {
        movementYaw = null;
    }
}
