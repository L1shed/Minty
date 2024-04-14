package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.BlockUtils;
import keystrokesmod.utility.RandomUtils;
import keystrokesmod.utility.RotationUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class KillAura extends Module {
    public static EntityLivingBase target;
    private SliderSetting aps;
    private SliderSetting randomization;
    private SliderSetting autoBlockMode;
    private SliderSetting fov;
    private SliderSetting attackRange;
    private SliderSetting swingRange;
    private SliderSetting blockRange;
    private SliderSetting rotationMode;
    private SliderSetting sortMode;
    private SliderSetting switchDelay;
    private SliderSetting targets;
    private ButtonSetting targetInvis;
    private ButtonSetting disableInInventory;
    private ButtonSetting disableWhileBlocking;
    private ButtonSetting disableWhileMining;
    private ButtonSetting fixSlotReset;
    private ButtonSetting hitThroughBlocks;
    private ButtonSetting ignoreTeammates;
    private ButtonSetting requireMouseDown;
    private ButtonSetting weaponOnly;
    private String[] autoBlockModes = new String[]{"Manual", "Vanilla", "Fake"};
    private String[] rotationModes = new String[]{"None", "Silent", "Lock"};
    private String[] sortModes = new String[]{"Health", "HurtTime", "Distance", "Yaw"};
    private List<EntityLivingBase> availableTargets = new ArrayList<>();
    private AtomicBoolean block = new AtomicBoolean();
    private long lastSwitched = System.currentTimeMillis();
    private long lastAttacked = System.currentTimeMillis();
    private boolean switchTargets;
    private byte entityIndex;
    private boolean swing;


    public KillAura() {
        super("KillAura", category.combat);
        this.registerSetting(aps = new SliderSetting("APS", 16.0, 1.0, 25.0, 0.5));
        this.registerSetting(randomization = new SliderSetting("Randomization", 0, 0, 5, 0.15));
        this.registerSetting(autoBlockMode = new SliderSetting("Autoblock", autoBlockModes, 0));
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(attackRange = new SliderSetting("Range (attack)", 3.0, 3.0, 6.0, 0.05));
        this.registerSetting(swingRange = new SliderSetting("Range (swing)", 3.3, 3.0, 8.0, 0.05));
        this.registerSetting(blockRange = new SliderSetting("Range (block)", 6.0, 3.0, 12.0, 0.05));
        this.registerSetting(rotationMode = new SliderSetting("Rotation mode", rotationModes, 0));
        this.registerSetting(sortMode = new SliderSetting("Sort mode", sortModes, 0.0));
        this.registerSetting(switchDelay = new SliderSetting("Switch delay (ms)", 200.0, 50.0, 1000.0, 25.0));
        this.registerSetting(targets = new SliderSetting("Targets", 3.0, 1.0, 10.0, 1.0));
        this.registerSetting(targetInvis = new ButtonSetting("Target invis", true));
        this.registerSetting(disableInInventory = new ButtonSetting("Disable in inventory", true));
        this.registerSetting(disableWhileBlocking = new ButtonSetting("Disable while blocking", false));
        this.registerSetting(disableWhileMining = new ButtonSetting("Disable while mining", false));
        this.registerSetting(fixSlotReset = new ButtonSetting("Fix slot reset", false));
        this.registerSetting(hitThroughBlocks = new ButtonSetting("Hit through blocks", true));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", true));
        this.registerSetting(requireMouseDown = new ButtonSetting("Require mouse down", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
    }

    public void onDisable() {
        resetVariables();
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (!basicCondition()) {
            resetVariables();
            return;
        }
        setTarget();

        // block range code here

        if (settingCondition()) {
            if ((ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled() && !ModuleManager.bedAura.allowKillAura.isToggled() && ModuleManager.bedAura.currentBlock != null)) {
                return;
            }
            if (mc.thePlayer.isBlocking() && disableWhileBlocking.isToggled()) {
                return;
            }
            if (swing) {
                mc.thePlayer.swingItem();
            }
            if (target == null) {
                return;
            }
            if (Math.abs(System.currentTimeMillis() - lastAttacked) > 1000 / aps.getInput() + RandomUtils.getRandom(randomization.getInput())) {
                switchTargets = true;
                Utils.attackEntity(target, !swing);
                lastAttacked = System.currentTimeMillis();
            }
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (!basicCondition() || !settingCondition()) {
            resetVariables();
            return;
        }
        if (target != null && rotationMode.getInput() > 0) {
            float[] rotations = RotationUtils.getRotations(target, e.getYaw(), e.getPitch());
            switch ((int) rotationMode.getInput()) {
                case 1:
                    e.setYaw(rotations[0]);
                    e.setPitch(rotations[1]);
                    break;
                case 2:
                    mc.thePlayer.rotationYaw = rotations[0];
                    mc.thePlayer.rotationPitch = rotations[1];
                    break;
            }
        }
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!basicCondition() || !fixSlotReset.isToggled()) {
            return;
        }
        if (Utils.holdingSword() && mc.thePlayer.isBlocking()) {
            if (e.getPacket() instanceof S2FPacketSetSlot) {
                if (mc.thePlayer.inventory.currentItem == ((S2FPacketSetSlot) e.getPacket()).func_149173_d() - 36 && mc.currentScreen == null) {
                    if (((S2FPacketSetSlot) e.getPacket()).func_149174_e() == null || (((S2FPacketSetSlot) e.getPacket()).func_149174_e().getItem() != mc.thePlayer.getHeldItem().getItem())) {
                        return;
                    }
                    e.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouse(final MouseEvent mouseEvent) {
        if (mouseEvent.button == 0) {
            if (target != null || swing) {
                mouseEvent.setCanceled(true);
            }
        }
        else if (mouseEvent.button == 1 && (autoBlockMode.getInput() >= 1) && Utils.holdingSword()) {
            if (target == null && mc.objectMouseOver != null) {
                if (mc.objectMouseOver.entityHit != null && AntiBot.isBot(mc.objectMouseOver.entityHit)) {
                    return;
                }
                final BlockPos getBlockPos = mc.objectMouseOver.getBlockPos();
                if (getBlockPos != null && (BlockUtils.check(getBlockPos, Blocks.chest) || BlockUtils.check(getBlockPos, Blocks.ender_chest))) {
                    return;
                }
            }
            mouseEvent.setCanceled(true);
        }
    }

    @Override
    public String getInfo() {
        return rotationModes[(int) rotationMode.getInput()];
    }

    private void resetVariables() {
        target = null;
        availableTargets.clear();
        block.set(false);
        swing = false;
    }

    private void setTarget() {
        availableTargets.clear();
        block.set(false);
        swing = false;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (availableTargets.size() > targets.getInput()) {
                continue;
            }
            if (entity == null) {
                continue;
            }
            if (entity == mc.thePlayer) {
                continue;
            }
            if (!(entity instanceof EntityPlayer)) {
                continue;
            }
            if (Utils.isFriended((EntityPlayer) entity)) {
                continue;
            }
            if (!entity.isEntityAlive() || entity.ticksExisted < 10) {
                continue;
            }
            if (AntiBot.isBot(entity) || (Utils.isTeamMate(entity) && ignoreTeammates.isToggled())) {
                continue;
            }
            if (entity.isInvisible() && !targetInvis.isToggled()) {
                continue;
            }
            if (!mc.thePlayer.canEntityBeSeen(entity) && !hitThroughBlocks.isToggled()) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !Utils.inFov(n, entity)) {
                continue;
            }
            double distance = mc.thePlayer.getDistanceToEntity(entity); // need a more accurate distance check as this can ghost on hypixel
            if (distance <= blockRange.getInput()) {
                block.set(true);
            }
            if (distance <= swingRange.getInput()) {
                swing = true;
            }
            if (distance > attackRange.getInput()) {
                continue;
            }
            availableTargets.add((EntityLivingBase) entity);
        }
        if (Math.abs(System.currentTimeMillis() - lastSwitched) > switchDelay.getInput() && switchTargets) {
            switchTargets = false;
            if (entityIndex < availableTargets.size() - 1) {
                entityIndex++;
            } else {
                entityIndex = 0;
            }
            lastSwitched = System.currentTimeMillis();
        }
        if (!availableTargets.isEmpty()) {
            if (entityIndex > availableTargets.size() - 1) {
                entityIndex = 0;
            }
            target = availableTargets.get(entityIndex);
        } else {
            target = null;
        }
    }

    private boolean basicCondition() {
        if (!Utils.nullCheck()) {
            return false;
        }
        if (mc.thePlayer.isDead) {
            return false;
        }
        return true;
    }

    private boolean settingCondition() {
        if (!Mouse.isButtonDown(0) && requireMouseDown.isToggled()) {
            return false;
        }
        else if (!Utils.holdingWeapon() && weaponOnly.isToggled()) {
            return false;
        }
        else if (isMining() && disableWhileMining.isToggled()) {
            return false;
        }
        else if (mc.currentScreen != null && disableInInventory.isToggled()) {
            return false;
        }
        return true;
    }

    private boolean isMining() {
        return Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }
}
