package keystrokesmod.module.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import keystrokesmod.Raven;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.Utils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Backtrack extends Module {
    private final SliderSetting latency;
    private final SliderSetting delayBetweenBacktracks;
    private final SliderSetting startRange;
    private final SliderSetting stopRange;
    private final SliderSetting stopOnHurtTime;
    private final ButtonSetting onlyIfTargetGoesAway;
    private final ButtonSetting ignoreTransaction;

    private AbstractClientPlayer target = null;
    private long startTime = -1;
    private long stopTime = -1;
    private final Map<Packet<?>, Long> unReceivePackets = new ConcurrentHashMap<>();
    private final List<Packet<?>> skipPackets = new ArrayList<>();

    public Backtrack() {
        super("Backtrack", category.player);
        this.registerSetting(new DescriptionSetting("Allows you to hit past opponents."));
        this.registerSetting(latency = new SliderSetting("Latency", 50, 0, 500, 1));
        this.registerSetting(delayBetweenBacktracks = new SliderSetting("Delay between backtracks", 200, 0, 1000, 10));
        this.registerSetting(startRange = new SliderSetting("Start range", 6, 1, 6, 0.1));
        this.registerSetting(stopRange = new SliderSetting("Stop range", 3, 0, 6, 0.1));
        this.registerSetting(stopOnHurtTime = new SliderSetting("Stop on HurtTime", -1, -1, 10, 1));
        this.registerSetting(onlyIfTargetGoesAway = new ButtonSetting("Only if target goes away", true));
        this.registerSetting(ignoreTransaction = new ButtonSetting("Ignore transaction", false));
    }

    @Override
    public void onEnable() {
        Utils.sendMessage(ChatFormatting.RED + this.getName() + " still under development! It may result in BANNED.");
    }

    @Override
    public void onDisable() {
        receivePacket(false);
        target = null;
        startTime = -1;
        stopTime = -1;
        unReceivePackets.clear();
        skipPackets.clear();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAttack(@NotNull AttackEntityEvent event) {
        if (event.target instanceof AbstractClientPlayer) {
            target = (AbstractClientPlayer) event.target;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRender(TickEvent.RenderTickEvent event) {
        receivePacket(true);
    }

    @Override
    public void onUpdate() {
        if (target != null && stopOnHurtTime.getInput() != -1) {
            if (target.hurtTime == stopOnHurtTime.getInput())
                receivePacket(false);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onReceivePacket(@NotNull ReceivePacketEvent event) {
        if (skipPackets.contains(event.getPacket())) {
            skipPackets.remove(event.getPacket());
            return;
        }
        if (event.getPacket() instanceof C0FPacketConfirmTransaction && ignoreTransaction.isToggled()) return;

        final long currentTime = System.currentTimeMillis();
        if (target != null && shouldBacktrack()) {
            event.setCanceled(true);
            unReceivePackets.put(event.getPacket(), currentTime);
        } else {
            receivePacket(false);
        }
    }

    private boolean shouldBacktrack() {
        final long currentTime = System.currentTimeMillis();
        if (stopTime != -1 && currentTime - stopTime < delayBetweenBacktracks.getInput()) return false;
        if (startTime != -1 && currentTime - startTime > latency.getInput()) return false;

        final double distance = new Vec3(target).distanceTo(mc.thePlayer);
        if (distance > startRange.getInput() || distance < stopRange.getInput()) return false;
        if (onlyIfTargetGoesAway.isToggled()) {
            final double lastDistance = new Vec3(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ).distanceTo(mc.thePlayer);
            return lastDistance <= distance;
        }
        return true;
    }

    private void receivePacket(boolean delay) {
        try {
            Iterator<Map.Entry<Packet<?>, Long>> packets = unReceivePackets.entrySet().iterator();
            while (packets.hasNext()) {
                Map.Entry<Packet<?>, Long> entry = packets.next();
                Packet<?> packet = entry.getKey();
                if (packet == null) {
                    continue;
                }
                long receiveTime = entry.getValue();
                long ms = System.currentTimeMillis();
                if (Utils.getDifference(ms, receiveTime) > this.latency.getInput() || !delay) {
                    final Packet<NetHandlerPlayClient> netHandlerPlayClientPacket = (Packet<NetHandlerPlayClient>) packet;
                    skipPackets.add(netHandlerPlayClientPacket);
                    netHandlerPlayClientPacket.processPacket(Raven.mc.getNetHandler());
                    packets.remove();
                }
            }
        }
        catch (Exception ignored) {
        }
    }
}
