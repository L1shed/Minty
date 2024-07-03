package keystrokesmod.module.impl.world;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ModeSetting;

public class ScaffoldV2 extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", new String[]{"Normal", "Telly"}, 0);

    public ScaffoldV2() {
        super("ScaffoldV2", category.world);
    }
}
