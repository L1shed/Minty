package keystrokesmod.utility;

import keystrokesmod.event.PostUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BadPacketsHandler {
    private boolean C08;
    private boolean C07;
    private boolean C02;
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSendPacket(SendPacketEvent e) {
        if (e.isCanceled()) {
            return;
        }
        if (e.getPacket() instanceof C02PacketUseEntity) {
            if (C07) {
                e.setCanceled(true);
                return;
            }
            C02 = true;
        }
        else if (e.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08 = true;
        }
        else if (e.getPacket() instanceof C07PacketPlayerDigging) {
            C07 = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPostUpdate(PostUpdateEvent e) {
        C08 = C07 = C02 = false;
    }
}
