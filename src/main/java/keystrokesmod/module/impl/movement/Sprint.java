package keystrokesmod.module.impl.movement;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PrePlayerInput;
import keystrokesmod.mixins.impl.client.KeyBindingAccessor;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Sprint extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", new String[]{"Legit", "Hypixel"}, 0);

    public Sprint() {
        super("Sprint", Module.category.movement, 0);
        this.registerSetting(mode);
    }

    @SubscribeEvent
    public void p(PlayerTickEvent e) {
        if (Utils.nullCheck() && mode.getInput() == 0 && mc.inGameHasFocus) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    @SubscribeEvent
    public void onStrafe(PrePlayerInput event) {
        if (mode.getInput() != 1) return;

        ((KeyBindingAccessor) mc.gameSettings.keyBindSprint).setPressed(true);

        if (MoveUtil.isMoving()) {
            mc.thePlayer.setSprinting(true);

            float forward = event.getForward();
            float strafe = event.getStrafe();
            if (Math.abs(strafe) == 1)
                strafe *= 1.3F;
            if (Math.abs(forward) == 1)
                forward *= 1.3F;
            event.setForward(forward);
            event.setStrafe(strafe);
        }
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        if (mode.getInput() != 1) return;

        if (mc.thePlayer.moveForward <= 0)
            event.setSprinting(false);
    }
}
