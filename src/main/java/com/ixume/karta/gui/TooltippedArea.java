package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.gui.events.Tooltipped;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.screen.MapScreen;

public class TooltippedArea extends MapElement implements Hoverable, Tooltipped {
    private final int width;
    private final int height;
    private final CursorTooltip tooltip;

    public TooltippedArea(MapScreen mapScreen, int zIndex, int x, int y, int width, int height, CursorTooltip tooltip) {
        super(mapScreen, zIndex, x, y);
        this.width = width;
        this.height = height;
        this.tooltip = tooltip;

        mapScreen.registerElement(tooltip);
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + width, y + height);
        return activeSections;
    }

    @Override
    public void drawElement() {

    }

    @Override
    public void onMouseMove(int x, int y) {
        boolean isInBounds = inBounds(x, y);
        tooltip.setVisible(isInBounds);
        if (isInBounds) {
            tooltip.onMouseMove(x, y);
        }
    }

    @Override
    public MapElement getTooltip() {
        return tooltip;
    }

    private boolean inBounds(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }
}
