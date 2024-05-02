package keystrokesmod.script.packets.serverbound;

import net.minecraft.network.Packet;

public class CPacket {
    public String name;
    protected net.minecraft.network.Packet packet;

    public CPacket(net.minecraft.network.Packet packet) {
        this.packet = packet;
        if (packet == null) {
            return;
        }
        this.name = packet.getClass().getSimpleName();
    }

    public Packet convert() {
        return packet;
    }
}
