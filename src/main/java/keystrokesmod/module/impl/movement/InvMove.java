package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove", category.movement);
    }

    public void onUpdate() {
        if (mc.currentScreen != null) {
            if (mc.currentScreen instanceof GuiChat) {
                return;
            }

            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode()));
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Utils.jumpDown());
            if (Keyboard.isKeyDown(208) && mc.thePlayer.rotationPitch < 90.0F) {
                mc.thePlayer.rotationPitch += 6.0F;
            }
            if (Keyboard.isKeyDown(200) && mc.thePlayer.rotationPitch > -90.0F) {
                mc.thePlayer.rotationPitch -= 6.0F;
            }
            if (Keyboard.isKeyDown(205)) {
                mc.thePlayer.rotationYaw += 6.0F;
            }
            if (Keyboard.isKeyDown(203)) {
                mc.thePlayer.rotationYaw -= 6.0F;
            }
        }
    }
}
