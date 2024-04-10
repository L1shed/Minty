package keystrokesmod.module.impl.minigames;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MurderMystery extends Module {
    public static ButtonSetting a;
    public static ButtonSetting b;
    public static ButtonSetting c;
    private static final List<EntityPlayer> mur = new ArrayList();
    private static final List<EntityPlayer> det = new ArrayList();
    private final String c1 = "MURDER";
    private final String c2 = "MYSTERY";
    private final String c3 = "Role:";
    private final String c4 = "&7[&cALERT&7]";
    private final String c5 = "note.pling";
    private final String c6 = "is a murderer!";
    private final String c7 = "has a bow!";

    public MurderMystery() {
        super("Murder Mystery", Module.category.minigames, 0);
        this.registerSetting(a = new ButtonSetting("Alert", true));
        this.registerSetting(b = new ButtonSetting("Search detectives", true));
        this.registerSetting(c = new ButtonSetting("Announce murderer", false));
    }

    @SubscribeEvent
    public void o(RenderWorldLastEvent e) {
        if (Utils.nullCheck()) {
            if (ModuleManager.playerESP.isEnabled()) {
                ModuleManager.playerESP.disable();
            }

            if (!this.imm()) {
                this.c();
            } else {
                Iterator var2 = mc.theWorld.playerEntities.iterator();

                while (true) {
                    EntityPlayer en;
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    return;
                                }

                                en = (EntityPlayer) var2.next();
                            } while (en == mc.thePlayer);
                        } while (en.isInvisible());
                    } while (AntiBot.isBot(en));

                    if (en.getHeldItem() != null && en.getHeldItem().hasDisplayName()) {
                        Item i = en.getHeldItem().getItem();
                        if (i instanceof ItemSword || i instanceof ItemAxe || en.getHeldItem().getDisplayName().contains("aKnife")) {
                            if (!mur.contains(en)) {
                                mur.add(en);
                                if (a.isToggled()) {
                                    mc.thePlayer.playSound(this.c5, 1.0F, 1.0F);
                                    Utils.sendMessage(this.c4 + " &e" + en.getName() + " &3" + this.c6);
                                }

                                if (c.isToggled()) {
                                    mc.thePlayer.sendChatMessage(en.getName() + " " + this.c6);
                                }
                            } else if (i instanceof ItemBow && b.isToggled() && !det.contains(en)) {
                                det.add(en);
                                if (a.isToggled()) {
                                    Utils.sendMessage(this.c4 + " &e" + en.getName() + " &3" + this.c7);
                                }

                                if (c.isToggled()) {
                                    mc.thePlayer.sendChatMessage(en.getName() + " " + this.c7);
                                }
                            }
                        }
                    }

                    int rgb = Color.green.getRGB();
                    if (mur.contains(en)) {
                        rgb = Color.red.getRGB();
                    } else if (det.contains(en)) {
                        rgb = Color.orange.getRGB();
                    }

                    RenderUtils.renderEntity(en, 2, 0.0D, 0.0D, rgb, false);
                }
            }
        }
    }

    private boolean imm() {
        if (Utils.isHypixel()) {
            if (mc.thePlayer.getWorldScoreboard() == null || mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1) == null) {
                return false;
            }

            String d = mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1).getDisplayName();
            if (!d.contains(this.c1) && !d.contains(this.c2)) {
                return false;
            }

            Iterator var2 = Utils.gsl().iterator();

            while (var2.hasNext()) {
                String l = (String) var2.next();
                String s = Utils.stripColor(l);
                if (s.contains(this.c3)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void c() {
        mur.clear();
        det.clear();
    }
}
