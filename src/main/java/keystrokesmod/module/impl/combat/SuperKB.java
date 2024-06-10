package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.TimeUnit;

public class SuperKB extends Module {
    private final SliderSetting chance;
    private final SliderSetting delay;
    private final SliderSetting rePressDelay;
    private long lastFinish = -1;
    private final ButtonSetting playersOnly;
    private final ButtonSetting sprintReset;
    private final ButtonSetting sneak;

    public SuperKB() {
        super("SuperKB", category.combat);
        this.registerSetting(chance = new SliderSetting("Chance", 100, 0, 100, 1, "%"));
        this.registerSetting(delay = new SliderSetting("Delay", 500, 200, 750, 1, "ms"));
        this.registerSetting(rePressDelay = new SliderSetting("Re-press delay", 100, 1, 500, 1, "ms"));
        this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
        this.registerSetting(sprintReset = new ButtonSetting("Sprint reset", true));
        this.registerSetting(sneak = new ButtonSetting("Sneak", false));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onAttack(AttackEntityEvent event) {
        final long currentTimeMillis = System.currentTimeMillis();
        if (!Utils.nullCheck() || event.entityPlayer != mc.thePlayer || currentTimeMillis - lastFinish < delay.getInput()) return;
        if (playersOnly.isToggled() && !(event.target instanceof EntityPlayer)) return;
        else if (!(event.target instanceof EntityLivingBase)) return;
        if (((EntityLivingBase) event.target).deathTime != 0) return;
        if (AntiBot.isBot(event.target)) return;

        if (Math.random() > chance.getInput()) return;
        // code
        if (sprintReset.isToggled() && mc.gameSettings.keyBindForward.isKeyDown()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
            Raven.getExecutor().schedule(() -> KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true),
                    (long) rePressDelay.getInput(), TimeUnit.MILLISECONDS);
        }
        if (sneak.isToggled() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            Raven.getExecutor().schedule(() -> KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false),
                    (long) rePressDelay.getInput(), TimeUnit.MILLISECONDS);
        }

        lastFinish = currentTimeMillis;
    }

    @Override
    public String getInfo() {
        return sprintReset.isToggled() && sneak.isToggled() ? "LegitFast" : sneak.isToggled() ? "LegitSneak" : "Legit";
    }
}
