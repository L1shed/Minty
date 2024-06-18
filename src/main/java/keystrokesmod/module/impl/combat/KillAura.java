package keystrokesmod.module.impl.combat;

import akka.japi.Pair;
import keystrokesmod.Raven;
import keystrokesmod.event.*;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.*;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.util.EnumFacing.DOWN;

public class KillAura extends Module {
    public static EntityLivingBase target;
    private final SliderSetting aps;
    public SliderSetting autoBlockMode;
    private final SliderSetting fov;
    private final SliderSetting attackRange;
    private final SliderSetting swingRange;
    private final SliderSetting blockRange;
    private final SliderSetting rotationMode;
    private final SliderSetting rotationSmoothing;
    private final SliderSetting sortMode;
    private final SliderSetting switchDelay;
    private final SliderSetting targets;
    private final ButtonSetting targetInvisible;
    private final ButtonSetting targetPlayer;
    private final ButtonSetting targetEntity;
    private final ButtonSetting disableInInventory;
    private final ButtonSetting disableWhileBlocking;
    private final ButtonSetting disableWhileMining;
    private final ButtonSetting fixSlotReset;
    private final ButtonSetting hitThroughBlocks;
    private final ButtonSetting ignoreTeammates;
    public ButtonSetting manualBlock;
    private final ButtonSetting requireMouseDown;
    private final ButtonSetting silentSwing;
    private final ButtonSetting weaponOnly;
    private final String[] rotationModes = new String[]{"None", "Silent", "Lock view"};
    private final List<EntityLivingBase> availableTargets = new ArrayList<>();
    public AtomicBoolean block = new AtomicBoolean();
    private long lastSwitched = System.currentTimeMillis();
    private boolean switchTargets;
    private byte entityIndex;
    public boolean swing;
    // autoclicker vars
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;
    private Random rand;
    // autoclicker vars end
    private boolean attack;
    private boolean blocking;
    public boolean blinking;
    public boolean lag;
    private boolean swapped;
    public boolean rmbDown;
    private float[] prevRotations;
    private boolean startSmoothing;
    private final ConcurrentLinkedQueue<Packet<?>> blinkedPackets = new ConcurrentLinkedQueue<>();


