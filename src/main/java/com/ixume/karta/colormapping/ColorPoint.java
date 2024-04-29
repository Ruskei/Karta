package com.ixume.karta.colormapping;

import org.joml.Vector3i;

public class ColorPoint {
    private final Vector3i vector;
    private final int id;

    public ColorPoint(Vector3i vector, int id) {
        this.vector = vector;
        this.id = id;
    }

    public Vector3i getVector() {
        return vector;
    }

    public int getId() {
        return id;
    }
}
