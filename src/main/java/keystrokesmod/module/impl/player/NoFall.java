package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class NoFall extends Module {
    public final SliderSetting mode;
    private final SliderSetting minFallDistance;
    private final ButtonSetting ignoreVoid;
    private final String[] modes = new String[]{"Spoof", "Extra", "NoGround", "Blink"};

    // for blink noFall
    private boolean blinked = false;

    public NoFall() {
        super("NoFall", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(minFallDistance = new SliderSetting("Minimum fall distance", 3.0, 0.0, 8.0, 0.1));
        this.registerSetting(ignoreVoid = new ButtonSetting("Ignore void", true));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreMotion(PreMotionEvent e) {
        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }
        if ((double) mc.thePlayer.fallDistance > minFallDistance.getInput() || minFallDistance.getInput() == 0) {
            if ((int) mode.getInput() == 0) {
                e.setOnGround(true);
            }
        }
    }

    public void onUpdate() {
        if (ignoreVoid.isToggled() && isVoid()) {
            return;
        }
        if (mode.getInput() == 1 && (mc.thePlayer.fallDistance > minFallDistance.getInput()|| minFallDistance.getInput() == 0)) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
        }
    }

    @SubscribeEvent
    public void onPacketSend(@NotNull SendPacketEvent event) {
        try {
            if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                switch ((int) mode.getInput()) {
                    case 2:
                        Reflection.C03PacketPlayerOnGround.set(packet, false);
                        break;
                    case 3:
                        if (!packet.isOnGround() && mc.thePlayer.fallDistance >= minFallDistance.getInput()) {
                            if (!blinked) {
                                ModuleManager.blink.enable();
                                blinked = true;
                            }
                            Reflection.C03PacketPlayerOnGround.set(packet, true);
                        } else if (blinked) {
                            ModuleManager.blink.disable();
                            blinked = false;
                        }
                        break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            mc.thePlayer.addChatMessage(new ChatComponentText("Exception in console."));
        }
    }

    @Override
    public String getInfo() {
        return modes[(int) mode.getInput()];
    }

    private boolean isVoid() {
        return Utils.overVoid(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }
}
