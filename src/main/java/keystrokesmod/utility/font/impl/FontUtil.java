package keystrokesmod.utility.font.impl;

import keystrokesmod.Raven;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;

public class FontUtil {

    private static final IResourceManager RESOURCE_MANAGER = Raven.mc.getResourceManager();

    /**
     * Method, which gets a font by a resource name
     *
     * @param resource resource name
     * @param size     font size
     * @return font by resource
     */
    public static @Nullable Font getResource(final String resource, final int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, RESOURCE_MANAGER.getResource(new ResourceLocation(resource)).getInputStream()).deriveFont((float) size);
        } catch (final FontFormatException | IOException ignored) {
            return null;
        }
    }
}
