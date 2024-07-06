package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class Criticals extends Module {
    public static int ticksSinceVelocity = Integer.MAX_VALUE;
    private final ModeSetting modes = new ModeSetting("Mode", new String[]{"Default", "NoGround"}, 1);

    public Criticals() {
        super("Criticals", category.combat);
        this.registerSetting(new DescriptionSetting("Makes you get a critical hit every time you attack."));
        this.registerSetting(modes);
    }

    @Override
    public void onEnable() {
        ticksSinceVelocity = Integer.MAX_VALUE;
    }

    @Override
    public void onUpdate() {
        if (ticksSinceVelocity < Integer.MAX_VALUE) ticksSinceVelocity++;
    }

    @SubscribeEvent
    public void onPacketReceive(@NotNull ReceivePacketEvent event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) event.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                ticksSinceVelocity = 0;
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        switch ((int) modes.getInput()) {
            case 0: // Default
                if (ticksSinceVelocity <= 18 && mc.thePlayer.fallDistance < 1.3) {
                    event.setOnGround(false);
                }
                break;
            case 1: // NoGround
                if (KillAura.target != null) {
                    event.setOnGround(false);
                } else {
                    event.setOnGround(true);
                }
                break;
        }
    }
}