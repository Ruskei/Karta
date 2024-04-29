package com.ixume.karta.render.background;

import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;

public abstract class PixelBackgroundRenderer implements BackgroundRenderer {
    private final MapScreen mapScreen;

    public PixelBackgroundRenderer(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
    }

    @Override
    public void drawBackground(int section) {
        Vector2i sectionPos = RenderUtils.posFromSection(mapScreen.getWidth(), section);
        for (int y = 0; y < 128; y++) {
            for (int x = 0; x < 128; x++) {
                RenderUtils.drawPixelRaw(mapScreen, section, x, y, colorAt(sectionPos.x + x, sectionPos.y + y));
            }
        }
    }

    public abstract byte colorAt(int x, int y);
}
