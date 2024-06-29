package keystrokesmod.module.impl.world;

import keystrokesmod.mixins.impl.client.KeyBindingAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class LegitScaffold extends Module {
    private final SliderSetting minDelay = new SliderSetting("Min delay", 100, 0, 500, 1, "ms");
    private final SliderSetting maxDelay = new SliderSetting("Max delay", 200, 0, 500, 1, "ms");
    private final ButtonSetting pitchCheck = new ButtonSetting("Pitch check", true);
    private final SliderSetting pitch = new SliderSetting("Pitch", 45, 90, 0, 5, pitchCheck::isToggled);
    private final ButtonSetting onlySPressed = new ButtonSetting("Only S pressed", false);

    private long lastSneakTime = -1;

    public LegitScaffold() {
        super("Legit scaffold", category.world);
        this.registerSetting(minDelay, maxDelay, pitchCheck, pitch, onlySPressed);
    }

    @Override
    public void guiUpdate() {
        Utils.correctValue(minDelay, maxDelay);
    }

    @Override
    public void onDisable() {
        lastSneakTime = -1;
        setSneak(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if (!Utils.nullCheck()) return;

        if ((onlySPressed.isToggled() && !mc.gameSettings.keyBindBack.isKeyDown())
                || (pitchCheck.isToggled() && mc.thePlayer.rotationPitch < pitch.getInput())
        ) {
            setSneak(Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()));
            return;
        }

        final long currentTime = System.currentTimeMillis();
        if (Utils.overAir()) {
            setSneak(true);
            lastSneakTime = currentTime;
        } else if (lastSneakTime != -1
                && currentTime - lastSneakTime > Math.random() * (maxDelay.getInput() - minDelay.getInput()) + minDelay.getInput()) {
            setSneak(false);
            lastSneakTime = -1;
        }
    }

    private void setSneak(boolean sneak) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sneak);
        ((KeyBindingAccessor) mc.gameSettings.keyBindSneak).setPressed(sneak);
    }
}
