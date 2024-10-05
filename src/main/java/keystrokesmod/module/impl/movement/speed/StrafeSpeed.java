package keystrokesmod.module.impl.movement.speed;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.impl.movement.Speed;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SubMode;
import keystrokesmod.utility.MoveUtil;
import keystrokesmod.utility.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class StrafeSpeed extends SubMode<Speed> {
    private final ButtonSetting autoJump;
    private final ButtonSetting groundStrafe;

    public StrafeSpeed(String name, @NotNull Speed parent) {
        super(name, parent);
        this.registerSetting(autoJump = new ButtonSetting("Auto jump", true));
        this.registerSetting(groundStrafe = new ButtonSetting("Ground strafe", true));
    }

    @SubscribeEvent
    public void onPreUpdate(@NotNull PreUpdateEvent event) {
        if (parent.noAction() || !MoveUtil.isMoving()) return;

        if (mc.thePlayer.onGround && groundStrafe.isToggled()) {
            MoveUtil.strafe(MoveUtil.getAllowedHorizontalDistance());
        } else {
            MoveUtil.strafe();
        }
        if (mc.thePlayer.onGround && autoJump.isToggled() && !Utils.jumpDown()) {
            mc.thePlayer.jump();
        }
    }

}
