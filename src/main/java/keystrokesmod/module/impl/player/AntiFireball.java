package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;

public class AntiFireball extends Module {
    private SliderSetting fov;
    private SliderSetting range;
    private ButtonSetting disableWhileFlying;
    private ButtonSetting blocksRotate;
    private ButtonSetting projectileRotate;
    private ButtonSetting silentSwing;
    private EntityFireball fireball;
    private HashSet<Entity> fireballs = new HashSet<>();

    public AntiFireball() {
        super("AntiFireball", category.player);
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(range = new SliderSetting("Range", 8.0, 3.0, 15.0, 0.5));
        this.registerSetting(disableWhileFlying = new ButtonSetting("Disable while flying", false));
        this.registerSetting(blocksRotate = new ButtonSetting("Rotate with blocks", false));
        this.registerSetting(projectileRotate = new ButtonSetting("Rotate with projectiles", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing", false));
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (mc.thePlayer.capabilities.isFlying && disableWhileFlying.isToggled()) {
            return;
        }
        if (fireball != null) {
            Utils.attackEntity(fireball, !silentSwing.isToggled());
            final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
            if (getHeldItem != null && getHeldItem.getItem() instanceof ItemBlock && !blocksRotate.isToggled()) {
                return;
            }
            if (getHeldItem != null && (getHeldItem.getItem() instanceof ItemBow || getHeldItem.getItem() instanceof ItemSnowball || getHeldItem.getItem() instanceof ItemEgg || getHeldItem.getItem() instanceof ItemFishingRod) && !projectileRotate.isToggled()) {
                return;
            }
            float[] rotations = RotationUtils.getRotations(fireball, e.getYaw(), e.getPitch());
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }
    }

    private EntityFireball getFireball() {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFireball)) {
                continue;
            }
            if (!this.fireballs.contains(entity)) {
                continue;
            }
            if (mc.thePlayer.getDistanceSqToEntity(entity) > range.getInput() * range.getInput()) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !Utils.inFov(n, entity)) {
                continue;
            }
            return (EntityFireball) entity;
        }
        return null;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (e.entity == mc.thePlayer) {
            this.fireballs.clear();
        } else if (e.entity instanceof EntityFireball && mc.thePlayer.getDistanceSqToEntity(e.entity) > 16.0) {
            this.fireballs.add(e.entity);
        }
    }

    public void onDisable() {
        this.fireballs.clear();
        this.fireball = null;
    }

    public void onUpdate() {
        if (mc.currentScreen != null) {
            fireball = null;
            return;
        }
        fireball = this.getFireball();
    }
}
