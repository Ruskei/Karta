package com.ixume.karta.render.background;

import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;

public class ImageBackground implements BackgroundRenderer{
    private final MapColorImage backgroundImage;
    private final MapScreen mapScreen;
    private final int upscale;

    public ImageBackground(MapColorImage backgroundImage, MapScreen mapScreen, int upscale) {
        this.backgroundImage = backgroundImage;
        this.mapScreen = mapScreen;
        this.upscale = upscale;
    }

    @Override
    public void drawBackground(int section) {
        Vector2i sectionPos = RenderUtils.posFromSection(mapScreen.getWidth(), section);

        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                RenderUtils.drawPixelRaw(mapScreen, section, x, y, backgroundImage.getPixel((int) Math.floor((sectionPos.x + x) / upscale), (int) Math.floor(((mapScreen.getHeight() - 1) * 128 - sectionPos.y + y) / upscale)));
            }
        }
    }
}
