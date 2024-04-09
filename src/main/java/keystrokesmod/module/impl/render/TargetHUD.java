package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public class TargetHUD extends Module {
    private DescriptionSetting description;
    private SliderSetting theme;
    private ButtonSetting renderEsp;
    private ButtonSetting showStatus;
    public TargetHUD() {
        super("TargetHUD", category.render);
        this.registerSetting(description = new DescriptionSetting("Only works with KillAura."));
        this.registerSetting(theme = new SliderSetting("Theme", Theme.themes, 0));
        this.registerSetting(renderEsp = new ButtonSetting("Render ESP", true));
        this.registerSetting(showStatus = new ButtonSetting("Show win or loss", true));
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END && Utils.nullCheck()) {
            if (mc.currentScreen == null) {

            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent renderWorldLastEvent) {
        if (!renderEsp.isToggled() || !Utils.nullCheck()) {
            return;
        }
        if (KillAura.target != null) {
            //RenderUtils.renderEntity(KillAura.target, 2, 0.0, 0.0, ab.b.e(0.0), false);
        }
    }

    /*private void drawTargetHUD(Timer cd, String string, double n) {
        if (showStatus.isToggled()) {
            string = string + " " + ((n <= Utils.getCompleteHealth(mc.thePlayer) / mc.thePlayer.getMaxHealth()) ? "§aW" : "§cL");
        }
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int n2 = 8;
        final int n3 = mc.fontRendererObj.getStringWidth(string) + n2;
        final int n4 = scaledResolution.getScaledWidth() / 2 - n3 / 2 + 70;
        final int n5 = scaledResolution.getScaledHeight() / 2 + 15 + 30;
        final int n6 = n4 - n2;
        final int n7 = n5 - n2;
        final int n8 = n4 + n3;
        final int n9 = n5 + (mc.fontRendererObj.FONT_HEIGHT + 5) - 6 + n2;
        final int n10 = (cd == null) ? 255 : (255 - cd.a(0, 255, 1));
        if (n10 > 0) {
            final int n11 = (n10 > 110) ? 110 : n10;
            final int n12 = (n10 > 210) ? 210 : n10;
            final int[] array = (int[])ab.b.d();
            go.c((float)n6, (float)n7, (float)n8, (float)(n9 + 13), 10.0f, cz.k(Color.black.getRGB(), n11), cz.k(array[0], n10), cz.k(array[1], n10));
            final int n13 = n6 + 6;
            final int n14 = n8 - 6;
            final int n15 = n9;
            go.j((float)n13, (float)n15, (float)n14, (float)(n15 + 5), 4.0f, cz.k(Color.black.getRGB(), n11));
            final int k = cz.k(array[0], n12);
            final int n16 = (n > 0.15) ? cz.k(array[1], n12) : k;
            go.h((float)n13, (float)n15, (float)(int)(n14 + (n13 - n14) * (1.0 - ((n < 0.05) ? 0.05 : n))), (float)(n15 + 5), 4.0f, k, k, n16, n16);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            mc.fontRendererObj.drawString(string, (float)n4, (float)n5, (cj.b.c & 0xFFFFFF) | cz.c(n10 + 15) << 24, true);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }*/
}
