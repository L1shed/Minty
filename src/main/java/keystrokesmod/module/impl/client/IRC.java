package keystrokesmod.module.impl.client;

import keystrokesmod.backend.IRCClient;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class IRC extends Module {
    public IRC() {
        super("IRC", category.client);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSendPacket(@NotNull SendPacketEvent event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            final String message = ((C01PacketChatMessage) event.getPacket()).getMessage();

            if (message.startsWith("%")) {
                event.setCanceled(true);
                IRCClient.send(message.substring(1));
            }
        }
    }
}
