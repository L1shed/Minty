package keystrokesmod.utility;

import keystrokesmod.Raven;
import net.minecraft.network.Packet;

import java.util.ArrayList;
import java.util.List;

public class PacketUtils {
    public static List<Packet> skipEvent = new ArrayList<>();

    public static void sendPacketNoEvent(Packet packet) {
        skipEvent.add(packet);
        Raven.mc.getNetHandler().addToSendQueue(packet);
    }
}
