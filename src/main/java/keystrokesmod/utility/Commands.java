package keystrokesmod.utility;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.minigames.DuelsStats;
import keystrokesmod.module.impl.other.FakeChat;
import keystrokesmod.module.impl.other.NameHider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class Commands {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private static boolean f = true;
   private static final List<Integer> cs = Arrays.asList((new Color(170, 107, 148, 50)).getRGB(), (new Color(122, 158, 134, 50)).getRGB(), (new Color(16, 16, 16, 50)).getRGB(), (new Color(64, 114, 148, 50)).getRGB());
   private static int ccs = 0;
   private static int lccs = -1;
   public static List<String> rs = new ArrayList();
   private static final String invSyn = "&cInvalid syntax.";
   private static final String invCom = "&cInvalid command.";

   public static void rCMD(String c) {
      if (!c.isEmpty()) {
         String cm = c.toLowerCase();
         boolean hasArgs = c.contains(" ");
         String[] args = hasArgs ? c.split(" ") : null;
         String n;
         if (cm.startsWith("setkey".toLowerCase())) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

            print("Setting...", 1);
            n = args[1];
            Raven.getExecutor().execute(() -> {
               if (URLUtils.isHypixelKeyValid(n)) {
                  URLUtils.k = n;
                  print("&a" + "success!", 0);
               } else {
                  print("&c" + "Invalid key.", 0);
               }

            });
         } else if (cm.startsWith("nick")) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

            DuelsStats.nk = args[1];
            print("&aNick has been set to:", 1);
            print("\"" + DuelsStats.nk + "\"", 0);
         } else if (cm.startsWith("cname")) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

            NameHider.n = args[1];
            print("&a" + Utils.uf("name") + "Nick has been set to:".substring(4), 1);
            print("\"" + NameHider.n + "\"", 0);
         } else if (cm.startsWith(FakeChat.command)) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            n = c.replaceFirst(FakeChat.command, "").substring(1);
            if (n.isEmpty() || n.equals("\\n")) {
               print(FakeChat.c4, 1);
               return;
            }

            FakeChat.msg = n;
            print("&aMessage set!", 1);
         } else if (cm.startsWith("Duels".toLowerCase())) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

            if (URLUtils.k.isEmpty()) {
               print("&cAPI Key is empty!", 1);
               print("Use \"setkey [api_key]\".", 0);
               return;
            }

            n = args[1];
            print("Retrieving data...", 1);
            Raven.getExecutor().execute(() -> {
               int[] s = ProfileUtils.getHypixelStats(n, ProfileUtils.DM.OVERALL);
               if (s != null) {
                  if (s[0] == -1) {
                     print("&c" + (n.length() > 16 ? n.substring(0, 16) + "..." : n) + " does not exist!", 0);
                  } else {
                     double wlr = s[1] != 0 ? Utils.rnd((double)s[0] / (double)s[1], 2) : (double)s[0];
                     print("&e" + n + " stats:", 1);
                     print("Wins: " + s[0], 0);
                     print("Losses: " + s[1], 0);
                     print("WLR: " + wlr, 0);
                     print("Winstreak: " + s[2], 0);
                     print("Threat: " + DuelsStats.gtl(s[0], s[1], wlr, s[2]).substring(2), 0);
                  }
               } else {
                  print("&cThere was an error.", 0);
               }

            });
         } else if (cm.startsWith("ping")) {
            Ping.checkPing();
         } else if (cm.startsWith("clear")) {
            rs.clear();
         } else if (cm.startsWith("hide")) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

             for (Module module : Raven.getModuleManager().getModules()) {
                String name = module.getName().toLowerCase().replace(" ", "");
                if (name.equals(args[1].toLowerCase())) {
                   module.setVisibility(false);
                   print(module.getName() + " is now hidden in HUD", 1);
                }
             }
         } else if (cm.startsWith("show")) {
            if (!hasArgs) {
               print(invSyn, 1);
               return;
            }

            if (args.length != 2) {
               print(invSyn, 1);
               return;
            }

            for (Module module : Raven.getModuleManager().getModules()) {
               String name = module.getName().toLowerCase().replace(" ", "");
               if (name.equals(args[1].toLowerCase())) {
                  module.setVisibility(true);
                  print(module.getName() + " is now visible in HUD", 1);
               }
            }
         } else if (cm.startsWith("Debug".toLowerCase())) {
            Raven.debugger = !Raven.debugger;
            print("Debug " + (Raven.debugger ? "enabled" : "disabled") + ".", 1);
         } else if (!cm.startsWith("help") && !cm.startsWith("?")) {
            if (cm.startsWith("shoutout")) {
               print("&eCelebrities:", 1);
               print("- hevex", 0);
               print("- jc", 0);
               print("- mood", 0);
               print("- charlotte", 0);
            } else {
               print(invCom + " (" + (cm.length() > 5 ? cm.substring(0, 5) + "..." : cm) + ")", 1);
            }
         } else {
            print("&eAvailable commands:", 1);
            print("1 setkey [key]", 0);
            print("2 duels [player]", 0);
            print("3 nick [name]", 0);
            print("4 ping", 0);
            print("5 hide [module]", 0);
            print("6 show [module]", 0);
            print("&eModule-specific:", 0);
            print("1 cname [name]", 0);
            print("2 " + FakeChat.command + " [msg]", 0);
         }

      }
   }

   public static void print(String m, int t) {
      if (t == 1 || t == 2) {
         rs.add("");
      }

      rs.add(m);
      if (t == 2 || t == 3) {
         rs.add("");
      }

   }

   public static void rc(FontRenderer fr, int h, int w, int s) {
      int x = w - 195;
      int y = h - 130;
      int sY = h - 345;
      int sH = 230;
      GL11.glEnable(3089);
      int mw = w * s;
      GL11.glScissor(0, mc.displayHeight - (sY + sH) * s, mw - (mw < 2 ? 0 : 2), sH * s - 2);
      RenderUtils.db(1000, 1000, ccs);
      rss(fr, rs, x, y);
      GL11.glDisable(3089);
   }

   private static void rss(FontRenderer fr, List<String> rs, int x, int y) {
      if (f) {
         f = false;
         print("Welcome,", 0);
         print("Use \"help\" for help.", 0);
      }

      if (!rs.isEmpty()) {
         for(int i = rs.size() - 1; i >= 0; --i) {
            String s = rs.get(i);
            int c = -1;
            if (s.contains("&a")) {
               s = s.replace("&a", "");
               c = Color.green.getRGB();
            } else if (s.contains("&c")) {
               s = s.replace("&c", "");
               c = Color.red.getRGB();
            } else if (s.contains("&e")) {
               s = s.replace("&e", "");
               c = Color.yellow.getRGB();
            }

            fr.drawString(s, x, y, c);
            y -= fr.FONT_HEIGHT + 5;
         }

      }
   }

   public static void setccs() {
      int val = Utils.rand().nextInt(cs.size());
      if (val == lccs) {
         val += val == 3 ? -3 : 1;
      }

      lccs = val;
      ccs = (Integer)cs.get(val);
   }

   public static void od() {
      Ping.rs();
   }
}
