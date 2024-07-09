package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.mixins.impl.network.S12PacketEntityVelocityAccessor;
import keystrokesmod.mixins.impl.network.S27PacketExplosionAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.utils.ModeOnly;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

import static keystrokesmod.utility.Utils.isLobby;

public class Velocity extends Module {
    private static final String[] MODES = new String[]{"Normal", "Hypixel", "Intave"};
    public static ModeSetting mode;
    public static SliderSetting horizontal;
    public static SliderSetting vertical;
    private final ButtonSetting cancelExplosion;
    private final ButtonSetting cancelAir;
    private final ButtonSetting damageBoost;
    private final SliderSetting boostMultiplier;
    private final SliderSetting boostDelay;
    private final ButtonSetting groundCheck;
    private final ButtonSetting lobbyCheck;
    private final ButtonSetting onlyFirstHit;
    private final SliderSetting resetTime;
    private final ButtonSetting debug;

    private boolean attacked = false;
    private long lastVelocityTime = -1;

    public Velocity() {
        super("Velocity", category.combat);
        this.registerSetting(new DescriptionSetting("Reduce knock-back."));
        this.registerSetting(mode = new ModeSetting("Mode", MODES, 1));
        final ModeOnly canChangeMode = new ModeOnly(mode, 0, 1);
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 0.0, -100.0, 100.0, 1.0, canChangeMode));
        this.registerSetting(vertical = new SliderSetting("Vertical", 0.0, 0.0, 100.0, 1.0, canChangeMode));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion packet", true, canChangeMode));
        this.registerSetting(cancelAir = new ButtonSetting("Cancel air", false, canChangeMode));
        this.registerSetting(damageBoost = new ButtonSetting("Damage boost", false));
        this.registerSetting(boostMultiplier = new SliderSetting("Boost multiplier", 1.2, 0.5, 2.5, 0.1, damageBoost::isToggled));
        this.registerSetting(boostDelay = new SliderSetting("Boost delay", 0, 0, 1000, 5, "ms", damageBoost::isToggled));
        this.registerSetting(groundCheck = new ButtonSetting("Ground check", false, damageBoost::isToggled));
        this.registerSetting(lobbyCheck = new ButtonSetting("Lobby check", false));
        this.registerSetting(onlyFirstHit = new ButtonSetting("Only first hit", false));
        this.registerSetting(resetTime = new SliderSetting("Reset time", 5000, 500, 10000, 500, "ms", onlyFirstHit::isToggled));
        this.registerSetting(debug = new ButtonSetting("Debug", false, new ModeOnly(mode, 2)));
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (lobbyCheck.isToggled() && isLobby()) {
            return;
        }

        try {
            if (mode.getInput() == 2) {
                if (mc.objectMouseOver.typeOfHit.equals(MovingObjectPosition.MovingObjectType.ENTITY) && mc.thePlayer.hurtTime > 0 && !attacked) {
                    mc.thePlayer.motionX *= 0.6D;
                    mc.thePlayer.motionZ *= 0.6D;
                    mc.thePlayer.setSprinting(false);
                    if (debug.isToggled()) Utils.sendMessage("reduced.");
                }
            }
        } catch (NullPointerException e) {
            Utils.sendMessage(e.getLocalizedMessage());
        }

        attacked = false;
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        attacked = true;
    }

    private boolean overAir() {
        if (mc.thePlayer.onGround) return false;

        final AxisAlignedBB boundingBox = BlockUtils.getCollisionBoundingBox(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ));
        if (boundingBox == null) return true;

        return boundingBox.maxY != mc.thePlayer.posY;
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!Utils.nullCheck() || LongJump.stopModules || e.isCanceled()) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                if (onlyFirstHit.isToggled() && System.currentTimeMillis() - lastVelocityTime < resetTime.getInput()) {
                    return;
                }
                lastVelocityTime = System.currentTimeMillis();
                if (lobbyCheck.isToggled() && isLobby()) {
                    return;
                }
                switch ((int) mode.getInput()) {
                    case 0:
                        if (cancel()) {
                            ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionX(0);
                            ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionY(0);
                            ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionZ(0);
                            return;
                        }
                        ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionX((int) (((S12PacketEntityVelocity) e.getPacket()).getMotionX() * horizontal.getInput() / 100));
                        ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionY((int) (((S12PacketEntityVelocity) e.getPacket()).getMotionY() * vertical.getInput() / 100));
                        ((S12PacketEntityVelocityAccessor) e.getPacket()).setMotionZ((int) (((S12PacketEntityVelocity) e.getPacket()).getMotionZ() * horizontal.getInput() / 100));
                        break;
                    case 1:
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
                        break;
                }
                if (damageBoost.isToggled()) {
                    if (boostDelay.getInput() == 0) {
                        boost();
                    } else {
                        Raven.getExecutor().schedule(this::boost, (long) boostDelay.getInput(), TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
        else if (e.getPacket() instanceof S27PacketExplosion) {
            if (lobbyCheck.isToggled() && isLobby()) {
                return;
            }
            switch ((int) mode.getInput()) {
                case 0:
                    if (cancelExplosion.isToggled() || cancel()) {
                        e.setCanceled(true);
                        return;
                    }
                    ((S27PacketExplosionAccessor) e.getPacket()).setMotionX((float) (((S27PacketExplosion) e.getPacket()).func_149149_c() * horizontal.getInput() / 100));
                    ((S27PacketExplosionAccessor) e.getPacket()).setMotionY((float) (((S27PacketExplosion) e.getPacket()).func_149144_d() * vertical.getInput()) / 100);
                    ((S27PacketExplosionAccessor) e.getPacket()).setMotionZ((float) (((S27PacketExplosion) e.getPacket()).func_149147_e() * horizontal.getInput() / 100));
                    break;
                case 1:
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
                    break;
            }
        }
    }

    private void boost() {
        if (groundCheck.isToggled() && !mc.thePlayer.onGround) {
            return;
        }
        MoveUtil.strafe(MoveUtil.speed() * boostMultiplier.getInput()); // from croat
    }

    private boolean cancel() {
        return (vertical.getInput() == 0 && horizontal.getInput() == 0) || ModuleManager.bedAura.cancelKnockback() || (cancelAir.isToggled() && overAir());
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }
}
