package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Clickable;
import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElementContainer extends MapElement implements Hoverable, Clickable {
    protected List<MapElement> elements;

    public ElementContainer(MapScreen mapScreen, int zIndex, int x, int y) {
        super(mapScreen, zIndex, x, y);
        this.elements = new ArrayList<>();
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = RenderUtils.emptyActiveSections(mapScreen);
        elements.sort(Comparator.comparingInt(MapElement::getZIndex));
        elements.forEach(e -> {
            e.updateActiveSections();
            for (int i = 0; i < activeSections.length; i++) {
                if (e.activeSections[i]) activeSections[i] = true;
            }
        });

        return activeSections;
    }

    public void addElement(MapElement element) {elements.add(element);}

    @Override
    public void drawElement() {
        elements.sort(Comparator.comparingInt(MapElement::getZIndex));
        elements.forEach(MapElement::render);
    }

    public void setVisible(boolean visible) {
        elements.forEach(e -> e.isVisible = visible);
        isVisible = visible;
    }

    public void setPosition(int newX, int newY) {
        int deltaX = newX - x;
        int deltaY = newY - y;
        elements.forEach(e -> {
            e.x = e.x + deltaX;
            e.y = e.y + deltaY;
        });

        x = newX;
        y = newY;
    }

    @Override
    public void onMouseClick(int x, int y) {
//        setPosition(x, y);
//        mapScreen.getRenderer().addUpdate(this);
    }

    @Override
    public void onMouseMove(int x, int y) {

    }
}
