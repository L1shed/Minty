package keystrokesmod.module.impl.combat.ragebot.rapidfire;

import keystrokesmod.module.impl.combat.RageBot;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class LegitRapidFire extends IRapidFire {
    private final Set<Integer> firedSlots = new HashSet<>();

    public LegitRapidFire(String name, @NotNull RageBot parent) {
        super(name, parent);
    }

    @Override
    public int getBestArm() {
        int arm = super.getBestArm();

        if (arm == -1 && !firedSlots.isEmpty()) {
            firedSlots.clear();
            return getBestArm();
        }
        firedSlots.add(arm);

        return arm;
    }

    @Override
    public void onEnable() throws Throwable {
        firedSlots.clear();
    }
}
