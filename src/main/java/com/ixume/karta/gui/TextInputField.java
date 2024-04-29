package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Clickable;
import com.ixume.karta.gui.events.Tickable;
import com.ixume.karta.gui.events.Typable;
import com.ixume.karta.gui.listeners.ClickListener;
import com.ixume.karta.gui.listeners.TextInputFieldListener;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.render.TextRenderer;
import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TextInputField extends MapElement implements Typable, Clickable, Tickable {
    private final int width;
    private final int height;
    private final List<String> defaultText;
    private List<String> inputTextLines;
    private TextInputFieldListener textInputListener;
    private ClickListener clickListener;
    private boolean isValid;
    private boolean isSelected;
    private int underscoreBlink;

    public TextInputField(MapScreen mapScreen, int zIndex, int x, int y, int width, int height /*in lines*/, String defaultText) {
        super(mapScreen, zIndex, x, y);
        this.width = width;
        this.height = height;
        this.defaultText = TextRenderer.wrapText(defaultText, width, 1);
        inputTextLines = new ArrayList<>();
        isValid = true;
        isSelected = false;
        underscoreBlink = 15;
    }

    public void setTextInputListener(TextInputFieldListener textInputListener) {
        this.textInputListener = textInputListener;
    }

    @Override
    public void tick() {
        underscoreBlink--;
        if (underscoreBlink == 0 || underscoreBlink == 6) {
            if (inputTextLines.isEmpty() && isSelected) {
                hasChanged = true;
            }

            if (underscoreBlink == 0) {
                underscoreBlink = 15;
            }
        }
    }

    @Override
    public boolean[] updateActiveSections() {
        int boxHeight = height * (MinecraftFont.Font.getHeight() + 1) + 2;
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x+1, y+1, x + width - 1, y + boxHeight - 2);
        return activeSections;
    }

    @Override
    public void drawElement() {
        inputTextLines = inputTextLines.stream().limit(height).collect(Collectors.toList());
        int boxHeight = height * (MinecraftFont.Font.getHeight() + 1) + 2;
        RenderUtils.fillRectangle(mapScreen, x + 1, y + 1, x + width - 1, y + boxHeight - 1, (byte) 119);
        RenderUtils.drawRectangle(mapScreen, x, y, x + width, y + boxHeight, (byte) 34);
        if (inputTextLines.isEmpty()) {
            if (isSelected) {
                TextRenderer.renderText(mapScreen, underscoreBlink < 7 ? "_" : "", x + 2, y + boxHeight - 2, 1, (byte) (34), false);
            } else {
                TextRenderer.renderLines(mapScreen, defaultText, x + 2, y + boxHeight - 2, 1, (byte) (59 * 4 + 2), false);
            }
        } else {
            TextRenderer.renderLines(mapScreen, inputTextLines, x + 2, y + boxHeight - 2, 1, isValid ? (byte) (34) : (byte) (52 * 4 + 2), false);
        }
    }

    @Override
    public void onTextInput(String input) {
        if (isSelected) {
            isValid = textInputListener.onTextInput(input);
            hasChanged = true;
            if (input.equals("")) {
                inputTextLines.clear();
            } else {
                inputTextLines = TextRenderer.wrapText(input, width, 1);
            }
        }
    }

    @Override
    public void clear() {
        hasChanged = true;
        inputTextLines.clear();
    }

    @Override
    public void onMouseClick(int x, int y) {
        if (inBounds(x, y)) {
            hasChanged = true;
            isSelected = !isSelected;
            clickListener.onClick(x, y);
        }
    }

    public boolean inBounds(int x, int y) {
        return (x > this.x && x < this.x + +width && y > this.y && y < this.y + height * (MinecraftFont.Font.getHeight() + 1) + 2);
    }
}
