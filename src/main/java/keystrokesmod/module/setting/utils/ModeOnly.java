package keystrokesmod.module.setting.utils;

import keystrokesmod.module.setting.impl.ModeSetting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModeOnly implements Supplier<Boolean> {
    private final ModeSetting modeSetting;
    private final Set<Integer> activeMode;

    public ModeOnly(@NotNull ModeSetting modeSetting, int @NotNull ... activeMode) {
        this.modeSetting = modeSetting;
        this.activeMode = new HashSet<>();
        for (int i : activeMode) {
            this.activeMode.add(i);
        }
    }

    public ModeOnly(ModeSetting modeSetting, List<Integer> activeMode) {
        this.modeSetting = modeSetting;
        this.activeMode = new HashSet<>();
        this.activeMode.addAll(activeMode);
    }

    @Override
    public Boolean get() {
        return activeMode.contains((int) modeSetting.getInput());
    }
    
    public ModeOnly reserve() {
        List<Integer> options = new ArrayList<>(modeSetting.getMax() + 1 - activeMode.size());
        for (int i = 0; i <= modeSetting.getMax(); i++) {
            if (!activeMode.contains(i))
                options.add(i);
        }
        return new ModeOnly(modeSetting, options);
    }
}
