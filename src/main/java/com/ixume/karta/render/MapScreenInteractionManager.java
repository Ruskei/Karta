package com.ixume.karta.render;

import com.ixume.karta.gui.MapElement;
import com.ixume.karta.gui.events.Clickable;
import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.gui.events.Tickable;
import com.ixume.karta.gui.events.Typable;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;

import java.util.List;

public class MapScreenInteractionManager {
    private final MapScreen screen;
    private boolean hasLeftClicked;
    private boolean hasMovedMouse;
    private Vector2i mouseLocation;
    private String textInput;
    private boolean hasTextInputUpdated;

    public MapScreenInteractionManager(MapScreen screen) {
        this.screen = screen;
        hasLeftClicked = false;
        hasMovedMouse = false;
        mouseLocation = new Vector2i(screen.getWidth() * 64, screen.getHeight() * 64);
        textInput = "";
        hasTextInputUpdated = false;
    }

    public void tick(List<MapElement> elements) {
        for (int i = elements.size() - 1; i >= 0; i--) {
            MapElement element = elements.get(i);

            if (element instanceof Tickable tickableElement) {
                tickableElement.tick();
            }

            if (hasLeftClicked && element instanceof Clickable clickableElement) {
                clickableElement.onMouseClick(mouseLocation.x, mouseLocation.y);
            }

            if (hasMovedMouse && element instanceof Hoverable hoverableElement) {
                hoverableElement.onMouseMove(mouseLocation.x, mouseLocation.y);
            }

            if (hasTextInputUpdated && element instanceof Typable typableElement) {
                typableElement.onTextInput(textInput);
            }
        }
    }

    public void resetInputs() {
        hasMovedMouse = false;
        hasLeftClicked = false;
        hasTextInputUpdated = false;
    }

    public void leftClick() {
        hasLeftClicked = true;
    }

    public void mouseMoved(int x, int y) {
        hasMovedMouse = true;
        mouseLocation = new Vector2i(x, y);
    }

    public boolean hasMovedMouse() {
        return hasMovedMouse;
    }

    public void onTextInput(String input) {
        textInput = input;
        hasTextInputUpdated = true;
    }
}
