package keystrokesmod.module.impl.render;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeSetting;
import keystrokesmod.utility.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;

import static com.mojang.realmsclient.gui.ChatFormatting.*;

public final class CustomCape extends Module {
    private static File directory;
    public static String[] CAPES_NAME = new String[]{
            "RavenAnime", "RavenAqua", "RavenGreen", "RavenPurple", "RavenRed", "RavenWhite", "RavenYellow",
            "Cherry", "Die",
            "Astolfo", "AugustusCandy", "Esound"
    };
    public static final List<ResourceLocation> LOADED_CAPES = new ArrayList<>();
    public static final ModeSetting cape = new ModeSetting("Cape", CAPES_NAME, 0);

    public CustomCape() {
        super("CustomCape", category.render);
        this.registerSetting(new ButtonSetting("Load capes", CustomCape::loadCapes));
        this.registerSetting(cape);

        directory = new File(mc.mcDataDir + File.separator + "keystrokes", "customCapes");
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                System.out.println("There was an issue creating customCapes directory.");
            }
        }

        loadCapes();
    }

    public static void loadCapes() {
        final File[] files;
        try {
            files = Objects.requireNonNull(directory.listFiles());
        } catch (NullPointerException e) {
            Utils.sendMessage(RED + "Fail to load.");
            return;
        }

        CAPES_NAME = new String[files.length + 12];
        LOADED_CAPES.clear();
        String[] builtinCapes = new String[]{
                "RavenAnime", "RavenAqua", "RavenGreen", "RavenPurple", "RavenRed", "RavenWhite", "RavenYellow",
                "Cherry", "Die",
                "Astolfo", "AugustusCandy", "Esound"
        };
        System.arraycopy(builtinCapes, 0, CAPES_NAME, 0, builtinCapes.length);

        for (int i = 0, filesLength = files.length; i < filesLength; i++) {
            File file = files[i];
            if (!file.exists() || !file.isFile()) continue;
            if (!file.getName().endsWith(".png")) continue;
            String fileName = file.getName().substring(0, file.getName().length() - 4);

            CAPES_NAME[builtinCapes.length + i] = fileName;
        }

        for (String s : CAPES_NAME) {
            try {
                String name = s.toLowerCase();
                InputStream stream = Raven.class.getResourceAsStream("/assets/keystrokesmod/textures/capes/" + name + ".png");
                if (stream == null)
                    stream = Raven.class.getResourceAsStream("/assets/keystrokesmod/textures/capes/" + s + ".png");
                if (stream == null)
                    continue;
                BufferedImage bufferedImage = ImageIO.read(stream);
                LOADED_CAPES.add(Minecraft.getMinecraft().renderEngine.getDynamicTextureLocation(name, new DynamicTexture(bufferedImage)));
            } catch (Exception e) {
                Utils.sendMessage(RED + "Failed to load cape '" + RESET + s + RED + "'");
            }
        }

        cape.setOptions(CAPES_NAME);
        Utils.sendMessage(GREEN + "Loaded " + RESET + cape.getOptions().length + GREEN + " capes.");
    }
}
