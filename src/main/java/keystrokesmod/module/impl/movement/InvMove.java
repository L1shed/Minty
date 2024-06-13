package keystrokesmod.module.impl.movement;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

import static keystrokesmod.module.ModuleManager.blink;
import static keystrokesmod.module.ModuleManager.scaffold;

public class InvMove extends Module {
    public static final String[] MODES = {"Normal", "Blink"};
    private final SliderSetting mode;
    private final ButtonSetting allowSprint;
    private final ButtonSetting allowSneak;
    private final ButtonSetting chestNameCheck;
    private boolean blinking = false;
    private String currentTitle = "";
    public InvMove() {
        super("InvMove", category.movement);
        this.registerSetting(new DescriptionSetting("Allow you move in inventory."));
        this.registerSetting(mode = new SliderSetting("Mode", MODES, 0));
        this.registerSetting(allowSprint = new ButtonSetting("Allow sprint", false));
        this.registerSetting(allowSneak = new ButtonSetting("Allow sneak", false));
        this.registerSetting(chestNameCheck = new ButtonSetting("Chest name check", true));
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer && nameCheck() && !scaffold.isEnabled()) {
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
        if (!(mc.currentScreen instanceof GuiChest)) return true;

        return currentTitle.equals("Chest");
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onReceivePacket(@NotNull ReceivePacketEvent event) {
        if (event.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow packet = (S2DPacketOpenWindow) event.getPacket();

            this.currentTitle = packet.getWindowTitle().getUnformattedText();
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
