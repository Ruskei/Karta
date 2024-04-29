package com.ixume.karta.gui;

import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

public class ImageElement extends MapElement {
    private final MapColorImage image;
    private final int upscale;

    public ImageElement(MapScreen mapScreen, int zIndex, int x, int y, MapColorImage image, int upscale) {
        super(mapScreen, zIndex, x, y);
        this.image = image;
        this.upscale = upscale;
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromImage(mapScreen, x, y, image, upscale);
        return activeSections;
    }

    @Override
    public void drawElement() {
        RenderUtils.drawImage(mapScreen, x, y, image, upscale);
    }
}
