package keystrokesmod.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RotationEvent extends Event {
    private float yaw;
    private float pitch;
    private boolean isSet;

    public RotationEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        isSet = true;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        isSet = true;
    }

    public boolean isSet() {
        return isSet;
    }
}
