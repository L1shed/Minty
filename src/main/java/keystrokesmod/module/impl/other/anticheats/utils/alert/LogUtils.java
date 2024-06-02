package keystrokesmod.module.impl.other.anticheats.utils.alert;

import keystrokesmod.Raven;
import keystrokesmod.module.impl.other.Anticheat;
import keystrokesmod.utility.Utils;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.logging.Logger;

public class LogUtils {
    public static final Logger LOGGER = Logger.getLogger("AntiCheat");

    public static void alert(String player, String module, String extraMsg) {
        if (check()) {
            final ChatComponentText chatComponentText = new ChatComponentText(String.format("%s§r §r%s§r %s§r %s§r | %s§r", "§b§lTR§r§l>", player, "failed", module, extraMsg));
            final ChatStyle chatStyle = new ChatStyle();
            chatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wdr " + player));
            ((IChatComponent)chatComponentText).appendSibling(new ChatComponentText(Utils.formatColor(" §7[§cWDR§7]")).setChatStyle(chatStyle));
            Raven.mc.thePlayer.addChatMessage(chatComponentText);
            if (Anticheat.getShouldPing().isToggled()) {
                Raven.mc.thePlayer.playSound("note.pling", 1.0f, 1.0f);
            }
            if (Anticheat.getAutoReport().isToggled()) {
                Raven.mc.thePlayer.sendChatMessage("/wdr " + Utils.stripColor(player));
            }
        }
    }

    public static void prefix(String prefix, String msg) {
        if (check()) {
            Raven.mc.thePlayer.addChatMessage(new ChatComponentText(
                    String.format("%s§r §r%s§r | %s§r", "§b§lTR§r§l>", prefix, msg)
            ));
        }
    }

    public static void custom(String msg) {
        if (check()) {
            Raven.mc.thePlayer.addChatMessage(new ChatComponentText(
                    String.format("%s§r §r%s§r", "§b§lTR§r§l>", msg)
            ));
        }


    }

    private static boolean check() {
        return Raven.mc.thePlayer != null;
    }
}
