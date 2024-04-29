package com.ixume.karta.gui;

import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

public class Rectangle extends MapElement {
    private final int width;
    private final int height;
    private final byte color;

    public Rectangle(MapScreen mapScreen, int zIndex, int x, int y, int width, int height, byte color) {
        super(mapScreen, zIndex, x, y);
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + width, y + height);
        return activeSections;
    }

    @Override
    public void drawElement() {
        RenderUtils.drawRectangle(mapScreen, x, y, x + width, y + height, color);
    }
}
