package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.RenderUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

public class Xray extends Module {
    private SliderSetting range;
    private SliderSetting rate;
    private ButtonSetting iron;
    private ButtonSetting gold;
    private ButtonSetting diamond;
    private ButtonSetting emerald;
    private ButtonSetting lapis;
    private ButtonSetting redstone;
    private ButtonSetting coal;
    private ButtonSetting spawner;
    private ButtonSetting obsidian;
    private java.util.Timer t;
    private List<BlockPos> blocks;

    public Xray() {
        super("Xray", Module.category.render);
        this.registerSetting(range = new SliderSetting("Range", 20.0D, 5.0D, 50.0D, 1.0D));
        this.registerSetting(rate = new SliderSetting("Rate", 0.5, 0.1, 3.0, 0.1));
        this.registerSetting(coal = new ButtonSetting("Coal", true));
        this.registerSetting(diamond = new ButtonSetting("Diamond", true));
        this.registerSetting(emerald = new ButtonSetting("Emerald", true));
        this.registerSetting(gold = new ButtonSetting("Gold", true));
        this.registerSetting(iron = new ButtonSetting("Iron", true));
        this.registerSetting(lapis = new ButtonSetting("Lapis", true));
        this.registerSetting(obsidian = new ButtonSetting("Obsidian", true));
        this.registerSetting(redstone = new ButtonSetting("Redstone", true));
        this.registerSetting(spawner = new ButtonSetting("Spawner", true));
    }

    public void onEnable() {
        this.blocks = new ArrayList();
        (this.t = new java.util.Timer()).scheduleAtFixedRate(this.t(), 0L, (long) (rate.getInput() * 1000));
    }

    public void onDisable() {
        if (this.t != null) {
            this.t.cancel();
            this.t.purge();
            this.t = null;
        }
    }

    private TimerTask t() {
        TimerTask t = new TimerTask() {
            public void run() {
                blocks.clear();
                int ra = (int) range.getInput();

                for (int y = ra; y >= -ra; --y) {
                    for (int x = -ra; x <= ra; ++x) {
                        for (int z = -ra; z <= ra; ++z) {
                            BlockPos p = new BlockPos(Module.mc.thePlayer.posX + (double) x, Module.mc.thePlayer.posY + (double) y, Module.mc.thePlayer.posZ + (double) z);
                            Block bl = Module.mc.theWorld.getBlockState(p).getBlock();
                            if (iron.isToggled() && bl.equals(Blocks.iron_ore) || gold.isToggled() && bl.equals(Blocks.gold_ore) || diamond.isToggled() && bl.equals(Blocks.diamond_ore) || emerald.isToggled() && bl.equals(Blocks.emerald_ore) || lapis.isToggled() && bl.equals(Blocks.lapis_ore) || redstone.isToggled() && bl.equals(Blocks.redstone_ore) || coal.isToggled() && bl.equals(Blocks.coal_ore) || spawner.isToggled() && bl.equals(Blocks.mob_spawner) || obsidian.isToggled() && bl.equals(Blocks.obsidian)) {
                                blocks.add(p);
                            }
                        }
                    }
                }
            }
        };
        return t;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent ev) {
        if (Utils.nullCheck() && !this.blocks.isEmpty()) {
            List<BlockPos> tRen = new ArrayList(this.blocks);
            Iterator var3 = tRen.iterator();

            while (var3.hasNext()) {
                BlockPos p = (BlockPos) var3.next();
                this.drawBox(p);
            }
        }
    }

    private void drawBox(BlockPos p) {
        int[] rgb = this.getColor(mc.theWorld.getBlockState(p).getBlock());
        if (rgb[0] + rgb[1] + rgb[2] != 0) {
            RenderUtils.renderBlock(p, (new Color(rgb[0], rgb[1], rgb[2])).getRGB(), false, true);
        }

    }

    private int[] getColor(Block b) {
        int red = 0;
        int green = 0;
        int blue = 0;
        if (b.equals(Blocks.iron_ore)) {
            red = 255;
            green = 255;
            blue = 255;
        } else if (b.equals(Blocks.gold_ore)) {
            red = 255;
            green = 255;
        } else if (b.equals(Blocks.diamond_ore)) {
            green = 220;
            blue = 255;
        } else if (b.equals(Blocks.emerald_ore)) {
            red = 35;
            green = 255;
        } else if (b.equals(Blocks.lapis_ore)) {
            green = 50;
            blue = 255;
        } else if (b.equals(Blocks.redstone_ore)) {
            red = 255;
        } else if (b.equals(Blocks.mob_spawner)) {
            red = 30;
            blue = 135;
        }

        return new int[]{red, green, blue};
    }
}
