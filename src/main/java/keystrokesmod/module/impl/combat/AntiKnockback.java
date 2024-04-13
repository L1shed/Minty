package keystrokesmod.module.impl.combat;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiKnockback extends Module {
    private SliderSetting horizontal;
    private SliderSetting vertical;
    private ButtonSetting cancelExplosion;
    public AntiKnockback() {
        super("AntiKnockback", category.combat);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(vertical = new SliderSetting("Vertical", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion packet", true));
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                e.setCanceled(true);
                if (cancel()) {
                    return;
                }
                if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                    mc.thePlayer.motionY = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionY() / 8000) * vertical.getInput()/100;
                }
                else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                    mc.thePlayer.motionX = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionZ = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                else {
                    mc.thePlayer.motionX = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionY = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionY() / 8000) * vertical.getInput()/100;
                    mc.thePlayer.motionZ = ((double) ((S12PacketEntityVelocity) e.getPacket()).getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                e.setCanceled(true);
            }
        }
        else if (e.getPacket() instanceof S27PacketExplosion) {
            e.setCanceled(true);
            if (cancelExplosion.isToggled() || cancel()) {
                return;
            }
            if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                mc.thePlayer.motionY = ((S27PacketExplosion) e.getPacket()).func_149144_d() * vertical.getInput()/100;
            }
            else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                mc.thePlayer.motionX = ((S27PacketExplosion) e.getPacket()).func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionZ = ((S27PacketExplosion) e.getPacket()).func_149147_e() * horizontal.getInput()/100;
            }
            else {
                mc.thePlayer.motionX = ((S27PacketExplosion) e.getPacket()).func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionY = ((S27PacketExplosion) e.getPacket()).func_149144_d() * vertical.getInput()/100;
                mc.thePlayer.motionZ = ((S27PacketExplosion) e.getPacket()).func_149147_e() * horizontal.getInput()/100;
            }
            e.setCanceled(true);
        }
    }

    private boolean cancel() {
        return vertical.getInput() == 0 && horizontal.getInput() == 0;
    }

    @Override
    public String getInfo() {
        return (horizontal.getInput() == 100 ? "" : (int) horizontal.getInput() + "h") + (horizontal.getInput() != 100 ? " " : "") + (vertical.getInput() == 100 ? "" : (int) vertical.getInput() + "v");
    }
}
