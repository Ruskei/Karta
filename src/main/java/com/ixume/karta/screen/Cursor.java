package com.ixume.karta.screen;

import com.ixume.karta.render.RenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import org.joml.Vector2f;

import java.util.HashSet;
import java.util.List;

public class Cursor {
    private final MapScreen mapScreen;
    private final float sensitivity;
    private final float threshold;
    private int oldX;
    private int oldY;
    private int x;
    private int y;
    private List<Integer> activeSections;

    public Cursor(MapScreen mapScreen) {
        this.mapScreen = mapScreen;
        this.sensitivity = 7f;
        this.threshold = 30f;
        this.oldX = mapScreen.getWidth() * 128;
        this.oldY = mapScreen.getHeight() * 128;
        this.x = mapScreen.getWidth() * 128;
        this.y = mapScreen.getHeight() * 128;
    }

    public List<Integer> getActiveSections() {
        return activeSections;
    }

    public int getX() {
        return (int) Math.floor(x / 2);
    }

    public int getY() {
        return (int) Math.floor(y / 2);
    }

    public void cursorInput(float xRot, float yRot) {
        //clamp to borders & max movement
        Vector2f delta = new Vector2f(xRot, yRot);
        double distance = Math.sqrt(xRot*xRot + yRot*yRot);
        if (distance > threshold) {
            delta.mul((float) (threshold / distance));
        }

        delta.mul(sensitivity);
        x = (int) (x + delta.x);
        x = Math.min(x, mapScreen.getWidth() * 256 - 2);
        x = Math.max(x, 0);
        y = (int) (y - delta.y);
        y = Math.min(y, mapScreen.getHeight() * 256 - 2);
        y = Math.max(y, 0);
    }

    public HashSet<Integer> render() {
        HashSet<Integer> activeSections = new HashSet<>();
        int visualOldX = oldX + 1;
        int visualOldY = oldY + 1;

        int visualX = x + 1;
        int visualY = y + 1;
        //find which section to draw the cursor on
        int oldSectionIndex = RenderUtils.sectionFromPos(mapScreen.getWidth(), (int) Math.floor(visualOldX / 2), (int) Math.floor(visualOldY / 2));
        IndividualMap oldMap = mapScreen.getMaps().get(oldSectionIndex);
        oldMap.icons.clear();
        activeSections.add(oldSectionIndex);
        int sectionIndex = RenderUtils.sectionFromPos(mapScreen.getWidth(), (int) Math.floor(visualX / 2), (int) Math.floor(visualY / 2));
        IndividualMap activeMap = mapScreen.getMaps().get(sectionIndex);
        activeSections.add(sectionIndex);

        MapDecoration cursor = new MapDecoration(MapDecoration.Type.TARGET_POINT, (byte) (visualX % 256 - 128), (byte) -(visualY % 256 - 128), (byte) -2, Component.empty());
        activeMap.icons.clear();
        activeMap.icons.add(cursor);

        oldX = x;
        oldY = y;

        return activeSections;
    }
}
