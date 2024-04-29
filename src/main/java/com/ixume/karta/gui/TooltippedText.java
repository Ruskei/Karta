package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.gui.events.Tooltipped;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.TextRenderer;
import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MinecraftFont;

public class TooltippedText extends MapElement implements Hoverable, Tooltipped {
    private final String text;
    private final byte color;
    private final boolean shadowed;
    private final int scale;
    private final CursorTooltip tooltip;

    public TooltippedText(MapScreen mapScreen, int zIndex, int x, int y, CursorTooltip tooltip, String text, byte color, boolean shadowed, int scale) {
        super(mapScreen, zIndex, x, y);
        this.tooltip = tooltip;
        this.text = text;
        this.color = color;
        this.shadowed = shadowed;
        this.scale = scale;
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromText(mapScreen, text, x, y, scale);
        return activeSections;
    }

    @Override
    public void drawElement() {
        TextRenderer.renderText(mapScreen, text, x, y, scale, color, shadowed);
    }

    boolean inBounds(int x, int y) {
        return x >= this.x && x <= this.x + MinecraftFont.Font.getWidth(text) * scale && y >= this.y && y <= this.y + MinecraftFont.Font.getHeight() * scale;
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
}
