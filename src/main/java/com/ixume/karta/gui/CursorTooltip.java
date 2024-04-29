package com.ixume.karta.gui;

import com.ixume.karta.screen.MapScreen;

public abstract class CursorTooltip extends MapElement {
    public CursorTooltip(MapScreen mapScreen, int zIndex, int x, int y) {
        super(mapScreen, zIndex, x, y);
        isVisible = false;
    }

    @Override
    abstract public boolean[] updateActiveSections();

    public void setVisible(boolean visible) {
        if (visible != isVisible) hasChanged = true;
        isVisible = visible;
    }

    @Override
    abstract public void drawElement();

    public void onMouseMove(int x, int y) {
        hasChanged = true;
        this.setX(x);
        this.setY(y);
    }
}
