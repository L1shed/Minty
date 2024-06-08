package keystrokesmod.utility;

import keystrokesmod.module.impl.movement.TargetStrafe;
import net.minecraft.util.MathHelper;

import static keystrokesmod.Raven.mc;
import static keystrokesmod.utility.Utils.isMoving;

public class MoveUtil {
    /**
     * Makes the player strafe
     */
    public static void strafe() {
        strafe(speed());
    }

    /**
     * Makes the player strafe at the specified speed
     */
    public static void strafe(final double speed) {
        final double yaw = direction();
        mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    }

    public static void strafe(final double speed, float yaw) {
        yaw = (float) Math.toRadians(yaw);
        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    /**
     * Stops the player from moving
     */
    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

    /**
     * Gets the players' movement yaw
     */
    public static double direction() {
        float rotationYaw = TargetStrafe.getMovementYaw();

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    /**
     * Gets the players' movement yaw
     */
    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Used to get the players speed
     */
    public static double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }
}
