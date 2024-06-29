package keystrokesmod.module.impl.world;

import keystrokesmod.Raven;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.other.anticheats.utils.world.BlockUtils;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.module.setting.utils.ModeOnly;
import keystrokesmod.utility.Reflection;
import keystrokesmod.utility.Utils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.concurrent.TimeUnit;

import static keystrokesmod.module.ModuleManager.scaffold;

public class Tower extends Module {
    private final ModeSetting mode;
    private final SliderSetting speed;
    private final SliderSetting diagonalSpeed;
    private final SliderSetting slowedSpeed;
    private final SliderSetting slowedTicks;
    private final ButtonSetting disableWhileCollided;
    private final ButtonSetting disableWhileHurt;
    private final ButtonSetting sprintJumpForward;
    private final ButtonSetting hypixelNoStrafe;
    private final ButtonSetting hypixelLowHop;
    private final SliderSetting hypixelOffGroundSpeed;
    private final SliderSetting hypixelJumpMotion;
    private final SliderSetting hypixelTowerDelay;
    private final ButtonSetting hypixelTowerTest;
    private int slowTicks;
    private boolean wasTowering;
    private int offGroundTicks = 0;
    private BlockPos toweredBlock = null;
    public Tower() {
        super("Tower", category.world);
        this.registerSetting(new DescriptionSetting("Works with SafeWalk & Scaffold"));
        String[] modes = new String[]{"Vanilla", "Hypixel"};
        this.registerSetting(mode = new ModeSetting("Mode", modes, 0));
        final ModeOnly mode0 = new ModeOnly(mode, 0);
        final ModeOnly mode1 = new ModeOnly(mode, 1);
        this.registerSetting(speed = new SliderSetting("Speed", 0.95, 0.5, 1, 0.01));
        this.registerSetting(diagonalSpeed = new SliderSetting("Diagonal speed", 5, 0, 10, 0.1, mode0));
        this.registerSetting(slowedSpeed = new SliderSetting("Slowed speed", 2, 0, 9, 0.1, mode0));
        this.registerSetting(slowedTicks = new SliderSetting("Slowed ticks", 1, 0, 20, 1, mode0));
        this.registerSetting(hypixelOffGroundSpeed = new SliderSetting("Hypixel off ground speed", 0.5, 0.0, 1.0, 0.01, mode1));
        this.registerSetting(hypixelNoStrafe = new ButtonSetting("Hypixel no strafe", false, mode1));
        this.registerSetting(hypixelLowHop = new ButtonSetting("Hypixel low hop", false, mode1));
        this.registerSetting(hypixelTowerTest = new ButtonSetting("Hypixel tower test", false, mode1));
        this.registerSetting(hypixelJumpMotion = new SliderSetting("Hypixel tower motion", 0.4, 0.2, 0.8, 0.01, () -> hypixelTowerTest.isToggled() && mode1.get()));
        this.registerSetting(hypixelTowerDelay = new SliderSetting("Hypixel tower delay", 100, 0, 1000, 10, "ms", () -> hypixelTowerTest.isToggled() && mode1.get()));
        this.registerSetting(disableWhileCollided = new ButtonSetting("Disable while collided", false));
        this.registerSetting(disableWhileHurt = new ButtonSetting("Disable while hurt", false));
        this.registerSetting(sprintJumpForward = new ButtonSetting("Sprint jump forward", true));
        this.canBeEnabled = false;
    }

