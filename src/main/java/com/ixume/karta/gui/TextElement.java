package com.ixume.karta.gui;

import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.TextRenderer;
import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

public class TextElement extends MapElement {
    private final MapFont font;
    private String text;
    private byte color;
    private final boolean shadowed;
    private final int scale;

    public TextElement(MapScreen mapScreen, int zIndex, int x, int y, String text, byte color, boolean shadowed, int scale) {
        super(mapScreen, zIndex, x, y);
        this.font = MinecraftFont.Font;
        this.text = text;
        this.color = color;
        this.shadowed = shadowed;
        this.scale = scale;
    }

    public MapFont getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public byte getTextColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    public boolean isShadowed() {
        return shadowed;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public int getZIndex() {
        return zIndex;
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
}
