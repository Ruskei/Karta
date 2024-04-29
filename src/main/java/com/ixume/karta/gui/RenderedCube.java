package com.ixume.karta.gui;

import com.ixume.karta.gui.events.Clickable;
import com.ixume.karta.gui.events.Hoverable;
import com.ixume.karta.gui.events.Tickable;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.render.RenderUtils3D;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;
import org.joml.Vector3d;

public class RenderedCube extends MapElement implements Clickable, Hoverable, Tickable {
    private final Vector3d[] vertices;
    private final Vector3d origin;

    Vector2i boundsMin;
    Vector2i boundsMax;
    boolean isDragging;

    byte color;

    public RenderedCube(MapScreen mapScreen, int zIndex, Vector3d pos1, Vector3d pos2, Vector3d origin, int x, int y, byte color) {
        super(mapScreen, zIndex, x, y);

        this.origin = origin;
        vertices = new Vector3d[8];
        vertices[0] = pos1;
        vertices[1] = new Vector3d(pos2.x, pos1.y, pos1.z);
        vertices[2] = new Vector3d(pos2.x, pos2.y, pos1.z);
        vertices[3] = new Vector3d(pos1.x, pos2.y, pos1.z);
        vertices[4] = new Vector3d(pos1.x, pos1.y, pos2.z);
        vertices[5] = new Vector3d(pos2.x, pos1.y, pos2.z);
        vertices[6] = pos2;
        vertices[7] = new Vector3d(pos1.x, pos2.y, pos2.z);

        Vector2i[] points = getRenderedVertices(128);
        Vector2i boundsMin = new Vector2i(mapScreen.getWidth() * 128 - 1, mapScreen.getHeight() * 128 - 1);
        Vector2i boundsMax = new Vector2i(0, 0);
        isDragging = false;

        for (Vector2i point : points) {
            int pointX = x + point.x;
            int pointY = y + point.y;

            if (RenderUtils.inBounds(mapScreen, pointX, pointY)) {
                boundsMin = new Vector2i(Math.min(boundsMin.x, pointX), Math.min(boundsMin.y, pointY));
                boundsMax = new Vector2i(Math.max(boundsMax.x, pointX), Math.max(boundsMax.y, pointY));
            }
        }

        this.color = color;
    }

    public Vector3d[] getVertices() {
        return vertices;
    }

    public Vector2i[] getRenderedVertices(int resolution) {
        Vector2i[] points = new Vector2i[8];
        for (int i = 0; i < 8; i++) {
            points[i] = RenderUtils3D.worldToScreenPos(vertices[i], resolution);
        }

        return points;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    @Override
    public void tick() {
        rotateX(0.05d);
        rotateY(0.04d);
        rotateZ(-0.03d);
        hasChanged = true;
    }


    @Override
    public boolean[] updateActiveSections() {
        Vector2i[] points = getRenderedVertices(128);
        Vector2i min = new Vector2i(mapScreen.getWidth() * 128 - 1, mapScreen.getHeight() * 128 - 1);
        Vector2i max = new Vector2i(0, 0);

        for (Vector2i point : points) {
            int pointX = x + point.x;
            int pointY = y + point.y;

            if (RenderUtils.inBounds(mapScreen, pointX, pointY)) {
                min = new Vector2i(Math.min(min.x, pointX), Math.min(min.y, pointY));
                max = new Vector2i(Math.max(max.x, pointX), Math.max(max.y, pointY));
            }
        }

        boundsMin = min;
        boundsMax = max;
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, min.x, min.y, max.x, max.y);
        return activeSections;
    }

    @Override
    public void drawElement() {
        Vector2i[] points = getRenderedVertices(128);

        RenderUtils.drawLineRaw(mapScreen, x + points[0].x, y + points[0].y, x + points[1].x, y + points[1].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[1].x, y + points[1].y, x + points[2].x, y + points[2].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[2].x, y + points[2].y, x + points[3].x, y + points[3].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[3].x, y + points[3].y, x + points[0].x, y + points[0].y, color);

        RenderUtils.drawLineRaw(mapScreen, x + points[4].x, y + points[4].y, x + points[5].x, y + points[5].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[5].x, y + points[5].y, x + points[6].x, y + points[6].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[6].x, y + points[6].y, x + points[7].x, y + points[7].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[7].x, y + points[7].y, x + points[4].x, y + points[4].y, color);

        RenderUtils.drawLineRaw(mapScreen, x + points[0].x, y + points[0].y, x + points[4].x, y + points[4].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[1].x, y + points[1].y, x + points[5].x, y + points[5].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[2].x, y + points[2].y, x + points[6].x, y + points[6].y, color);
        RenderUtils.drawLineRaw(mapScreen, x + points[3].x, y + points[3].y, x + points[7].x, y + points[7].y, color);
    }

    public Vector3d[] rotateZ(double theta) {
        for (int i = 0; i < 8; i++) {
            Vector3d vertex = vertices[i];
            double relativeX = vertex.x - origin.x;
            double relativeY = vertex.y - origin.y;
            vertices[i] = new Vector3d(origin.x + relativeX * Math.cos(theta) - relativeY * Math.sin(theta), origin.y + relativeX * Math.sin(theta) + relativeY * Math.cos(theta), vertex.z);
        }

        return vertices;
    }

    public Vector3d[] rotateX(double theta) {
        for (int i = 0; i < 8; i++) {
            Vector3d vertex = vertices[i];
            double relativeY = vertex.y - origin.y;
            double relativeZ = vertex.z - origin.z;
            vertices[i] = new Vector3d(vertex.x, origin.y + relativeZ * Math.sin(theta) + relativeY * Math.cos(theta), origin.z + relativeZ * Math.cos(theta) - relativeY * Math.sin(theta));
        }

        return vertices;
    }

    public Vector3d[] rotateY(double theta) {
        for (int i = 0; i < 8; i++) {
            Vector3d vertex = vertices[i];
            double relativeX = vertex.x - origin.x;
            double relativeZ = vertex.z - origin.z;
            vertices[i] = new Vector3d(origin.x + relativeX * Math.cos(theta) - relativeZ * Math.sin(theta), vertex.y, origin.z + relativeX * Math.sin(theta) + relativeZ * Math.cos(theta));
        }

        return vertices;
    }

    @Override
    public void onMouseClick(int x, int y) {
        if (inBounds(x, y)) {
            isDragging = !isDragging;
        }
    }

    public boolean inBounds(int x, int y) {
        return x > boundsMin.x && x < boundsMax.x && y > boundsMin.y && y < boundsMax.y;
    }

    @Override
    public void onMouseMove(int x, int y) {
        if (isDragging) {
            this.x = x;
            this.y = y;
        }
    }
}
