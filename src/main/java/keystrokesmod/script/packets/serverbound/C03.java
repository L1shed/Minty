package keystrokesmod.script.packets.serverbound;

import keystrokesmod.script.classes.Vec3;
import net.minecraft.network.play.client.C03PacketPlayer;

public class C03 extends CPacket {
    public Vec3 pos;
    public float yaw;
    public float pitch;
    public boolean ground;
    private int mode;

    public C03(boolean ground) {
        super(null);
        this.ground = ground;
        this.mode = 0;
    }

    public C03(Vec3 pos, boolean ground) {
        super(null);
        this.pos = pos;
        this.ground = ground;
        this.mode = 1;
    }

    public C03(float yaw, float pitch, boolean ground) {
        super(null);
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.mode = 2;
    }

    public C03(Vec3 pos, float yaw, float pitch, boolean ground) {
        super(null);
        this.pos = pos;
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
        this.mode = 3;
    }

    protected C03(C03PacketPlayer packet, String filler, String filler2, String filler3, String filler4, String filler5) {
        super(packet);
        this.pos = new Vec3(packet.getPositionX(), packet.getPositionY(), packet.getPositionZ());
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
        this.ground = packet.isOnGround();
    }

    @Override
    public C03PacketPlayer convert() {
        if (this.mode == 3) {
            return new C03PacketPlayer.C06PacketPlayerPosLook(this.pos.x, this.pos.y, this.pos.z, this.yaw, this.pitch, this.ground);
        }
        else if (this.mode == 2) {
            return new C03PacketPlayer.C05PacketPlayerLook(this.yaw, this.pitch, this.ground);
        }
        else if (this.mode == 1) {
            return new C03PacketPlayer.C04PacketPlayerPosition(this.pos.x, this.pos.y, this.pos.z, this.ground);
        }
        else if (this.mode == 0) {
            return new C03PacketPlayer(this.ground);
        }
        return null;
    }
}
