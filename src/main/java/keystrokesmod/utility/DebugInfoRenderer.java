package keystrokesmod.utility;

import java.awt.Color;

import keystrokesmod.Raven;
import keystrokesmod.module.impl.player.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class DebugInfoRenderer extends net.minecraft.client.gui.Gui {
   private static Minecraft mc = Minecraft.getMinecraft();

   @SubscribeEvent
   public void onRenderTick(RenderTickEvent ev) {
      if (Raven.debugger && ev.phase == Phase.END && Utils.nullCheck()) {
         if (mc.currentScreen == null) {
            ScaledResolution res = new ScaledResolution(mc);
            double bps = Utils.gbps((Entity)(Freecam.freeEntity == null ? mc.thePlayer : Freecam.freeEntity), 2);
            int rgb;
            if (bps < 10.0D) {
               rgb = Color.green.getRGB();
            } else if (bps < 30.0D) {
               rgb = Color.yellow.getRGB();
            } else if (bps < 60.0D) {
               rgb = Color.orange.getRGB();
            } else if (bps < 160.0D) {
               rgb = Color.red.getRGB();
            } else {
               rgb = Color.black.getRGB();
            }

            String t = bps + "bps";
            int x = res.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(t) / 2;
            int y = res.getScaledHeight() / 2 + 15;
            mc.fontRendererObj.drawString(t, (float)x, (float)y, rgb, false);
         }

      }
   }
}
