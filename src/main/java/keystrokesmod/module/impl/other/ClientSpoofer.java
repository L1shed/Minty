package keystrokesmod.module.impl.other;

import io.netty.buffer.Unpooled;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.mixins.impl.network.C17PacketCustomPayloadAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ClientSpoofer extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", SpoofMode.getNames(), 0);
    public final ButtonSetting cancelForgePacket = new ButtonSetting("Cancel Forge packet", false);

    public ClientSpoofer() {
        super("ClientSpoofer", category.other);
        this.registerSetting(mode, cancelForgePacket);
    }

    @SubscribeEvent
    public void onSendPacket(@NotNull SendPacketEvent event) {
        if (event.getPacket() instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload packet = (C17PacketCustomPayload) event.getPacket();


            String brand = SpoofMode.values()[(int) mode.getInput()].brand;
            if (brand == null)
                event.setCanceled(true);
            else
                ((C17PacketCustomPayloadAccessor) packet).setData(createPacketBuffer(brand));
        } else if (event.getPacket() instanceof FMLProxyPacket && cancelForgePacket.isToggled()) {
            event.setCanceled(true);
        }
    }

    enum SpoofMode {
        VANILLA("Vanilla", "vanilla"),
        LUNAR("Lunar", "lunarclient:v2.12.3-2351"),
        CHEATBREAKER("Cheatbreaker", "CB"),
        CANCEL("Cancel", null);

        public final String name;
        public final String brand;

        SpoofMode(String name, String brand) {
            this.name = name;
            this.brand = brand;
        }

        public static String @NotNull [] getNames() {
            final String[] result = new String[values().length];

            for (int i = 0; i < values().length; i++) {
                result[i] = values()[i].name;
            }

            return result;
        }
    }

    @Contract("_ -> new")
    @SuppressWarnings("All")
    private @NotNull PacketBuffer createPacketBuffer(final @NotNull String data) {
        return new PacketBuffer(Unpooled.buffer()).writeString(data);
    }
}
