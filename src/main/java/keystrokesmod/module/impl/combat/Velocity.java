package keystrokesmod.module.impl.combat;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class Velocity extends Module {
    public static SliderSetting horizontal;
    public static SliderSetting vertical;
    private final ButtonSetting cancelExplosion;
    private final ButtonSetting damageBoost;
    private final SliderSetting boostMultiplier;
    private final ButtonSetting groundCheck;
    private final ButtonSetting lobbyCheck;

    public Velocity() {
        super("Velocity", category.combat);
        this.registerSetting(new DescriptionSetting("Reduce knockback."));
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(vertical = new SliderSetting("Vertical", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion packet", true));
        this.registerSetting(damageBoost = new ButtonSetting("Damage boost", false));
        this.registerSetting(boostMultiplier = new SliderSetting("Boost multiplier", 2.0, 1.0, 8.0, 0.1));
        this.registerSetting(groundCheck = new ButtonSetting("Ground check", false));
        this.registerSetting(lobbyCheck = new ButtonSetting("Lobby check", false));
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck() || LongJump.stopModules || e.isCanceled()) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                if (lobbyCheck.isToggled() && isLobby()) {
                    return;
                }
                e.setCanceled(true);
                if (cancel()) {
                    return;
                }
                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) e.getPacket();
                if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                    mc.thePlayer.motionY = ((double) s12PacketEntityVelocity.getMotionY() / 8000) * vertical.getInput()/100;
                }
                else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                    mc.thePlayer.motionX = ((double) s12PacketEntityVelocity.getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionZ = ((double) s12PacketEntityVelocity.getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                else {
                    mc.thePlayer.motionX = ((double) s12PacketEntityVelocity.getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionY = ((double) s12PacketEntityVelocity.getMotionY() / 8000) * vertical.getInput()/100;
                    mc.thePlayer.motionZ = ((double) s12PacketEntityVelocity.getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                e.setCanceled(true);
                if (damageBoost.isToggled()) {
                    if (groundCheck.isToggled() && !mc.thePlayer.onGround) {
                        return;
                    }
                    Utils.setSpeed(Utils.getHorizontalSpeed() * boostMultiplier.getInput()); // from croat
                }
            }
        }
        else if (e.getPacket() instanceof S27PacketExplosion) {
            if (lobbyCheck.isToggled() && isLobby()) {
                return;
            }
            e.setCanceled(true);
            if (cancelExplosion.isToggled() || cancel()) {
                return;
            }
            S27PacketExplosion s27PacketExplosion = (S27PacketExplosion) e.getPacket();
            if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                mc.thePlayer.motionY += s27PacketExplosion.func_149144_d() * vertical.getInput()/100;
            }
            else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                mc.thePlayer.motionX += s27PacketExplosion.func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionZ += s27PacketExplosion.func_149147_e() * horizontal.getInput()/100;
            }
            else {
                mc.thePlayer.motionX += s27PacketExplosion.func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionY += s27PacketExplosion.func_149144_d() * vertical.getInput()/100;
                mc.thePlayer.motionZ += s27PacketExplosion.func_149147_e() * horizontal.getInput()/100;
            }
            e.setCanceled(true);
        }
    }

    private boolean cancel() {
        return (vertical.getInput() == 0 && horizontal.getInput() == 0) || ModuleManager.bedAura.cancelKnockback();
    }

    @Override
    public String getInfo() {
        return (int) horizontal.getInput() + "% " + (int) vertical.getInput() + "%";
    }

    private boolean isLobby() {
        if (Utils.isHypixel()) {
            List<String> sidebarLines = Utils.getSidebarLines();
            if (!sidebarLines.isEmpty()) {
                String[] parts = Utils.stripColor(sidebarLines.get(1)).split(" {2}");
                return parts.length > 1 && parts[1].charAt(0) == 'L';
            }
        }
        return false;
    }
}
