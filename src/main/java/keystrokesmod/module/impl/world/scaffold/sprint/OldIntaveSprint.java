package keystrokesmod.module.impl.world.scaffold.sprint;

import keystrokesmod.event.JumpEvent;
import keystrokesmod.module.impl.world.Scaffold;
import keystrokesmod.module.impl.world.scaffold.IScaffoldSprint;
import keystrokesmod.utility.MoveUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class OldIntaveSprint extends IScaffoldSprint {
    public OldIntaveSprint(String name, @NotNull Scaffold parent) {
        super(name, parent);
    }

    @SubscribeEvent
    public void onJump(@NotNull JumpEvent event) {
        event.setCanceled(true);
        mc.thePlayer.motionY = MoveUtil.jumpMotion();
    }

    @Override
    public boolean isSprint() {
        return !mc.thePlayer.onGround;
    }
}
