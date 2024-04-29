package com.ixume.karta.colormapping;

import org.joml.Vector3i;

public class Cuboid {
    private final Vector3i min;
    private final Vector3i max;

    public Cuboid(Vector3i min, Vector3i max) {
        this.min = min;
        this.max = max;
    }

    public Vector3i getMin() {
        return min;
    }

    public Vector3i getMax() {
        return max;
    }

    public double distanceSquared(Vector3i p) {
        double dx = Math.max(Math.max(min.x - p.x, 0), p.x - max.x);
        double dy = Math.max(Math.max(min.y - p.y, 0), p.y - max.y);
        double dz = Math.max(Math.max(min.z - p.z, 0), p.z - max.z);
        return dx*dx + dy*dy + dz*dz;
    }
}
