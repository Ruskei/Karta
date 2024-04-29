package com.ixume.karta.gui;

import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.gui.events.Tickable;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

public class TexturedButton extends ButtonElement implements Tickable {
    MapColorImage unselected;
    MapColorImage hover;
    MapColorImage clicked;
    private final int upscale;
    private int timeSinceClick;

    public TexturedButton(MapScreen mapScreen, int zIndex, int x, int y, int width, int height, MapColorImage unselected, MapColorImage hover, MapColorImage clicked, int upscale) {
        super(mapScreen, zIndex, x, y, width, height);
        this.unselected = unselected;
        this.hover = hover;
        this.clicked = clicked;
        this.upscale = upscale;
        timeSinceClick = 0;
    }

    @Override
    public void drawElement() {
        if (timeSinceClick > 0) {
            RenderUtils.drawImage(mapScreen, x + width / 2 - clicked.getWidth() * upscale / 2, y + height / 2 - clicked.getHeight() * upscale / 2, clicked, upscale);
        } else if (isHovering) {
            RenderUtils.drawImage(mapScreen, x + width / 2 - hover.getWidth() * upscale / 2, y + height / 2 - hover.getHeight() * upscale / 2, hover, upscale);
        } else {
            RenderUtils.drawImage(mapScreen, x + width / 2 - unselected.getWidth() * upscale / 2, y + height / 2 - unselected.getHeight() * upscale / 2, unselected, upscale);
        }
    }

    @Override
    public void onMouseClick(int x, int y) {
        if (inBounds(x, y)) {
            timeSinceClick = 4;
            hasChanged = true;
            listener.onClick(x, y);
        }
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
    public void tick() {
        if (timeSinceClick == 1) hasChanged = true;
        if (timeSinceClick != 0) timeSinceClick--;
    }
}
