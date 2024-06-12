package keystrokesmod.module.impl.other;

import keystrokesmod.Raven;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class AutoPlay extends Module {
    private final SliderSetting mode;
    private final SliderSetting delay;
    private static final String winMessage = "You won! Want to play again? Click here!";
    private static final String loseMessage = "You died! Want to play again? Click here!";

    public AutoPlay() {
        super("AutoPlay", category.other);
        this.registerSetting(new DescriptionSetting("Auto take you to next game."));
        this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Solo Normal", "Solo Insane"}, 0));
        this.registerSetting(delay = new SliderSetting("Delay", 1500, 0, 4000, 50, "ms"));
    }

    @SubscribeEvent
    public void onReceive(@NotNull ReceivePacketEvent event) {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)event.getPacket();
            String message = packet.getChatComponent().getUnformattedText();
            if (message.contains(winMessage) && message.length() < message.length() + 3
                    || message.contains(loseMessage) && message.length() < loseMessage.length() + 3) {
                Utils.sendModuleMessage(this, "Sending you to a new game.");

                Raven.getExecutor().schedule(() -> {
                    if (!ModuleManager.autoPlay.isEnabled()) return;

                    String command = "";
                    switch ((int) this.mode.getInput()) {
                        case 0:
                            command = "/play solo_normal";
                            break;
                        case 1:
                            command = "/play solo_insane";
                            break;
                    }
                    mc.thePlayer.sendChatMessage(command);
                }, (long) delay.getInput(), TimeUnit.MILLISECONDS);
            }
        }

    }
}
