package keystrokesmod.module.impl.fun;

import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.RotationEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.ragebot.rapidfire.StoreRapidFire;
import keystrokesmod.module.impl.fun.antiaim.BackwardAntiAim;
import keystrokesmod.module.impl.fun.antiaim.SnapAntiAim;
import keystrokesmod.module.impl.fun.antiaim.SpinAntiAim;
import keystrokesmod.module.impl.other.RotationHandler;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeValue;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class AntiAim extends Module {
    private final ModeValue mode;
    private final ButtonSetting cancelSprint;
    private final ButtonSetting moveFix;
    private final ButtonSetting onlyWhileSneaking;

    public AntiAim() {
        super("AntiAim", category.fun);
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new SpinAntiAim("Spin", this))
                .add(new BackwardAntiAim("Backward", this))
                .add(new SnapAntiAim("Snap", this))
        );
        this.registerSetting(moveFix = new ButtonSetting("Move fix", false));
        this.registerSetting(cancelSprint = new ButtonSetting("Cancel sprint", false));
        this.registerSetting(onlyWhileSneaking = new ButtonSetting("Only while sneaking", false));
    }

    @Override
    public void onEnable() {
        mode.enable();
    }

    @Override
    public void onDisable() {
        mode.disable();
    }

    @SubscribeEvent
    public void onRotation(@NotNull RotationEvent event) {
        if (canAntiAim())
            event.setMoveFix(moveFix.isToggled() ? RotationHandler.MoveFix.Silent : RotationHandler.MoveFix.None);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPreMotion(PreMotionEvent event) {
        if (canAntiAim() && cancelSprint.isToggled()) {
            event.setSprinting(false);
        }
    }

    public boolean canAntiAim() {
        return (!onlyWhileSneaking.isToggled() || mc.thePlayer.isSneaking()) && StoreRapidFire.canAntiAim();
    }
}
