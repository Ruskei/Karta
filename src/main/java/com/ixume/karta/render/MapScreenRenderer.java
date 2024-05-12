package com.ixume.karta.render;

import com.ixume.karta.gui.MapElement;
import com.ixume.karta.render.background.BackgroundRenderer;
import com.ixume.karta.screen.MapScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MapScreenRenderer {
    private final MapScreen mapScreen;
    private final int width;
    private final int height;
    private final BackgroundRenderer backgroundRenderer;
    private List<MapElement> elementsToUpdate;
    private boolean[] previousActiveSections;

    public MapScreenRenderer(MapScreen mapScreen, int width, int height, BackgroundRenderer backgroundRenderer) {
        this.mapScreen = mapScreen;
        this.width = width;
        this.height = height;
        this.backgroundRenderer = backgroundRenderer;
        elementsToUpdate = new ArrayList<>();
        previousActiveSections = new boolean[width * height];
    }

    public void init() {
        elementsToUpdate.addAll(mapScreen.elements);
        for (int i = 0; i < width * height; i++) backgroundRenderer.drawBackground(i);
        mapScreen.elements.forEach(MapElement::updateActiveSections);
        mapScreen.elements.forEach(MapElement::render);
    }

    public void tick() {
        registerUpdates();
        render();
    }

    private void registerUpdates() {
        for (MapElement element : mapScreen.elements) {
            if (element.hasChanged) {
                elementsToUpdate.add(element);
                element.hasChanged = false;
            }
        }
    }

    private void render() {
        boolean[] relevantSections = new boolean[width * height];

        if (mapScreen.getInteractionManager().hasMovedMouse()) {
            mapScreen.getCursor().render().forEach(i -> relevantSections[i] = true);
        }

        //get all the current sections
        for (MapElement elementToUpdate : elementsToUpdate) {
            elementToUpdate.updateActiveSections();
            for (int i = 0; i < relevantSections.length; i++) {
                if (elementToUpdate.activeSections[i]) {
                    relevantSections[i] = true;
                }
            }
        }
        //add all the previous sections
        int z = 0;
        boolean[] mergedActiveSections = relevantSections.clone();
        for (int i = 0; i < previousActiveSections.length; i++) {
            if (previousActiveSections[i] || relevantSections[i]) {
                backgroundRenderer.drawBackground(i);
                z++;
            }

            if (previousActiveSections[i]) {
                mergedActiveSections[i] = true;
            }
        }

        System.out.println("redrew " + z + " sections");

        List<MapElement> relevantElements = getRelevantElements(elementsToUpdate, mergedActiveSections);
        relevantElements = new ArrayList<>(relevantElements);
        relevantElements.sort(Comparator.comparingInt(MapElement::getZIndex));

        for (MapElement element : relevantElements) {
            element.render();
        }

        elementsToUpdate = new ArrayList<>();

        sendUpdates(mergedActiveSections);

        previousActiveSections = relevantSections.clone();
    }

    public List<MapElement> getRelevantElements(List<MapElement> relevantElements, boolean[] relevantSections) {
        List<MapElement> newElements = new ArrayList<>(mapScreen.elements);
        List<MapElement> toRemove = new ArrayList<>();
        for (MapElement e : newElements) {
            if (relevantElements.contains(e) || !e.isVisible) {
                toRemove.add(e);
            }
        }

        newElements.removeAll(toRemove);

        for (int i = 0; i < relevantSections.length; i++) {
            if (relevantSections[i]) {
                //active section
                for (MapElement newElement : newElements) {
                    if (newElement.activeSections[i]) {
                        //intersection
                        relevantElements.add(newElement);
                    }
                }

                newElements.removeAll(relevantElements);
            }
        }

        return relevantElements;
    }

    public void sendUpdates(boolean[] sections) {
        for (int i = 0; i < sections.length; i++) {
            if (sections[i]) {
                mapScreen.getMaps().get(i).sendTrackerPacket();
            }
        }
    }

    public void addUpdate(MapElement element) {
        elementsToUpdate.add(element);
    }
}
