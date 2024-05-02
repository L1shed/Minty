package keystrokesmod.script.packets.clientbound;

public class SPacket {
    public String a;
    protected net.minecraft.network.Packet b;

    public SPacket(net.minecraft.network.Packet b) {
        this.b = b;
        this.a = b.getClass().getSimpleName();
    }
}
