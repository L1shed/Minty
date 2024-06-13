package keystrokesmod.utility.font.impl;

import keystrokesmod.utility.font.Font;

import static keystrokesmod.Raven.mc;

public class MinecraftFontRenderer implements Font {
    public static MinecraftFontRenderer INSTANCE = new MinecraftFontRenderer();

    @Override
    public int drawString(String text, double x, double y, int color, boolean dropShadow) {
        return mc.fontRendererObj.drawString(text, (float) x, (float) y, color, dropShadow);
    }

    @Override
    public int drawString(String text, double x, double y, int color) {
        return drawString(text, x, y, color, false);
    }

    @Override
    public int drawStringWithShadow(String text, double x, double y, int color) {
        return drawString(text, x, y, color, true);
    }

    @Override
    public int width(String text) {
        return mc.fontRendererObj.getStringWidth(text);
    }

    @Override
    public int drawCenteredString(String text, double x, double y, int color) {
        return drawString(text, x - (width(text) >> 1), y, color, false);
    }

    @Override
    public int drawRightString(String text, double x, double y, int color) {
        return drawString(text, x - (width(text)), y, color, false);
    }

    @Override
    public float height() {
        return mc.fontRendererObj.FONT_HEIGHT;
    }
}
