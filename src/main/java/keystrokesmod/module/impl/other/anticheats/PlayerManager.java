package keystrokesmod.module.impl.other.anticheats;

import keystrokesmod.module.impl.other.Anticheat;
import keystrokesmod.module.impl.other.anticheats.utils.alert.LogUtils;
import keystrokesmod.module.impl.other.anticheats.utils.world.LevelUtils;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    public HashMap<UUID, Boolean> activeMap;  // 实时活动玩家(可被检查)列表

    public HashMap<UUID, TRPlayer> dataMap;  // 玩家数据列表

    public PlayerManager() {
        activeMap = new HashMap<>();
        dataMap = new HashMap<>();
    }

    public void update(@NotNull Minecraft client) {
        if (client.theWorld == null || client.thePlayer == null) return;
        activeMap.forEach((uuid, aBoolean) -> activeMap.replace(uuid, false));

        // 遍历活动玩家
        try {
            for (AbstractClientPlayer player : LevelUtils.getPlayers()) {
                final UUID uuid = player.getUniqueID();
                if (AntiBot.isBot(player)) {
                    activeMap.remove(uuid);
                    continue;
                }
                if (!Anticheat.getCheckForTeammates().isToggled() && Utils.isTeamMate(player)) {
                    activeMap.remove(uuid);
                    continue;
                }

                if (!activeMap.containsKey(uuid)) {
                    final TRPlayer trPlayer;
                    if (client.thePlayer.equals(player)) {
                        trPlayer = new TRSelf(client.thePlayer);
                    } else {
                        trPlayer = TRPlayer.create(player);
                    }
                    activeMap.put(uuid, true);
                    dataMap.put(uuid, trPlayer);
                }

                // 更新
                activeMap.replace(uuid, true);
                try {
                    dataMap.get(uuid).update(player);
                } catch (Exception e) {
                    LogUtils.custom(Arrays.toString(e.getStackTrace()));
                    LogUtils.LOGGER.warning(String.format("遇到了异常，丢弃玩家 %s 数据。", player.getName()) + e.getLocalizedMessage());
                    activeMap.remove(uuid);
                }
            }
        } catch (ConcurrentModificationException e) {
            LogUtils.custom(e.getLocalizedMessage());
        }
    }
}
