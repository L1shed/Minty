package keystrokesmod.module.impl.combat;

import akka.japi.Pair;
import keystrokesmod.Raven;
import keystrokesmod.mixins.impl.client.PlayerControllerMPAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.other.anticheats.utils.world.PlayerRotation;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.AimSimulator;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AimAssist extends Module {
    private final SliderSetting horizonSpeed;
    private final SliderSetting verticalSpeed;
    private final SliderSetting fov;
    private final SliderSetting distance;
    private final ButtonSetting clickAim;
    private final ButtonSetting stopOnTarget;
    private final ButtonSetting breakBlocks;
    private final ButtonSetting singleTarget;
    private final ButtonSetting weaponOnly;
    private final ButtonSetting aimInvis;
    private final ButtonSetting blatantMode;
    private final ButtonSetting aimNearest;
    private final ButtonSetting ignoreTeammates;

    private EntityPlayer target = null;
    public AimAssist() {
        super("AimAssist", category.combat, 0);
        this.registerSetting(horizonSpeed = new SliderSetting("Horizon speed", 3, 0, 5, 0.05));
        this.registerSetting(verticalSpeed = new SliderSetting("Vertical speed", 0, 0, 5, 0.05));
        this.registerSetting(fov = new SliderSetting("FOV", 90.0D, 15.0D, 180.0D, 1.0D));
        this.registerSetting(distance = new SliderSetting("Distance", 4.5D, 1.0D, 10.0D, 0.5D));
        this.registerSetting(clickAim = new ButtonSetting("Click aim", true));
        this.registerSetting(stopOnTarget = new ButtonSetting("Stop on target", true));
        this.registerSetting(breakBlocks = new ButtonSetting("Break blocks", false));
        this.registerSetting(singleTarget = new ButtonSetting("Single target", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(aimInvis = new ButtonSetting("Aim invis", false));
        this.registerSetting(blatantMode = new ButtonSetting("Blatant mode", false));
        this.registerSetting(aimNearest = new ButtonSetting("Aim nearest", false));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", false));
    }

    public void onUpdate() {
        if (noAction()) {
            target = null;
            return;
        }

        target = getEnemy();
        if (target != null) {
            if (Raven.debugger) {
                Utils.sendMessage(this.getName() + " &e" + target.getName());
            }

            if (stopOnTarget.isToggled() && mc.objectMouseOver.entityHit == target) return;

            if (blatantMode.isToggled()) {
                final Vec3 pos = Utils.getEyePos(target);
                mc.thePlayer.rotationYaw = PlayerRotation.getYaw(pos);
                mc.thePlayer.rotationPitch = PlayerRotation.getPitch(pos);
            } else {
                final Pair<Float, Float> rot = AimSimulator.getLegitAim(target, mc.thePlayer,
                        aimNearest.isToggled(), true, false, null, 0);
                if (horizonSpeed.getInput() > 0)
                    mc.thePlayer.rotationYaw = AimSimulator.rotMove(rot.first(), mc.thePlayer.rotationYaw,
                            (float) horizonSpeed.getInput());
                if (verticalSpeed.getInput() > 0)
                    mc.thePlayer.rotationPitch = AimSimulator.rotMove(rot.second(), mc.thePlayer.rotationPitch,
                            (float) verticalSpeed.getInput());
            }
        }
    }

    private boolean noAction() {
        if (mc.currentScreen != null || !mc.inGameHasFocus) return true;
        if (weaponOnly.isToggled() && !Utils.holdingWeapon()) return true;
        if (clickAim.isToggled() && !Utils.ilc()) return true;
        return breakBlocks.isToggled() && ((PlayerControllerMPAccessor) mc.playerController).isHittingBlock();
    }

    private @Nullable EntityPlayer getEnemy() {
        final int n = (int)fov.getInput();

        List<EntityPlayer> players = mc.theWorld.playerEntities;
        if (target != null && singleTarget.isToggled() && players.contains(target)) {
            return target;
        }
        EntityPlayer target = null;
        double targetDist = Double.MAX_VALUE;
        for (final EntityPlayer entityPlayer : players) {
            if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {
                if (Utils.isFriended(entityPlayer)) {
                    continue;
                }
                if (ignoreTeammates.isToggled() && Utils.isTeamMate(entityPlayer)) {
                    continue;
                }
                if (!aimInvis.isToggled() && entityPlayer.isInvisible()) {
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(entityPlayer) > distance.getInput()) {
                    continue;
                }
                if (AntiBot.isBot(entityPlayer)) {
                    continue;
                }
                if (!blatantMode.isToggled() && n != 360 && !Utils.inFov((float)n, entityPlayer)) {
                    continue;
                }
                double dist = new Vec3(entityPlayer).distanceTo(mc.thePlayer);
                if (dist < targetDist) {
                    target = entityPlayer;
                    targetDist = dist;
                }
            }
        }
        return target;
    }
}
