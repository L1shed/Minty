package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.RotationHandler;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", new String[]{"Cancel", "Silent"}, 0);

    public NoRotate() {
        super("NoRotate", category.player);
        this.registerSetting(mode);
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent event) { // from croat
        if (!Utils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            switch ((int) mode.getInput()) {
                case 1:
                    RotationHandler.setRotationYaw(packet.getYaw());
                    RotationHandler.setRotationPitch(packet.getPitch());
                case 0:
                    try {
                        Reflection.S08PacketPlayerPosLookYaw.set(packet, mc.thePlayer.rotationYaw);
                        Reflection.S08PacketPlayerPosLookPitch.set(packet, mc.thePlayer.rotationPitch);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sendModuleMessage(this, "&cFailed to modify S08PacketPlayerPosLookPitch. Relaunch your game.");
                    }
                    break;
            }
        }
    }
}
