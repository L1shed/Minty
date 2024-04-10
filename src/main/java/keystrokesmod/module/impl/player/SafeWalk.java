package keystrokesmod.module.impl.player;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class SafeWalk extends Module {
    public static ButtonSetting shift, blocksOnly, pitchCheck, disableOnForward;
    private static boolean isSneaking = false;

    public SafeWalk() {
        super("SafeWalk", Module.category.player, 0);
        this.registerSetting(blocksOnly = new ButtonSetting("Blocks only", true));
        this.registerSetting(disableOnForward = new ButtonSetting("Disable on forward", false));
        this.registerSetting(pitchCheck = new ButtonSetting("Pitch check", false));
        this.registerSetting(shift = new ButtonSetting("Shift", false));
    }

    public void onDisable() {
        if (shift.isToggled() && Utils.overAir()) {
            this.setSneakState(false);
        }
        isSneaking = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        if (!shift.isToggled() || !Utils.nullCheck()) {
            return;
        }
        if (mc.thePlayer.onGround && Utils.overAir()) {
            if (blocksOnly.isToggled()) {
                final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
                if (getHeldItem == null || !(getHeldItem.getItem() instanceof ItemBlock)) {
                    this.setSneakState(false);
                    return;
                }
            }
            if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                this.setSneakState(false);
                return;
            }
            if (pitchCheck.isToggled() && mc.thePlayer.rotationPitch < 70.0f) {
                this.setSneakState(false);
                return;
            }
            this.setSneakState(true);
        } else if (this.isSneaking) {
            this.setSneakState(false);
        }
        if (this.isSneaking && mc.thePlayer.capabilities.isFlying) {
            this.setSneakState(false);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(final GuiOpenEvent guiOpenEvent) {
        if (shift.isToggled() && guiOpenEvent.gui == null) {
            this.isSneaking = mc.thePlayer.isSneaking();
        }
    }

    private void setSneakState(boolean sh) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sh);
    }

    public static boolean canSafeWalk() {
        if (ModuleManager.safeWalk != null && ModuleManager.safeWalk.isEnabled()) {
            if (disableOnForward.isToggled() && Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
                return false;
            }
            if (pitchCheck.isToggled() && mc.thePlayer.rotationPitch < 70) {
                return false;
            }
            if (blocksOnly.isToggled() && (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))) {
                return false;
            }
            return true;
        }
        return false;
    }
}
