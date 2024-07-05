package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.module.ModuleManager;
import keystrokesmod.module.impl.player.ChestStealer;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.utility.Timer;
import keystrokesmod.utility.font.Font;
import keystrokesmod.utility.font.FontManager;
import keystrokesmod.utility.render.RenderUtils;
import keystrokesmod.utility.Theme;
import keystrokesmod.utility.Utils;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HUD extends Module {
    public static final String VERSION = "1.10.2";
    public static ModeSetting theme;
//    public static SliderSetting font;
//    public static SliderSetting fontSize;
    public static ButtonSetting dropShadow;
    public static ButtonSetting alphabeticalSort;
    private static ButtonSetting alignRight;
    private static ButtonSetting lowercase;
    public static ButtonSetting showInfo;
    public static ButtonSetting showWatermark;
    public static ModeSetting watermarkMode;
    public static int hudX = 5;
    public static int hudY = 70;
    public static String bName = "s";
    private boolean isAlphabeticalSort;
    private boolean canShowInfo;

    public HUD() {
        super("HUD", Module.category.render);
        this.registerSetting(new DescriptionSetting("Right click bind to hide modules."));
        this.registerSetting(theme = new ModeSetting("Theme", Theme.themes, 0));
//        this.registerSetting(font = new SliderSetting("Font", new String[]{"Minecraft", "Product Sans"}, 0));
//        this.registerSetting(fontSize = new SliderSetting("Font", 1.0, 0.5, 2.0, 0.25, "x"));
        this.registerSetting(new ButtonSetting("Edit position", () -> {
            final EditScreen screen = new EditScreen();
            FMLCommonHandler.instance().bus().register(screen);
            mc.displayGuiScreen(screen);
        }));
        this.registerSetting(alignRight = new ButtonSetting("Align right", false));
        this.registerSetting(alphabeticalSort = new ButtonSetting("Alphabetical sort", false));
        this.registerSetting(dropShadow = new ButtonSetting("Drop shadow", true));
        this.registerSetting(lowercase = new ButtonSetting("Lowercase", false));
        this.registerSetting(showInfo = new ButtonSetting("Show module info", true));
        this.registerSetting(showWatermark = new ButtonSetting("Show Watermark", true));
        this.registerSetting(watermarkMode = new ModeSetting("Watermark mode", new String[]{"Fork", "Augustus"}, 0, showWatermark::isToggled));
    }

    public void onEnable() {
        ModuleManager.sort();
    }

    public void guiButtonToggled(ButtonSetting b) {
        if (b == alphabeticalSort || b == showInfo) {
            ModuleManager.sort();
        }
    }

    @SubscribeEvent
    public void onRenderTick(@NotNull RenderTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || !Utils.nullCheck()) {
            return;
        }
        if (isAlphabeticalSort != alphabeticalSort.isToggled()) {
            isAlphabeticalSort = alphabeticalSort.isToggled();
            ModuleManager.sort();
        }
        if (canShowInfo != showInfo.isToggled()) {
            canShowInfo = showInfo.isToggled();
            ModuleManager.sort();
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChest && ChestStealer.noChestRender()) && !(mc.currentScreen instanceof GuiChat) || mc.gameSettings.showDebugInfo) {
            return;
        }
        int n = hudY;
        double n2 = 0.0;
        try {
            List<String> texts = getDrawTexts();

            for (String text : texts) {
                int e = Theme.getGradient((int) theme.getInput(), n2);
                if (theme.getInput() == 0) {
                    n2 -= 120;
                } else {
                    n2 -= 12;
                }
                int n3 = hudX;
                if (alignRight.isToggled()) {
                    n3 -= getFontRenderer().getStringWidth(text);
                }
                getFontRenderer().drawString(text, n3, (float) n, e, dropShadow.isToggled());
                n += Math.round(getFontRenderer().height() + 2);
            }
        }
        catch (Exception e) {
            Utils.sendMessage("&cAn error occurred rendering HUD. check your logs");
            Utils.sendDebugMessage(Arrays.toString(e.getStackTrace()));
            Utils.log.error(e);
        }
    }

    @NotNull
    private List<String> getDrawTexts() {
        List<Module> modules = ModuleManager.organizedModules;
        List<String> texts = new ArrayList<>(modules.size());

        if (showWatermark.isToggled()) {
            String text = "";
            String text2 = "";
            switch ((int) watermarkMode.getInput()) {
                case 0:
                    text = "§r§f§lRaven §bB§9" + bName +"§e";
                    text2 = "§r§7[§rxia__mc Forked§7]§r v" + VERSION;
                    break;
                case 1:
                    text = "§f§lAugustus b" + VERSION;
                    break;
            }

            if (!text.isEmpty()) {
                if (lowercase.isToggled())
                    text = text.toLowerCase();
                texts.add(text);
            }
            if (showInfo.isToggled() && !text2.isEmpty()) {
                if (lowercase.isToggled())
                    text2 = text2.toLowerCase();
                texts.add(text2);
            }
        }

        for (Module module : modules) {
            if (!module.isEnabled() || module == this) continue;
            if (module.isHidden()) continue;
            if (module == ModuleManager.commandLine) continue;

            String text = module.getPrettyName();
            if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                text += " §7" + module.getInfo();
            }
            if (lowercase.isToggled()) {
                text = text.toLowerCase();
            }
            texts.add(text);
        }
        return texts;
    }

    public static int getLongestModule(Font fr) {
        int length = 0;

        for (Module module : ModuleManager.organizedModules) {
            if (module.isEnabled()) {
                String moduleName = module.getPrettyName();
                if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                    moduleName += " §7" + module.getInfo();
                }
                if (lowercase.isToggled()) {
                    moduleName = moduleName.toLowerCase();
                }
                if (fr.getStringWidth(moduleName) > length) {
                    length = fr.getStringWidth(moduleName);
                }
            }
        }
        return length;
    }

    static class EditScreen extends GuiScreen {
        final String example = "This is an-Example-HUD";
        GuiButtonExt resetPosition;
        boolean hoverHUD = false;
        boolean hoverTargetHUD = false;
        int miX = 0;
        int miY = 0;
        int maX = 0;
        int maY = 0;
        int curHudX = 5;
        int curHudY = 70;
        int lastTargetHUDX = 70;
        int lastTargetHUDY = 30;
        int laX = 0;
        int laY = 0;
        int lmX = 0;
        int lmY = 0;
        int clickMinX = 0;

        public void initGui() {
            super.initGui();
            this.buttonList.add(this.resetPosition = new GuiButtonExt(1, this.width - 90, 5, 85, 20, "Reset position"));
            this.curHudX = HUD.hudX;
            this.curHudY = HUD.hudY;
            this.lastTargetHUDX = TargetHUD.posX;
            this.lastTargetHUDY = TargetHUD.posY;
        }

        @Override
        public void onGuiClosed() {
            FMLCommonHandler.instance().bus().unregister(this);
        }

        public void drawScreen(int mX, int mY, float pt) {
            drawRect(0, 0, this.width, this.height, -1308622848);
            int miX = this.curHudX;
            int miY = this.curHudY;
            int maX = miX + 50;
            int maY = miY + 32;
            int[] clickPos = this.d(getFontRenderer(), this.example);
            this.miX = miX;
            this.miY = miY;
            if (clickPos == null) {
                this.maX = maX;
                this.maY = maY;
                this.clickMinX = miX;
            }
            else {
                this.maX = clickPos[0];
                this.maY = clickPos[1];
                this.clickMinX = clickPos[2];
            }
            HUD.hudX = miX;
            HUD.hudY = miY;
            ScaledResolution res = new ScaledResolution(this.mc);
            int x = res.getScaledWidth() / 2 - 84;
            int y = res.getScaledHeight() / 2 - 20;
            RenderUtils.dct("Edit the HUD position by dragging.", '-', x, y, 2L, 0L, true, getFontRenderer());

            try {
                this.handleInput();
            } catch (IOException ignored) {
            }

            super.drawScreen(mX, mY, pt);
        }

        @SubscribeEvent
        public void onRenderTick(RenderTickEvent event) {
            TargetHUD.drawTargetHUD(null, mc.thePlayer.getName(), mc.thePlayer.getHealth());
        }

        private int @Nullable [] d(Font fr, String t) {
            if (empty()) {
                int x = this.miX;
                int y = this.miY;
                String[] var5 = t.split("-");

                for (String s : var5) {
                    if (HUD.alignRight.isToggled()) {
                        x += getFontRenderer().getStringWidth(var5[0]) - getFontRenderer().getStringWidth(s);
                    }
                    fr.drawString(s, (float) x, (float) y, Color.white.getRGB(), HUD.dropShadow.isToggled());
                    y += Math.round(fr.height() + 2);
                }
            }
            else {
                int longestModule = getLongestModule(getFontRenderer());
                int n = this.miY;
                double n2 = 0.0;
                for (Module module : ModuleManager.organizedModules) {
                    if (module.isEnabled() && !module.getName().equals("HUD")) {
                        if (module.isHidden()) {
                            continue;
                        }
                        if (module == ModuleManager.commandLine) {
                            continue;
                        }
                        String moduleName = module.getPrettyName();
                        if (showInfo.isToggled() && !module.getInfo().isEmpty()) {
                            moduleName += " §7" + module.getInfo();
                        }
                        if (lowercase.isToggled()) {
                            moduleName = moduleName.toLowerCase();
                        }
                        int e = Theme.getGradient((int) theme.getInput(), n2);
                        if (theme.getInput() == 0) {
                            n2 -= 120;
                        }
                        else {
                            n2 -= 12;
                        }
                        int n3 = this.miX;
                        if (alignRight.isToggled()) {
                            n3 -= getFontRenderer().getStringWidth(moduleName);
                        }
                        getFontRenderer().drawString(moduleName, n3, (float) n, e, dropShadow.isToggled());
                        n += Math.round(getFontRenderer().height() + 2);
                    }
                }
                return new int[]{this.miX + longestModule, n, this.miX - longestModule};
            }
            return null;
        }

        protected void mouseClickMove(int mX, int mY, int b, long t) {
            super.mouseClickMove(mX, mY, b, t);
            if (b == 0) {
                if (this.hoverHUD) {
                    this.curHudX = this.laX + (mX - this.lmX);
                    this.curHudY = this.laY + (mY - this.lmY);
                } else if (this.hoverTargetHUD) {
                    TargetHUD.posX = this.laX + (mX - this.lmX);
                    TargetHUD.posY = this.laY + (mY - this.lmY);
                } else if (mX > this.clickMinX && mX < this.maX && mY > this.miY && mY < this.maY) {
                    this.hoverHUD = true;
                    this.lmX = mX;
                    this.lmY = mY;
                    this.laX = this.curHudX;
                    this.laY = this.curHudY;
                } else if (mX > TargetHUD.current$minX && mX < TargetHUD.current$maxX && mY > TargetHUD.current$minY && mY < TargetHUD.current$maxY) {
                    this.hoverTargetHUD = true;
                    this.lmX = mX;
                    this.lmY = mY;
                    this.laX = TargetHUD.posX;
                    this.laY = TargetHUD.posY;
                }

            }
        }

        protected void mouseReleased(int mX, int mY, int s) {
            super.mouseReleased(mX, mY, s);
            if (s == 0) {
                this.hoverHUD = false;
                this.hoverTargetHUD = false;
            }

        }

        public void actionPerformed(GuiButton b) {
            if (b == this.resetPosition) {
                this.curHudX = HUD.hudX = 5;
                this.curHudY = HUD.hudY = 70;
                this.lastTargetHUDX = TargetHUD.posX = 70;
                this.lastTargetHUDY = TargetHUD.posY = 30;
            }

        }

        public boolean doesGuiPauseGame() {
            return false;
        }

        private boolean empty() {
            for (Module module : ModuleManager.organizedModules) {
                if (module.isEnabled() && !module.getName().equals("HUD")) {
                    if (module.isHidden()) {
                        continue;
                    }
                    if (module == ModuleManager.commandLine) {
                        continue;
                    }
                    return false;
                }
            }
            return true;
        }
    }

    private static Font getFontRenderer() {
        return FontManager.getMinecraft();
    }
}
