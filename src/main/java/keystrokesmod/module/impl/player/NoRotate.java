package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", category.player);
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent event) { // from croat
        if (!Utils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            try {
                Reflection.S08PacketPlayerPosLookYaw.set(packet, mc.thePlayer.rotationYaw);
                Reflection.S08PacketPlayerPosLookPitch.set(packet, mc.thePlayer.rotationPitch);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendModuleMessage(this, "&cFailed to modify S08PacketPlayerPosLookPitch. Relaunch your game.");
            }
        }
    }
}
