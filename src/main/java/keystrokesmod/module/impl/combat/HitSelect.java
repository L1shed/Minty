package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import static keystrokesmod.module.ModuleManager.hitSelect;

public class HitSelect extends Module {
    private static long attackTime = -1;
    private final SliderSetting chance;
    private static boolean currentShouldAttack = false;

    public HitSelect() {
        super("HitSelect", category.combat);
        this.registerSetting(new DescriptionSetting("chooses the best time to hit."));
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(@NotNull AttackEntityEvent event) {
        attackTime = System.currentTimeMillis();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent event) {
        currentShouldAttack = Math.random() > hitSelect.chance.getInput() || System.currentTimeMillis() - HitSelect.attackTime >= 400;
    }

    public static boolean canAttack() {
        if (!hitSelect.isEnabled()) return true;
        if (currentShouldAttack) {
            HitSelect.attackTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public static boolean canSwing() {
        if (!hitSelect.isEnabled()) return true;
        return currentShouldAttack;
    }
}
