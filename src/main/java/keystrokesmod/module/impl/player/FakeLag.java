package keystrokesmod.module.impl.player;

import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.PacketUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static keystrokesmod.module.ModuleManager.blink;

public class FakeLag extends Module {
    private final SliderSetting mode;
    private static final String[] MODES = new String[]{"Latency", "Dynamic"};
    private final SliderSetting delay;
    private final ButtonSetting debug;
    private final ButtonSetting dynamicIgnoreTeammates;
    private final ButtonSetting dynamicStopOnHurt;
    private final SliderSetting dynamicStopOnHurtTime;
    private final SliderSetting dynamicStartRange;
    private final SliderSetting dynamicStopRange;
    private final SliderSetting dynamicMaxTargetRange;
    private long lastDisableTime = -1;
    private boolean lastHurt = false;
    private long lastStartBlinkTime = -1;
    @Nullable
    private AbstractClientPlayer target = null;
    private final ConcurrentHashMap<Packet<?>, Long> delayedPackets = new ConcurrentHashMap<>();

    public FakeLag() {
        super("Fake Lag", category.player);
        this.registerSetting(mode = new SliderSetting("Mode", MODES, 0));
        this.registerSetting(delay = new SliderSetting("Delay", 200, 25, 1000, 5, "ms"));
        this.registerSetting(debug = new ButtonSetting("Debug", false));
        this.registerSetting(dynamicIgnoreTeammates = new ButtonSetting("Dynamic Ignore teammates", true));
        this.registerSetting(dynamicStopOnHurt = new ButtonSetting("Dynamic Stop on hurt", true));
        this.registerSetting(dynamicStopOnHurtTime = new SliderSetting("Dynamic Stop on hurt time", 500, 0, 1000, 5, "ms"));
        this.registerSetting(dynamicStartRange = new SliderSetting("Dynamic Start range", 6.0, 3.0, 10.0, 0.1, "blocks"));
        this.registerSetting(dynamicStopRange = new SliderSetting("Dynamic Stop range", 3.5, 1.0, 6.0, 0.1, "blocks"));
        this.registerSetting(dynamicMaxTargetRange = new SliderSetting("Dynamic Max target range", 15.0, 6.0, 20.0, 0.5, "blocks"));
    }

    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }

    @Override
    public void guiUpdate() {
        Utils.correctValue(dynamicStopRange, dynamicStartRange);
        Utils.correctValue(dynamicStartRange, dynamicMaxTargetRange);
    }

    public void onEnable() {
        lastDisableTime = -1;
        lastHurt = false;
        lastStartBlinkTime = -1;
        delayedPackets.clear();
    }

    public void onDisable() {
        sendPacket(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Utils.nullCheck()) {
            sendPacket(false);
            lastDisableTime = System.currentTimeMillis();
            lastStartBlinkTime = -1;
            return;
        }

        switch ((int) mode.getInput()) {
            case 0:
                sendPacket(true);
                break;
            case 1:
                if (System.currentTimeMillis() - lastDisableTime <= dynamicStopOnHurtTime.getInput()) {
                    blink.disable();
                    break;
                }
                
                if (blink.isEnabled()) {
                    if (System.currentTimeMillis() - lastStartBlinkTime > delay.getInput()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, "stop lag: time out.");
                        lastStartBlinkTime = System.currentTimeMillis();
                        blink.disable();
                    } else if (!lastHurt && mc.thePlayer.hurtTime > 0 && dynamicStopOnHurt.isToggled()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, "stop lag: hurt.");
                        lastDisableTime = System.currentTimeMillis();
                        blink.disable();
                    }
                }

                if (target != null) {
                    double distance = new Vec3(mc.thePlayer).distanceTo(target);
                    if (blink.isEnabled() && distance < dynamicStopRange.getInput()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, "stop lag: too low range.");
                        blink.disable();
                    } else if (!blink.isEnabled() && distance > dynamicStopRange.getInput()
                            && new Vec3(mc.thePlayer).distanceTo(target) < dynamicStartRange.getInput()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, "start lag: in range.");
                        lastStartBlinkTime = System.currentTimeMillis();
                        blink.enable();
                    } else if (blink.isEnabled() && distance > dynamicStartRange.getInput()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, "stop lag: out of range.");
                        blink.disable();
                    } else if (distance > dynamicMaxTargetRange.getInput()) {
                        if (debug.isToggled()) Utils.sendModuleMessage(this, String.format("release target: %s", target.getName()));
                        target = null;
                        blink.disable();
                    }
                } else blink.disable();

                lastHurt = mc.thePlayer.hurtTime > 0;
                break;
        }
    }

    @SubscribeEvent
    public void onAttack(@NotNull AttackEntityEvent e) {
        if (e.target instanceof AbstractClientPlayer) {
            if (dynamicIgnoreTeammates.isToggled() && Utils.isTeamMate(e.target)) return;
            target = (AbstractClientPlayer) e.target;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onSendPacket(@NotNull SendPacketEvent e) {
        if ((int) mode.getInput() != 0) return;
        final Packet<?> packet = e.getPacket();
        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart || packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing || packet instanceof C01PacketEncryptionResponse || packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction || packet instanceof C01PacketChatMessage) {
            return;
        }
        long receiveTime = System.currentTimeMillis();
        if (!Utils.nullCheck()) {
            sendPacket(false);
            return;
        }
        if (e.isCanceled()) {
            return;
        }
        delayedPackets.put(packet, receiveTime);
        e.setCanceled(true);
    }

    private void sendPacket(boolean delay) {
        try {
            Iterator<Map.Entry<Packet<?>, Long>> packets = delayedPackets.entrySet().iterator();
            while (packets.hasNext()) {
                Map.Entry<Packet<?>, Long> entry = packets.next();
                Packet<?> packet = entry.getKey();
                if (packet == null) {
                    continue;
                }
                long receiveTime = entry.getValue();
                long ms = System.currentTimeMillis();
                if (Utils.getDifference(ms, receiveTime) > this.delay.getInput() || !delay) {
                    PacketUtils.sendPacketNoEvent(packet);
                    packets.remove();
                }
            }
        }
        catch (Exception ignored) {
        }
    }
}
