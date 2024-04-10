package keystrokesmod.module.impl.combat;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Iterator;

public class AimAssist extends Module {
    private SliderSetting speed;
    private SliderSetting fov;
    private SliderSetting distance;
    private ButtonSetting clickAim;
    private ButtonSetting weaponOnly;
    private ButtonSetting aimInvis;
    private ButtonSetting blatantMode;

    public AimAssist() {
        super("AimAssist", Module.category.combat, 0);
        this.registerSetting(speed = new SliderSetting("Speed", 45.0D, 1.0D, 100.0D, 1.0D));
        this.registerSetting(fov = new SliderSetting("FOV", 90.0D, 15.0D, 180.0D, 1.0D));
        this.registerSetting(distance = new SliderSetting("Distance", 4.5D, 1.0D, 10.0D, 0.5D));
        this.registerSetting(clickAim = new ButtonSetting("Click aim", true));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(aimInvis = new ButtonSetting("Aim invis", false));
        this.registerSetting(blatantMode = new ButtonSetting("Blatant mode", false));
    }

    public void onUpdate() {
        if (mc.currentScreen == null && mc.inGameHasFocus) {
            if (!weaponOnly.isToggled() || Utils.holdingWeapon()) {
                if (!clickAim.isToggled() || Utils.ilc()) {
                    Entity en = this.getEnemy();
                    if (en != null) {
                        if (Raven.debugger) {
                            Utils.sendMessage(this.getName() + " &e" + en.getName());
                        }

                        if (blatantMode.isToggled()) {
                            Utils.aim(en, 0.0F, false);
                        } else {
                            double n = Utils.n(en);
                            if (n > 1.0D || n < -1.0D) {
                                float val = (float) (-(n / (101.0D - speed.getInput())));
                                mc.thePlayer.rotationYaw += val;
                            }
                        }
                    }

                }
            }
        }
    }

    public Entity getEnemy() {
        int fov = (int) this.fov.getInput();
        Iterator var2 = mc.theWorld.playerEntities.iterator();

        EntityPlayer en;
        do {
            do {
                do {
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    return null;
                                }

                                en = (EntityPlayer) var2.next();
                            } while (en == mc.thePlayer);
                        } while (en.deathTime != 0);
                    } while (!aimInvis.isToggled() && en.isInvisible());
                } while ((double) mc.thePlayer.getDistanceToEntity(en) > distance.getInput());
            } while (AntiBot.isBot(en));
        } while (!blatantMode.isToggled() && !Utils.fov(en, (float) fov));

        return en;
    }
}