    public KillAura() {
        super("KillAura", category.combat);
        this.registerSetting(aps = new SliderSetting("APS", 16.0, 1.0, 20.0, 0.5));
        String[] autoBlockModes = new String[]{"Manual", "Vanilla", "Post", "Swap", "Interact", "Fake", "Partial", "Watchdog 1.12.2"};
        this.registerSetting(autoBlockMode = new SliderSetting("Autoblock", autoBlockModes, 0));
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(attackRange = new SliderSetting("Attack range", 3.3, 3.0, 6.0, 0.1));
        this.registerSetting(swingRange = new SliderSetting("Swing range", 3.3, 3.0, 8.0, 0.1));
        this.registerSetting(blockRange = new SliderSetting("Block range", 6.0, 3.0, 12.0, 0.1));
        this.registerSetting(rotationMode = new SliderSetting("Rotation mode", rotationModes, 0));
        this.registerSetting(rotationSmoothing = new SliderSetting("Rotation smoothing", 0, 0, 15, 1));
        String[] sortModes = new String[]{"Health", "HurtTime", "Distance", "Yaw"};
        this.registerSetting(sortMode = new SliderSetting("Sort mode", sortModes, 0.0));
        this.registerSetting(switchDelay = new SliderSetting("Switch delay", 200.0, 50.0, 1000.0, 25.0, "ms"));
        this.registerSetting(targets = new SliderSetting("Targets", 3.0, 1.0, 10.0, 1.0));
        this.registerSetting(targetInvisible = new ButtonSetting("Target invisible", true));
        this.registerSetting(targetPlayer = new ButtonSetting("Target player", true));
        this.registerSetting(targetEntity = new ButtonSetting("Target entity", false));
        this.registerSetting(disableInInventory = new ButtonSetting("Disable in inventory", true));
        this.registerSetting(disableWhileBlocking = new ButtonSetting("Disable while blocking", false));
        this.registerSetting(disableWhileMining = new ButtonSetting("Disable while mining", false));
        this.registerSetting(fixSlotReset = new ButtonSetting("Fix slot reset", false));
        this.registerSetting(hitThroughBlocks = new ButtonSetting("Hit through blocks", true));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", true));
        this.registerSetting(manualBlock = new ButtonSetting("Manual block", false));
        this.registerSetting(requireMouseDown = new ButtonSetting("Require mouse down", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing while blocking", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
    }

    public void onEnable() {
        this.rand = new Random();
    }

    public void onDisable() {
        resetVariables();

    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (ev.phase != TickEvent.Phase.START) {
            return;
        }
        if (canAttack()) {
            attack = true;
        }
        if (target != null && rotationMode.getInput() == 2) {
            float[] rotations = RotationUtils.getRotations(target, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            if (rotationSmoothing.getInput() > 0) {
                float[] speed = new float[]{(float) ((rotations[0] - mc.thePlayer.rotationYaw) / ((101 - rotationSmoothing.getInput()) * 3.634542)), (float) ((rotations[1] - mc.thePlayer.rotationPitch) / ((101 - rotationSmoothing.getInput()) * 5.1853))};
                mc.thePlayer.rotationYaw += speed[0];
                mc.thePlayer.rotationPitch += speed[1];
            }
            else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent e) {
        if (gameNoAction() || playerNoAction()) {
            resetVariables();
            return;
        }

        block();

        if (ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled() && !ModuleManager.bedAura.allowAura.isToggled() && ModuleManager.bedAura.currentBlock != null) {
            resetBlinkState(true);
            return;
        }
        if ((mc.thePlayer.isBlocking() || block.get()) && disableWhileBlocking.isToggled()) {
            resetBlinkState(true);
            return;
        }
        boolean swingWhileBlocking = !silentSwing.isToggled() || !block.get();
        if (swing && attack && HitSelect.canSwing()) {
            if (swingWhileBlocking) {
                mc.thePlayer.swingItem();
            }
            else {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
        }
        if (block.get() && (autoBlockMode.getInput() == 3 || autoBlockMode.getInput() == 4) && Utils.holdingSword()) {
            setBlockState(block.get(), false, false);
            if (ModuleManager.bedAura.stopAutoblock) {
                resetBlinkState(false);
                ModuleManager.bedAura.stopAutoblock = false;
                return;
            }
            if (autoBlockMode.getInput() == 3) {
                if (lag) {
                    blinking = true;
                    if (Raven.badPacketsHandler.playerSlot != mc.thePlayer.inventory.currentItem % 8 + 1) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(Raven.badPacketsHandler.playerSlot = mc.thePlayer.inventory.currentItem % 8 + 1));
                        swapped = true;
                    }
                    lag = false;
                }
                else {
                    if (Raven.badPacketsHandler.delayAttack) {
                        return;
                    }
                    if (Raven.badPacketsHandler.playerSlot != mc.thePlayer.inventory.currentItem) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(Raven.badPacketsHandler.playerSlot = mc.thePlayer.inventory.currentItem));
                        swapped = false;
                    }
                    if (target != null && attack) {
                        attack = false;
                        if (noAimToEntity()) {
                            return;
                        }
                        switchTargets = true;
                        Utils.attackEntity(target, !swing && swingWhileBlocking, !swingWhileBlocking);
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                    }
                    else if (ModuleManager.antiFireball != null && ModuleManager.antiFireball.isEnabled() && ModuleManager.antiFireball.fireball != null && ModuleManager.antiFireball.attack) {
                        Utils.attackEntity(ModuleManager.antiFireball.fireball, !ModuleManager.antiFireball.silentSwing.isToggled(), ModuleManager.antiFireball.silentSwing.isToggled());
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ModuleManager.antiFireball.fireball, C02PacketUseEntity.Action.INTERACT));
                    }
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    releasePackets();
                    lag = true;
                }
            }
            else if (autoBlockMode.getInput() == 4) {
                if (lag) {
                    blinking = true;
                    unBlock();
                    lag = false;
                } else {
                    if (target != null && attack) {
                        attack = false;
                        if (noAimToEntity()) {
                            return;
                        }
                        switchTargets = true;
                        Utils.attackEntity(target, !swing && swingWhileBlocking, !swingWhileBlocking);
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
                    }
                    else if (ModuleManager.antiFireball != null && ModuleManager.antiFireball.isEnabled() && ModuleManager.antiFireball.fireball != null && ModuleManager.antiFireball.attack) {
                        Utils.attackEntity(ModuleManager.antiFireball.fireball, !ModuleManager.antiFireball.silentSwing.isToggled(), ModuleManager.antiFireball.silentSwing.isToggled());
                        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ModuleManager.antiFireball.fireball, C02PacketUseEntity.Action.INTERACT));
                    }
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    releasePackets();
                    lag = true;
                }
            }
            return;
        }
        else if (blinking || lag) {
            resetBlinkState(true);
        }
        if (target == null) {
            return;
        }
        if (attack) {
            resetBlinkState(true);
            attack = false;
            if (noAimToEntity()) {
                return;
            }
            switchTargets = true;
            Utils.attackEntity(target, swingWhileBlocking, !swingWhileBlocking);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreMotion(PreMotionEvent e) {
        if (gameNoAction() || playerNoAction()) {
            resetVariables();
            return;
        }
        setTarget(new float[]{e.getYaw(), e.getPitch()});
        if (target != null && rotationMode.getInput() == 1) {
            float[] rotations = RotationUtils.getRotations(target, e.getYaw(), e.getPitch());
            if (rotationSmoothing.getInput() >= 4) {
                if (!startSmoothing) {
                    prevRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                    startSmoothing = true;
                }
                float[] speed = new float[]{(float) ((rotations[0] - prevRotations[0]) / ((rotationSmoothing.getInput()) * 0.262843)), (float) ((rotations[1] - prevRotations[1]) / ((rotationSmoothing.getInput()) * 0.1637))};
                prevRotations[0] += speed[0];
                prevRotations[1] += speed[1];
                if (prevRotations[1] > 90) {
                    prevRotations[1] = 90;
                }
                else if (prevRotations[1] < -90) {
                    prevRotations[1] = -90;
                }
                e.setYaw(prevRotations[0]);
                e.setPitch(prevRotations[1]);
            }
            else {
                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);
            }
        }
        else {
            startSmoothing = false;
        }
        if (autoBlockMode.getInput() == 2 && block.get() && Utils.holdingSword()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void onPostMotion(PostMotionEvent e) {
        if (autoBlockMode.getInput() == 2 && block.get() && Utils.holdingSword()) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onSendPacket(SendPacketEvent e) {
        if (!Utils.nullCheck() || !blinking) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        blinkedPackets.add(e.getPacket());
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onReceivePacket(ReceivePacketEvent e) {
        if (gameNoAction() || !fixSlotReset.isToggled()) {
            return;
        }
        if (Utils.holdingSword() && (mc.thePlayer.isBlocking() || block.get())) {
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
    public void onMouse(final @NotNull MouseEvent mouseEvent) {
        if (mouseEvent.button == 0 && mouseEvent.buttonstate) {
            if (target != null || swing) {
                mouseEvent.setCanceled(true);
            }
        }
        else if (mouseEvent.button == 1) {
            rmbDown = mouseEvent.buttonstate;
            if (autoBlockMode.getInput() >= 1 && Utils.holdingSword() && block.get()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
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
    }

    @Override
    public String getInfo() {
        return rotationModes[(int) rotationMode.getInput()];
    }

    private boolean noAimToEntity() {
        if (rotationMode.getInput() > 0 && ((rotationSmoothing.getInput() >= 4 && rotationMode.getInput() == 1) || rotationSmoothing.getInput() > 0)) {
            Object[] raycast = Reach.getEntity(attackRange.getInput(), 0, rotationMode.getInput() == 1 ? prevRotations : null);
            return raycast == null || raycast[0] != target;
        }
        return false;
    }

    private void resetVariables() {
        target = null;
        availableTargets.clear();
        block.set(false);
        startSmoothing = false;
        swing = false;
        rmbDown = false;
        attack = false;
        this.i = 0L;
        this.j = 0L;
        block();
        resetBlinkState(true);
        swapped = false;
    }

    private void block() {
        if (!block.get() && !blocking) {
            return;
        }
        if (manualBlock.isToggled() && !rmbDown) {
            block.set(false);
        }
        if (!Utils.holdingSword()) {
            block.set(false);
        }
        switch ((int) autoBlockMode.getInput()) {
            case 1: // vanilla
                setBlockState(block.get(), true, true);
                break;
            case 2: // post
                setBlockState(block.get(), false, true);
                break;
            case 3: // interact
            case 4:
                setBlockState(block.get(), false, false);
                break;
            case 5: // fake
                setBlockState(block.get(), false, false);
                break;
            case 6: // partial
                boolean down = (target == null || target.hurtTime >= 5) && block.get();
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), down);
                Reflection.setButton(1, down);
                blocking = down;
                break;
            case 7: // watchdog 1.12.2
                setBlockState(block.get(), true, true);
                PacketUtils.sendPacketNoEvent(new C0APacketAnimation());
                /*
                code from Acrimony 1.0.5.
                I think it means: "send a swing packet" xd
                    PacketWrapper useItem = PacketWrapper.create(29, (ByteBuf)null, (UserConnection)Via.getManager().getConnectionManager().getConnections().iterator().next());
                    useItem.write(Type.VAR_INT, 1);
                    com.viaversion.viarewind.utils.PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
                 */
                blocking = true;
                break;
        }
    }

    private void setBlockState(boolean state, boolean sendBlock, boolean sendUnBlock) {
        if (Utils.holdingSword()) {
            if (sendBlock && !blocking && state && Utils.holdingSword() && !Raven.badPacketsHandler.C07) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            } else if (sendUnBlock && blocking && !state) {
                unBlock();
            }
        }
        blocking = Reflection.setBlocking(state);
    }

    private void setTarget(float[] rotations) {
        availableTargets.clear();
        block.set(false);
        swing = false;

        mc.theWorld.loadedEntityList.stream()
                .filter(Objects::nonNull)
                .filter(entity -> entity != mc.thePlayer)
                .filter(entity -> entity instanceof EntityLivingBase)
                .filter(entity -> {
                    if (entity instanceof EntityPlayer) {
                        if (!targetPlayer.isToggled()) return false;
                        if (Utils.isFriended((EntityPlayer) entity)) {
                            return false;
                        }
                        if (((EntityPlayer) entity).deathTime != 0) {
                            return false;
                        }
                        return !AntiBot.isBot(entity) && !(ignoreTeammates.isToggled() && Utils.isTeamMate(entity));
                    } else return targetEntity.isToggled();
                })
                .filter(entity -> targetInvisible.isToggled() || !entity.isInvisible())
                .filter(entity -> hitThroughBlocks.isToggled() || !behindBlocks(rotations))
                .filter(entity -> fov.getInput() == 360 || Utils.inFov((float) fov.getInput(), entity))
                .map(entity -> new Pair<>(entity, mc.thePlayer.getDistanceSqToEntity(entity)))
                .sorted((p1, p2) -> p2.second().compareTo(p1.second()))
                .forEach(pair -> {
                    if (availableTargets.size() >= targets.getInput()) return;

                    // need a more accurate distance check as this can ghost on hypixel
                    if (pair.second() <= blockRange.getInput() * blockRange.getInput() && autoBlockMode.getInput() > 0) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        block.set(true);
                    }
                    if (pair.second() <= swingRange.getInput() * swingRange.getInput()) {
                        swing = true;
                    }
                    if (pair.second() <= attackRange.getInput() * swingRange.getInput()) {
                        availableTargets.add((EntityLivingBase) pair.first());
                    }
                });

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
            Comparator<EntityLivingBase> comparator = null;
            switch ((int) sortMode.getInput()) {
                case 0:
                    comparator = Comparator.comparingDouble(entityPlayer -> (double)entityPlayer.getHealth());
                    break;
                case 1:
                    comparator = Comparator.comparingDouble(entityPlayer2 -> (double)entityPlayer2.hurtTime);
                    break;
                case 2:
                    comparator = Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity));
                    break;
                case 3:
                    comparator = Comparator.comparingDouble(entity2 -> RotationUtils.distanceFromYaw(entity2, false));
                    break;
            }
            availableTargets.sort(comparator);
            if (entityIndex > availableTargets.size() - 1) {
                entityIndex = 0;
            }
            target = availableTargets.get(entityIndex);
        } else {
            target = null;
        }
    }

    private boolean gameNoAction() {
        if (!Utils.nullCheck()) {
            return true;
        }
        if (ModuleManager.bedAura.isEnabled() && !ModuleManager.bedAura.allowAura.isToggled() && ModuleManager.bedAura.currentBlock != null) {
            return true;
        }
        return mc.thePlayer.isDead;
    }

    private boolean playerNoAction() {
        if (!Mouse.isButtonDown(0) && requireMouseDown.isToggled()) {
            return true;
        }
        else if (!Utils.holdingWeapon() && weaponOnly.isToggled()) {
            return true;
        }
        else if (isMining() && disableWhileMining.isToggled()) {
            return true;
        }
        else return mc.currentScreen != null && disableInInventory.isToggled();
    }

    private boolean isMining() {
        return Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }

    private boolean canAttack() {
        if (this.j > 0L && this.i > 0L) {
            if (System.currentTimeMillis() > this.j) {
                this.gd();
                return true;
            } else if (System.currentTimeMillis() > this.i) {
                return false;
            }
        } else {
            this.gd();
        }
        return false;
    }

    public void gd() {
        double c = aps.getInput() + 0.4D * this.rand.nextDouble();
        long d = (int) Math.round(1000.0D / c);
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.n = false;
            }

            this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.n) {
            d = (long) ((double) d * this.m);
        }

        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + (long) this.rand.nextInt(100);
            }

            this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
    }

    private void unBlock() {
        if (!Utils.holdingSword()) {
            return;
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, DOWN));
    }

    public void resetBlinkState(boolean unblock) {
        if (!Utils.nullCheck()) return;
        releasePackets();
        blocking = false;
        if (Raven.badPacketsHandler.playerSlot != mc.thePlayer.inventory.currentItem && swapped) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            Raven.badPacketsHandler.playerSlot = mc.thePlayer.inventory.currentItem;
            swapped = false;
        }
        if (lag && unblock) {
            unBlock();
        }
        lag = false;
    }

    private void releasePackets() {
        try {
            synchronized (blinkedPackets) {
                for (Packet<?> packet : blinkedPackets) {
                    if (packet instanceof C09PacketHeldItemChange) {
                        Raven.badPacketsHandler.playerSlot = ((C09PacketHeldItemChange) packet).getSlotId();
                    }
                    PacketUtils.sendPacketNoEvent(packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendModuleMessage(this, "&cThere was an error releasing blinked packets");
        }
        blinkedPackets.clear();
        blinking = false;
    }

    private boolean behindBlocks(float[] rotations) {
        switch ((int) rotationMode.getInput()) {
            default:
                if (mc.thePlayer.getCollisionBoundingBox().intersectsWith(target.getCollisionBoundingBox())) {
                    return false;
                }
            case 2:
            case 0:
                if (mc.objectMouseOver != null) {
                    BlockPos p = mc.objectMouseOver.getBlockPos();
                    if (p != null && mc.theWorld.getBlockState(p).getBlock().isFullBlock()) {
                        return true;
                    }
                }
                break;
            case 1:
                try {
                    Vec3 from = new Vec3(mc.thePlayer).add(0, mc.thePlayer.getEyeHeight(), 0);
                    MovingObjectPosition hitResult = RotationUtils.rayCast(
                            RotationUtils.getNearestPoint(target.getCollisionBoundingBox(), from).distanceTo(from),
                            rotations[0], rotations[1]
                    );
                    if (hitResult != null) {
                        if (keystrokesmod.module.impl.other.anticheats.utils.world.BlockUtils.isFullBlock(mc.theWorld.getBlockState(hitResult.getBlockPos()))) {
                            return true;
                        }
                    }
                } catch (NullPointerException ignored) {
                }
        }
        return false;
    }
}