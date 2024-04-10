package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Iterator;

public class PlayerESP extends Module {
    public static DescriptionSetting g;
    public static SliderSetting a;
    public static SliderSetting b;
    public static SliderSetting c;
    public static SliderSetting i;
    public static SliderSetting j;
    public static ButtonSetting d;
    public static ButtonSetting f;
    public static ButtonSetting h;
    public static ButtonSetting t1;
    public static ButtonSetting t2;
    public static ButtonSetting t3;
    public static ButtonSetting t4;
    public static ButtonSetting t5;
    public static ButtonSetting t6;
    private int rgb_c = 0;

    public PlayerESP() {
        super("PlayerESP", Module.category.render, 0);
        this.registerSetting(a = new SliderSetting("Red", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(b = new SliderSetting("Green", 255.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(c = new SliderSetting("Blue", 0.0D, 0.0D, 255.0D, 1.0D));
        this.registerSetting(d = new ButtonSetting("Rainbow", false));
        this.registerSetting(g = new DescriptionSetting("ESP Types"));
        this.registerSetting(t3 = new ButtonSetting("2D", false));
        this.registerSetting(t5 = new ButtonSetting("Arrow", false));
        this.registerSetting(t1 = new ButtonSetting("Box", false));
        this.registerSetting(t4 = new ButtonSetting("Health", true));
        this.registerSetting(t6 = new ButtonSetting("Ring", false));
        this.registerSetting(t2 = new ButtonSetting("Shaded", false));
        this.registerSetting(i = new SliderSetting("Expand", 0.0D, -0.3D, 2.0D, 0.1D));
        this.registerSetting(j = new SliderSetting("X-Shift", 0.0D, -35.0D, 10.0D, 1.0D));
        this.registerSetting(f = new ButtonSetting("Show invis", true));
        this.registerSetting(h = new ButtonSetting("Red on damage", true));
    }

    public void onDisable() {
        RenderUtils.ring_c = false;
    }

    public void guiUpdate() {
        this.rgb_c = (new Color((int) a.getInput(), (int) b.getInput(), (int) c.getInput())).getRGB();
    }

    @SubscribeEvent
    public void r1(RenderWorldLastEvent e) {
        if (Utils.nullCheck()) {
            int rgb = d.isToggled() ? 0 : this.rgb_c;
            Iterator var3;
            if (Raven.debugger) {
                var3 = mc.theWorld.loadedEntityList.iterator();

                while (var3.hasNext()) {
                    Entity en = (Entity) var3.next();
                    if (en instanceof EntityLivingBase && en != mc.thePlayer) {
                        this.r(en, rgb);
                    }
                }

            } else {
                var3 = mc.theWorld.playerEntities.iterator();

                while (true) {
                    EntityPlayer en;
                    do {
                        do {
                            do {
                                if (!var3.hasNext()) {
                                    return;
                                }

                                en = (EntityPlayer) var3.next();
                            } while (en == mc.thePlayer);
                        } while (en.deathTime != 0);
                    } while (!f.isToggled() && en.isInvisible());

                    if (!AntiBot.isBot(en)) {
                        this.r(en, rgb);
                    }
                }
            }
        }
    }

    private void r(Entity en, int rgb) {
        if (t1.isToggled()) {
            RenderUtils.renderEntity(en, 1, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

        if (t2.isToggled()) {
            RenderUtils.renderEntity(en, 2, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

        if (t3.isToggled()) {
            RenderUtils.renderEntity(en, 3, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

        if (t4.isToggled()) {
            RenderUtils.renderEntity(en, 4, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

        if (t5.isToggled()) {
            RenderUtils.renderEntity(en, 5, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

        if (t6.isToggled()) {
            RenderUtils.renderEntity(en, 6, i.getInput(), j.getInput(), rgb, h.isToggled());
        }

    }
}
