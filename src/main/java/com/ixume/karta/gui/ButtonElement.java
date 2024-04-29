package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Clickable;
import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.gui.listeners.ClickListener;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

public class ButtonElement extends MapElement implements Hoverable, Clickable {
    protected int width;
    protected int height;
    protected boolean isHovering;
    private final byte backgroundColor;
    private final byte unselectedOutlineColor;
    private final byte selectedOutlineColor;
    protected ClickListener listener;

    public ButtonElement(MapScreen mapScreen, int zIndex, int x, int y, int width, int height) {
        super(mapScreen, zIndex, x, y);
        this.width = width;
        this.height = height;
        backgroundColor = (byte) 117;
        unselectedOutlineColor = (byte) 119;
        selectedOutlineColor = (byte) 34;
        isHovering = false;
        listener = (int x1, int y1) -> {};
    }

    public void addListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + width, y + height);
        return activeSections;
    }

    @Override
    public void drawElement() {
        renderButton();
    }

    public void renderButton() {
        RenderUtils.drawShadedRectangle(mapScreen, x + 1, y + 1, x + width - 1, y + height - 1);
        RenderUtils.drawRectangle(mapScreen, x, y, x + width, y + height, isHovering ? selectedOutlineColor : unselectedOutlineColor);
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (inBounds(x, y)) {
            if (!isHovering) hasChanged = true;
            isHovering = true;
        } else {
            if (isHovering) hasChanged = true;
            isHovering = false;
        }
    }

    @Override
    public void onMouseClick(int x, int y) {
        System.out.println("button click");
        if (inBounds(x, y)) {
            hasChanged = true;
            listener.onClick(x, y);
        }
    }

    public boolean inBounds(int x, int y) {
        return x > this.x && x < this.x + width && y > this.y && y < this.y + height;
    }
}
