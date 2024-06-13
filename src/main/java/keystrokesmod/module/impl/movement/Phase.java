package keystrokesmod.module.impl.movement;

import keystrokesmod.event.BlockAABBEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import static keystrokesmod.module.ModuleManager.blink;

public class Phase extends Module {
    private final SliderSetting mode;

    // watchdog auto phase
    private int phaseTime;
    private boolean phase;

    public Phase() {
        super("Phase", category.movement);
        this.registerSetting(new DescriptionSetting("Lets you go through solid blocks."));
        this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Watchdog Auto Phase"}, 0));
    }

    @Override
    public void onEnable() {
        phase = false;
        phaseTime = 0;
    }

    @Override
    public void onDisable() {
        if ((int) mode.getInput() == 0) {
            blink.disable();
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        if ((int) mode.getInput() == 0) {
            if (this.phase) {
                this.phaseTime++;
            } else {
                this.phaseTime = 0;
            }

            if (this.phase) {
                blink.enable();
            }
        }
    }

    @SubscribeEvent
    public void onBlockAABB(BlockAABBEvent event) {
        if ((int) mode.getInput() == 0) {
            if (this.phase && this.phaseTime <= 5) {
                event.setBoundingBox(null);
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(@NotNull WorldEvent.Load event) {
        this.phase = false;
        this.phaseTime = 0;
    }

    @SubscribeEvent
    public void onReceivePacket(@NotNull ReceivePacketEvent event) {
        if ((int) mode.getInput() == 0) {
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat) event.getPacket();
                String chat = packet.getChatComponent().getUnformattedText();

                if (chat.contains(" 2 ") && chat.contains("game")) {
                    this.phase = true;
                } else if (chat.contains("FIGHT") && chat.contains("Cages")) {
                    this.phase = false;
                    blink.disable();
                }
            }
        }
    }
}
