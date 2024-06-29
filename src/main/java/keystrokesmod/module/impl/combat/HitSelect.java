package keystrokesmod.module.impl.combat;

import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import static keystrokesmod.module.ModuleManager.hitSelect;

public class HitSelect extends Module {
    private static final String[] MODES = new String[]{"Pause", "Active"};
    private final ModeSetting mode;
    private final SliderSetting delay;
    private final SliderSetting chance;
    private final ButtonSetting smart;

    private static long attackTime = -1;
    private static boolean currentShouldAttack = false;

    public HitSelect() {
        super("HitSelect", category.combat);
        this.registerSetting(new DescriptionSetting("chooses the best time to hit."));
        this.registerSetting(mode = new ModeSetting("Mode", MODES, 0));
        this.registerSetting(delay = new SliderSetting("Delay", 420, 400, 500, 1));
        this.registerSetting(chance = new SliderSetting("Chance", 80, 0, 100, 1));
        this.registerSetting(smart = new ButtonSetting("Smart", true));
    }

    @Override
    public String getInfo() {
        return MODES[(int) mode.getInput()];
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(@NotNull AttackEntityEvent event) {
        if (mode.getInput() == 1 && !currentShouldAttack && (!smart.isToggled()
                || !(event.target instanceof EntityLivingBase)
                || ((EntityLivingBase) event.target).hurtTime > 0)) {
            event.setCanceled(true);
            return;
        }

        attackTime = System.currentTimeMillis();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPreUpdate(PreUpdateEvent event) {
        currentShouldAttack = Math.random() > hitSelect.chance.getInput() || System.currentTimeMillis() - HitSelect.attackTime >= hitSelect.delay.getInput();
    }

    public static boolean canAttack(Entity target) {
        if (target instanceof EntityLivingBase)
            if (hitSelect.smart.isToggled() && ((EntityLivingBase) target).hurtTime == 0 && Math.random() <= hitSelect.chance.getInput())
                return true;

        return canSwing();
    }

    public static boolean canSwing() {
        if (!hitSelect.isEnabled() || hitSelect.mode.getInput() == 1) return true;
        return currentShouldAttack;
    }
}
