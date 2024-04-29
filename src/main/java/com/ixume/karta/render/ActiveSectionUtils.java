package com.ixume.karta.render;

import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MinecraftFont;

public class ActiveSectionUtils {
    public static boolean[] sectionsFromRectangle(MapScreen mapScreen, int x1, int y1, int x2, int y2) {
        boolean[] activeSections = RenderUtils.emptyActiveSections(mapScreen);

        for (int i = (int) Math.floor(y1 / 128f); i <= (int) Math.floor(y2 / 128f); i++) {
            for (int j = (int) Math.floor(x1 / 128f); j <= (int) Math.floor(x2 / 128f); j++) {
                activeSections[j + i * mapScreen.getWidth()] = true;
            }
        }

        return activeSections;
    }

    public static boolean[] sectionsFromText(MapScreen mapScreen, String text, int x, int y, int scale) {
        return sectionsFromRectangle(mapScreen, x, y, (x + MinecraftFont.Font.getWidth(text) * scale), (y + MinecraftFont.Font.getHeight() * scale));
    }

    public static boolean[] sectionsFromImage(MapScreen mapScreen, int x, int y, MapColorImage image, int upscale) {
        return sectionsFromRectangle(mapScreen, x, y, (x + image.getWidth() * upscale), (y + image.getHeight() * upscale));
    }
}
