package com.ixume.karta.gui;

import com.ixume.karta.screen.MapScreen;

public abstract class MapElement {
    protected MapScreen mapScreen;
    public int zIndex;
    public boolean hasChanged;
    public boolean[] activeSections;
    protected int x;
    protected int y;
    public boolean isVisible;

    public MapElement(MapScreen mapScreen, int zIndex, int x, int y, boolean isVisible) {
        this.mapScreen = mapScreen;
        this.zIndex = zIndex;
        hasChanged = false;
        activeSections = new boolean[mapScreen.getHeight() * mapScreen.getWidth()];
        this.x = x;
        this.y = y;
        this.isVisible = isVisible;
    }

    public MapElement(MapScreen mapScreen, int zIndex, int x, int y) {
        this.mapScreen = mapScreen;
        this.zIndex = zIndex;
        activeSections = new boolean[mapScreen.getHeight() * mapScreen.getWidth()];
        this.x = x;
        this.y = y;
        this.isVisible = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void render() {
        if (isVisible) drawElement();
    }

    public abstract boolean[] updateActiveSections();

    public abstract void drawElement();
}
