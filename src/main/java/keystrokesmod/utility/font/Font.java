package keystrokesmod.utility.font;

public interface Font {
    int drawString(String text, double x, double y, int color, boolean dropShadow);

    int drawString(final String text, final double x, final double y, final int color);

    int drawStringWithShadow(final String text, final double x, final double y, final int color);

    int width(String text);

    int drawCenteredString(final String text, final double x, final double y, final int color);

    int drawRightString(final String text, final double x, final double y, final int color);

    float height();

    default int getStringWidth(String text) {
        return width(text);
    }

    default int getCharWidth(char c) {
        return width(String.valueOf(c));
    }
}
