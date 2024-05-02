package keystrokesmod.module.impl.client;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.render.HUD;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.DescriptionSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Settings extends Module {
    private DescriptionSetting general;
    public static SliderSetting customCapes;
    public static ButtonSetting weaponAxe;
    public static ButtonSetting weaponRod;
    public static ButtonSetting weaponStick;
    public static ButtonSetting middleClickFriends;
    private DescriptionSetting rotations;
    public static ButtonSetting rotateBody;
    public static ButtonSetting fullBody;
    public static ButtonSetting movementFix;
    public static SliderSetting randomYawFactor;
    private DescriptionSetting themeColors;
    public static SliderSetting timeMultiplier;
    private DescriptionSetting profiles;
    public static ButtonSetting sendMessage;
    private String[] capes = new String[]{"None", "Anime", "Aqua", "Green", "Purple", "Red", "White", "Yellow"};
    public static List<ResourceLocation> loadedCapes = new ArrayList<>();

    public Settings() {
        super("Settings", category.client, 0);
        this.registerSetting(general = new DescriptionSetting("General"));
        this.registerSetting(weaponAxe = new ButtonSetting("Set axe as weapon", false));
        this.registerSetting(weaponRod = new ButtonSetting("Set rod as weapon", false));
        this.registerSetting(weaponStick = new ButtonSetting("Set stick as weapon", false));
        this.registerSetting(middleClickFriends = new ButtonSetting("Middle click friends", false));
        this.registerSetting(rotations = new DescriptionSetting("Rotations"));
        this.registerSetting(rotateBody = new ButtonSetting("Rotate body", true));
        this.registerSetting(fullBody = new ButtonSetting("Full body", false));
        //this.registerSetting(movementFix = new ButtonSetting("Movement fix", false));
        this.registerSetting(randomYawFactor = new SliderSetting("Random yaw factor", 1.0, 0.0, 10.0, 1.0));
        this.registerSetting(profiles = new DescriptionSetting("Profiles"));
        this.registerSetting(sendMessage = new ButtonSetting("Send message on enable", true));
        this.registerSetting(themeColors = new DescriptionSetting("Theme colors"));
        this.registerSetting(timeMultiplier = new SliderSetting("Time multiplier", 0.5, 0.1, 4.0, 0.1));
        this.canBeEnabled = false;
    }
}
