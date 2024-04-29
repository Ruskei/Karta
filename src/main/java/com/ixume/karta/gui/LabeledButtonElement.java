package com.ixume.karta.gui;

import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MinecraftFont;

public class LabeledButtonElement extends ButtonElement {
    private final TextElement textElement;

    public LabeledButtonElement(MapScreen mapScreen, int zIndex, int x, int y, int width, int height, String text, int scale) {
        super(mapScreen, zIndex, x, y, width, height);

        int textWidth = MinecraftFont.Font.getWidth(text) * scale;
        int textHeight = MinecraftFont.Font.getHeight() * scale;
        textElement = new TextElement(mapScreen, zIndex + 1, x + width / 2 - textWidth / 2, y + height / 2 + textHeight / 2, text, (byte) 34, true, scale);
    }

    @Override
    public void drawElement() {
        renderButton();
        textElement.render();
    }
}
