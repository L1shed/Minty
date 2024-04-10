package keystrokesmod.utility;

import keystrokesmod.module.impl.client.Settings;

import java.awt.*;

public enum Theme {
    Rainbow(null, null),
    Cherry(new Color(255, 200, 200), new Color(243, 58, 106)),
    Cotton_candy(new Color(99, 249, 255), new Color(255, 104, 204)),
    Flower(new Color(215, 166, 231), new Color(211, 90, 232)),
    Grayscale(new Color(240, 240, 240), new Color(110, 110, 110)),
    Royal(new Color(125, 204, 241), new Color(30, 71, 170));

    private final Color firstGradient;
    private final Color secondGradient;

    Theme(Color firstGradient, Color secondGradient) {
        this.firstGradient = firstGradient;
        this.secondGradient = secondGradient;
    }

    public static int getGradient(int index, double delay) {
        if (index > 0) {
            return convert(values()[index].firstGradient, values()[index].secondGradient, (Math.sin(System.currentTimeMillis() / 1.0E8 * Settings.timeMultiplier.getInput() * 400000.0 + delay * 0.550000011920929) + 1.0) * 0.5).getRGB();
        } else if (index == 0) {
            return Utils.getChroma(2, (long) delay);
        }
        return -1;
    }

    public static Color convert(Color color, Color color2, double n) {
        double n2 = 1.0 - n;
        return new Color((int) (color.getRed() * n + color2.getRed() * n2), (int) (color.getGreen() * n + color2.getGreen() * n2), (int) (color.getBlue() * n + color2.getBlue() * n2));
    }

    public static int[] getGradients(int index) {
        Theme[] values = values();
        if (values != null && index >= 0 && index < values.length && values[index] != null) {
            Color firstGradient = values[index].firstGradient;
            Color secondGradient = values[index].secondGradient;
            if (firstGradient != null && secondGradient != null) {
                return new int[]{firstGradient.getRGB(), secondGradient.getRGB()};
            } else {
                return new int[]{Utils.getChroma(2, (long) 0), Utils.getChroma(2, (long) 0)};
            }
        }
        return new int[]{0, 0};
    }

    public static String[] themes = new String[]{"Rainbow", "Cherry", "Cotton candy", "Flower", "Grayscale", "Royal"};
    //public static String[] themes = new String[]{"Rainbow", "Cherry", "Cotton candy", "Flare", "Flower", "Gold", "Grayscale", "Royal", "Sky", "Vine"};
}
