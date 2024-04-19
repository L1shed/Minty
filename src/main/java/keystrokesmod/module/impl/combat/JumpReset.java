package keystrokesmod.module.impl.combat;

import keystrokesmod.event.JumpEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class JumpReset extends Module {
    private SliderSetting chance;
    private SliderSetting motion;
    private boolean jump;

    public JumpReset() {
        super("Jump Reset", category.combat);
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1));
        this.registerSetting(motion = new SliderSetting("Jump motion", 0.42, 0, 1, 0.01));
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity && Utils.nullCheck()) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                if (chance.getInput() != 100.0D) {
                    double ch = Math.random();
                    if (ch >= chance.getInput() / 100.0D) {
                        return;
                    }
                }
                jump = true;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
            }
        }
    }

    @SubscribeEvent
    public void onJump(JumpEvent e) {
        if (!Utils.nullCheck() || !jump) {
            return;
        }
        e.setMotionY((float) motion.getInput());
        jump = false;
    }
}
