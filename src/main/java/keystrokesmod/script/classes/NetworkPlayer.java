package keystrokesmod.script.classes;

import net.minecraft.client.network.NetworkPlayerInfo;

public class NetworkPlayer {
    private NetworkPlayerInfo networkPlayerInfo;
    protected NetworkPlayer(NetworkPlayerInfo networkPlayerInfo) {
        this.networkPlayerInfo = networkPlayerInfo;
    }

    public String getCape() {
        return networkPlayerInfo.getLocationCape().getResourcePath();
    }

    public String getDisplayName() {
        return networkPlayerInfo.getDisplayName().getFormattedText();
    }

    public String getName() {
        return networkPlayerInfo.getGameProfile().getName();
    }

    public int getPing() {
        return networkPlayerInfo.getResponseTime();
    }

    public String getSkinData() {
        return networkPlayerInfo.getLocationSkin().getResourcePath();
    }

    public String getUUID() {
        return networkPlayerInfo.getGameProfile().getId().toString();
    }
}
