package keystrokesmod.module.setting.utils;

import keystrokesmod.module.setting.interfaces.InputSetting;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModeOnly implements Supplier<Boolean> {
    private final InputSetting modeSetting;
    private final Set<Integer> activeMode;

    public ModeOnly(@NotNull InputSetting modeSetting, int @NotNull ... activeMode) {
        this.modeSetting = modeSetting;
        this.activeMode = new HashSet<>();
        for (int i : activeMode) {
            this.activeMode.add(i);
        }
    }

    @Override
    public Boolean get() {
        return activeMode.contains((int) modeSetting.getInput());
    }
}
