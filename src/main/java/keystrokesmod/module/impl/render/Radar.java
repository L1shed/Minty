package keystrokesmod.module.impl.render;

import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Radar extends Module {
    private ButtonSetting showGUI;
    private ButtonSetting tracerLines;
    private int scale = 2;
    private int rectColor = new Color(0, 0, 0, 125).getRGB();
    public Radar() {
        super("Radar", category.render);
        this.registerSetting(showGUI = new ButtonSetting("Show in GUI", false));
        this.registerSetting(tracerLines = new ButtonSetting("Show tracer lines", false));
    }

    public void onUpdate() {
        this.scale = new ScaledResolution(mc).getScaleFactor();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen instanceof ClickGui) {
            return;
        }
        if (!showGUI.isToggled() && (mc.currentScreen != null || mc.gameSettings.showDebugInfo)) {
            return;
        }
        int miX = 5, miY = 70;
        int maX = miX + 100, maY = miY + 100;
        Gui.drawRect(miX, miY, maX, maY, rectColor);
        // drawing horizontal lines
        Gui.drawRect(miX - 1, miY - 1, maX + 1, miY, -1); // top
        Gui.drawRect(miX - 1, maY, maX + 1, maY + 1, -1); // bottom
        // drawing vertical lines
        Gui.drawRect(miX - 1, miY, miX, maY, -1); // left
        Gui.drawRect(maX, miY, maX + 1, maY, -1); // right
        RenderUtils.draw2DPolygon(maX / 2 + 3, miY + 52, 5f, 3, -1); // self
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(miX * scale, mc.displayHeight - scale * 170, maX * scale - (scale * 5), scale * 100);

        for (EntityPlayer en : mc.theWorld.playerEntities) {
            if (en == mc.thePlayer) {
                continue;
            }
            double dist_sq = mc.thePlayer.getDistanceSqToEntity(en);
            if (dist_sq > 360) {
                continue;
            }
            double x = en.posX - mc.thePlayer.posX, z = en.posZ - mc.thePlayer.posZ;
            double calc = Math.atan2(x, z) * 57.2957795131f;
            double angle = ((mc.thePlayer.rotationYaw + calc) % 360) * 0.01745329251f;
            double hypotenuse = dist_sq / 5;
            double x_shift = hypotenuse * Math.sin(angle), y_shift = hypotenuse * Math.cos(angle);
            RenderUtils.draw2DPolygon(maX / 2 + 3 - x_shift, miY + 52 - y_shift, 3f, 4, Color.red.getRGB());
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }
}