    @Override
    public void onDisable() {
        wasTowering = false;
        offGroundTicks = 0;
        toweredBlock = null;
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) throws IllegalAccessException {
        if (canTower()) {
            wasTowering = true;
            switch ((int) mode.getInput()) {
                case 0:
                    Utils.setSpeed(Math.max((diagonal() ? diagonalSpeed.getInput() : speed.getInput()) * 0.1 - 0.25, 0));
                    mc.thePlayer.jump();
                    break;
                case 1:
                    Reflection.jumpTicks.set(mc.thePlayer, 0);
                    e.setSprinting(false);

                    if (hypixelTowerTest.isToggled() && mc.thePlayer.motionX == 0 && mc.thePlayer.motionZ == 0) {
                        if (scaffold.placeBlock != null) {
                            BlockPos groundBlock = scaffold.placeBlock.getBlockPos().add(1, -1, 0);
                            if (BlockUtils.isFullBlock(mc.theWorld.getBlockState(groundBlock))) {
                                toweredBlock = groundBlock.up();
                            } else {
                                toweredBlock = null;
                            }
                            if (toweredBlock != null) {
                                Raven.getExecutor().schedule(() -> {
                                    if (scaffold.place(new MovingObjectPosition(
                                            new Vec3(toweredBlock.getX() + 0.1, scaffold.placeBlock.hitVec.yCoord, scaffold.placeBlock.hitVec.zCoord),
                                            EnumFacing.UP,
                                            toweredBlock), true)) {
//                                        e.setOnGround(true);
                                    }
                                }, (long) hypixelTowerDelay.getInput(), TimeUnit.MILLISECONDS);
                                mc.thePlayer.motionY = hypixelJumpMotion.getInput();
                            }
                        }
                    } else {
                        toweredBlock = null;
                        double moveSpeed = e.isOnGround() ? speed.getInput() : hypixelOffGroundSpeed.getInput();
                        if (hypixelNoStrafe.isToggled()) {
                            if (Math.abs(mc.thePlayer.motionX) >= Math.abs(mc.thePlayer.motionZ)) {
                                mc.thePlayer.motionX *= moveSpeed;
                                mc.thePlayer.motionZ = 0;
                            } else {
                                mc.thePlayer.motionZ *= moveSpeed;
                                mc.thePlayer.motionX = 0;
                            }
                        } else {
                            mc.thePlayer.motionX *= moveSpeed;
                            mc.thePlayer.motionZ *= moveSpeed;
                        }
                    }
                    break;

            }
        } else if (mode.getInput() == 0) {
            if (wasTowering && slowedTicks.getInput() > 0 && modulesEnabled()) {
                if (slowTicks++ < slowedTicks.getInput()) {
                    Utils.setSpeed(Math.max(slowedSpeed.getInput() * 0.1 - 0.25, 0));
                }
                else {
                    slowTicks = 0;
                    wasTowering = false;
                }
            }
            else {
                if (wasTowering) {
                    wasTowering = false;
                }
                slowTicks = 0;
            }
            reset();
        }
    }

    @SubscribeEvent
    public void onPreUpdate(PreUpdateEvent event) {
        if (mc.thePlayer.onGround) {
            offGroundTicks = 0;
        } else {
            offGroundTicks++;
        }

        if (canTower() && hypixelLowHop.isToggled() && Utils.isMoving()) {
            switch (offGroundTicks) {
                case 0:
                    mc.thePlayer.motionY = 0.4196;
                    break;
                case 3:
                case 4:
                    mc.thePlayer.motionY = 0;
                    break;
                case 5:
                    mc.thePlayer.motionY = 0.4191;
                    break;
                case 6:
                    mc.thePlayer.motionY = 0.3275;
                    break;
                case 11:
                    mc.thePlayer.motionY = -0.5;
                    break;
            }
        }
    }

    private void reset() {
    }

    private boolean canTower() {
        if (scaffold.totalBlocks() == 0) return false;
        if (mc.currentScreen != null) return false;
        if (!Utils.nullCheck() || !Utils.jumpDown()) {
            return false;
        }
        else if (disableWhileHurt.isToggled() && mc.thePlayer.hurtTime >= 9) {
            return false;
        }
        else if (disableWhileCollided.isToggled() && mc.thePlayer.isCollidedHorizontally) {
            return false;
        }
        else return modulesEnabled();
    }

    private boolean modulesEnabled() {
        return  ((ModuleManager.safeWalk.isEnabled() && ModuleManager.safeWalk.tower.isToggled() && SafeWalk.canSafeWalk()) || (scaffold.isEnabled() && scaffold.tower.isToggled()));
    }

    public boolean canSprint() {
        return canTower() && this.sprintJumpForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) && Utils.jumpDown();
    }

    private boolean diagonal() {
        return (Math.abs(mc.thePlayer.motionX) > 0.05 && Math.abs(mc.thePlayer.motionZ) > 0.05);
    }
}
