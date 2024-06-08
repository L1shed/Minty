package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import static keystrokesmod.module.ModuleManager.blink;

public class InvMove extends Module {
    public static final String[] MODES = {"Normal", "Blink"};
    private final SliderSetting mode;
    private boolean blinking = false;
    public InvMove() {
        super("InvMove", category.movement);
        this.registerSetting(new DescriptionSetting("Allow you move in inventory."));
        this.registerSetting(mode = new SliderSetting("Mode", MODES, 0));
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) {
            if (!blinking) {
                blink.enable();
            }
            blinking = true;

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Utils.jumpDown());
        } else {
            if (blinking && blink.isEnabled()) {
                blink.disable();
            }
            blinking = false;
        }
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
