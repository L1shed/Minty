package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ModeSetting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class Fullbright extends Module {
    private ModeSetting mode;
    private double originalGamma;
    private double brightness = 15.0;
    private boolean nightVisionEnabled = false;

    public Fullbright() {
        super("Fullbright", Module.category.render, 0);
        this.registerSetting(mode = new ModeSetting("Mode", new String[]{"Legit", "Gamma"}, 0));
    }

    @Override
    public void onEnable() {
        originalGamma = mc.gameSettings.gammaSetting;

        if (mode.getMode().equals("Gamma")) {
            enableGamma();
        } else if (mode.getMode().equals("Legit")) {
            enableNightVision();
        }
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = originalGamma;
        disableNightVision();
    }

    private void enableGamma() {
        mc.gameSettings.gammaSetting = MathHelper.clamp(originalGamma + 0.1, 0.0, brightness);
    }

    private void enableNightVision() {
        EntityPlayer player = mc.player;
        if (player != null) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION, 999999, 1, false, false));
            nightVisionEnabled = true;
        }
    }

    private void disableNightVision() {
        if (nightVisionEnabled) {
            EntityPlayer player = mc.player;
            if (player != null) {
                player.removePotionEffect(net.minecraft.init.MobEffects.NIGHT_VISION);
            }
            nightVisionEnabled = false;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (mode.getMode().equals("Gamma")) {
            if (mc.gameSettings.gammaSetting < brightness) {
                mc.gameSettings.gammaSetting = Math.min(mc.gameSettings.gammaSetting + 0.1, brightness);
            }
        }
    }
}
