package keystrokesmod.module.impl.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.Raven;
import keystrokesmod.event.PreConnectEvent;
import keystrokesmod.mixins.impl.client.GuiConnectingAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.GuiConnectingMsg;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

public class NyaProxy extends Module {
    private final SliderSetting maxRetryTimes;
    private GuiConnectingMsg.Data guiData = new GuiConnectingMsg.Data();
    private short retry = 0;

    public NyaProxy() {
        super("NyaProxy", category.client);
        this.registerSetting(new DescriptionSetting("work with NyaProxy server."));
        this.registerSetting(maxRetryTimes = new SliderSetting("Max retry times", 20, 0, 50, 1));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onConnect(@NotNull PreConnectEvent event) {
        NYA_BACKENDS backend;
        try {
            backend = isNyaProxy(event.getIp()).orElseThrow(RuntimeException::new);
        } catch (RuntimeException e) {
            return;
        }

        event.setCanceled(true);
        retry = 0;
        guiData = new GuiConnectingMsg.Data();

        guiData.add(ChatFormatting.GRAY + "[" + ChatFormatting.AQUA + "NyaProxy" + ChatFormatting.GRAY + "] " + ChatFormatting.BLUE);
        switch (backend) {
            case SUQIAN1:
                guiData.append("宿迁节点1");
                break;
            case SUQIAN2:
                guiData.append("宿迁节点2");
                break;
            case SHAOXING:
                guiData.append("绍兴节点");
                break;
        }

        event.getExtraMessage().update(guiData);
        Raven.getExecutor().execute(() -> connectWithRetry(event));
    }

    private void connectWithRetry(@NotNull PreConnectEvent event) {
        GuiConnectingAccessor guiConnecting = (GuiConnectingAccessor) event.getScreen();

        InetAddress inetaddress = null;

        try {
            if (guiConnecting.isCancel()) {
                return;
            }

            inetaddress = InetAddress.getByName(event.getIp());
            guiConnecting.setNetworkManager(NetworkManager.func_181124_a(inetaddress, event.getPort(), mc.gameSettings.func_181148_f()));
            guiConnecting.getNetworkManager().setNetHandler(new NetHandlerLoginClient(guiConnecting.getNetworkManager(), mc, guiConnecting.getPreviousGuiScreen()));
            guiConnecting.getNetworkManager().sendPacket(new C00Handshake(47, event.getIp(), event.getPort(), EnumConnectionState.LOGIN, true));
            guiConnecting.getNetworkManager().sendPacket(new C00PacketLoginStart(mc.getSession().getProfile()));
        } catch (UnknownHostException var5) {
            if (guiConnecting.isCancel()) {
                return;
            }

            guiConnecting.getLogger().error("Couldn't connect to server", var5);
            mc.displayGuiScreen(new GuiDisconnected(guiConnecting.getPreviousGuiScreen(), "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")));
        } catch (Exception var6) {
            if (guiConnecting.isCancel()) {
                return;
            }

            guiConnecting.getLogger().error("Couldn't connect to server", var6);
            String s = var6.toString();
            if (inetaddress != null) {
                String s1 = inetaddress + ":" + event.getPort();
                s = s.replaceAll(s1, "");
            }

            if (retry >= maxRetryTimes.getInput()) {
                guiData = null;
                retry = 0;
                mc.displayGuiScreen(new GuiDisconnected(guiConnecting.getPreviousGuiScreen(), "connect.failed", new ChatComponentTranslation("disconnect.genericReason", s)));
            } else {
                guiData.add("第" + (retry + 1) + "次失败：" + var6);
                event.getExtraMessage().update(guiData);

                retry++;
                connectWithRetry(event);
            }
        }
    }

    public static Optional<NYA_BACKENDS> isNyaProxy(@NotNull String ipAddress) {
        final String formattedIp = ipAddress.split(":")[0];

        for (NYA_BACKENDS backend : NYA_BACKENDS.values()) {
            if (formattedIp.endsWith(backend.getIp())) {
                return Optional.of(backend);
            }
        }
        return Optional.empty();
    }

    public enum NYA_BACKENDS {
        SUQIAN1(".suqian1.connect.hoursly.nyaproxy.xyz"),
        SUQIAN2(".suqian2.connect.hoursly.nyaproxy.xyz"),
        SHAOXING(".shaoxing.connect.hoursly.nyaproxy.xyz");

        final String ip;

        NYA_BACKENDS(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }
    }
}
