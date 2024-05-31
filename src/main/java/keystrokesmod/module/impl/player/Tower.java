package keystrokesmod.module.impl.player;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Tower extends Module {
    private SliderSetting mode;
    private SliderSetting speed;
    private SliderSetting diagonalSpeed;
    private SliderSetting slowedSpeed;
    private SliderSetting slowedTicks;
    private ButtonSetting disableWhileCollided;
    private ButtonSetting disableWhileHurt;
    private ButtonSetting sprintJumpForward;
    private String[] modes = new String[]{"Vanilla"};
    private int slowTicks;
    private boolean wasTowering;
    public Tower() {
        super("Tower", category.player);
        this.registerSetting(new DescriptionSetting("Works with Safewalk & Scaffold"));
        this.registerSetting(mode = new SliderSetting("Mode", modes, 0));
        this.registerSetting(speed = new SliderSetting("Speed", 5, 0.5, 9, 0.1));
        this.registerSetting(diagonalSpeed = new SliderSetting("Diagonal speed", 5, 0, 10, 0.1));
        this.registerSetting(slowedSpeed = new SliderSetting("Slowed speed", 2, 0, 9, 0.1));
        this.registerSetting(slowedTicks = new SliderSetting("Slowed ticks", 1, 0, 20, 1));
        this.registerSetting(disableWhileCollided = new ButtonSetting("Disable while collided", false));
        this.registerSetting(disableWhileHurt = new ButtonSetting("Disable while hurt", false));
        this.registerSetting(sprintJumpForward = new ButtonSetting("Sprint jump forward", false));
        this.canBeEnabled = false;
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent e) {
        if (canTower()) {
            wasTowering = true;
            Utils.setSpeed(Math.max((diagonal() ? diagonalSpeed.getInput() : speed.getInput()) * 0.1 - 0.25, 0));
            switch ((int) mode.getInput()) {
                case 0:
                    mc.thePlayer.jump();
                    break;
            }
        }
        else {
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

    private void reset() {
    }

    private boolean canTower() {
        if (!Utils.nullCheck() || !Utils.jumpDown()) {
            return false;
        }
        else if (disableWhileHurt.isToggled() && mc.thePlayer.hurtTime >= 9) {
            return false;
        }
        else if (disableWhileCollided.isToggled() && mc.thePlayer.isCollidedHorizontally) {
            return false;
        }
        else if (modulesEnabled()) {
            return true;
        }
        return false;
    }

    private boolean modulesEnabled() {
        return  ((ModuleManager.safeWalk.isEnabled() && ModuleManager.safeWalk.tower.isToggled() && SafeWalk.canSafeWalk()) || (ModuleManager.scaffold.isEnabled() && ModuleManager.scaffold.tower.isToggled()));
    }

    public boolean canSprint() {
        return canTower() && this.sprintJumpForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()) && Utils.jumpDown();
    }

    private boolean diagonal() {
        return (Math.abs(mc.thePlayer.motionX) > 0.05 && Math.abs(mc.thePlayer.motionZ) > 0.05);
    }
}
