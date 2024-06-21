package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.script.classes.Vec3;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ContainerChest;
import org.lwjgl.input.Keyboard;

import static keystrokesmod.module.ModuleManager.*;

public class InvMove extends Module {
    public static final String[] MODES = {"Normal", "Blink"};
    private final SliderSetting mode;
    private final ButtonSetting allowSprint;
    private final ButtonSetting allowSneak;
    private final ButtonSetting chestNameCheck;
    private final ButtonSetting targetNearbyCheck;
    private boolean blinking = false;

    public InvMove() {
        super("InvMove", category.movement);
        this.registerSetting(new DescriptionSetting("Allow you move in inventory."));
        this.registerSetting(mode = new SliderSetting("Mode", MODES, 0));
        this.registerSetting(allowSprint = new ButtonSetting("Allow sprint", false));
        this.registerSetting(allowSneak = new ButtonSetting("Allow sneak", false));
        this.registerSetting(chestNameCheck = new ButtonSetting("Chest name check", true));
        this.registerSetting(targetNearbyCheck = new ButtonSetting("Target nearby check", true));
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer && nameCheck() && targetNearbyCheck() && !scaffold.isEnabled()) {
            if ((int) mode.getInput() == 1) {
                if (!blinking) {
                    blink.enable();
                }
                blinking = true;
            }

            if (allowSprint.isToggled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode()));
            }
            if (allowSneak.isToggled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            }
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Utils.jumpDown());
        } else if ((int) mode.getInput() == 1) {
            if (blinking && blink.isEnabled()) {
                blink.disable();
            }
            blinking = false;
        }
    }

    private boolean nameCheck() {
        if (!chestNameCheck.isToggled()) return true;
        if (!(mc.thePlayer.openContainer instanceof ContainerChest)) return true;

        return ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory().getName().equals("Chest");
    }

    private boolean targetNearbyCheck() {
        if (!targetNearbyCheck.isToggled()) return true;

        return !Utils.isTargetNearby();
    }

    @Override
    public void onDisable() {
        if (blinking && blink.isEnabled()) {
            blink.disable();
        }
        blinking = false;
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }
}
