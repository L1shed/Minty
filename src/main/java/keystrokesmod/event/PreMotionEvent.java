package keystrokesmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PreMotionEvent extends Event {
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;
    private boolean onGround;
    private static boolean setRenderYaw;

    public PreMotionEvent(double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        this.setRenderYaw = true;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public static boolean isSetRenderYaw() {
        return setRenderYaw;
    }

    public void setRenderYaw(boolean setRenderYaw) {
        this.setRenderYaw = setRenderYaw;
    }
}
