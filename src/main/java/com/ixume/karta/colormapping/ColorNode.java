package com.ixume.karta.colormapping;

public class ColorNode {
    private final ColorPoint point; //point to represent the RGB values, 0-255
    private final Cuboid bounds;
    private final int plane;
    private final int axis; //0 = comparing X values, 1 = comparing Y values, 2 = comparing Z values
    private final ColorNode left;
    private final ColorNode right;

    public ColorNode(ColorPoint point, Cuboid bounds, int plane, int axis, ColorNode left, ColorNode right) {
        this.point = point;
        this.bounds = bounds;
        this.plane = plane;
        this.axis = axis;
        this.left = left;
        this.right = right;
    }

    public ColorPoint getPoint() {
        return point;
    }

    public Cuboid getBounds() {
        return bounds;
    }

    public int getPlane() {
        return plane;
    }

    public int getAxis() {
        return axis;
    }

    public ColorNode getLeft() {
        return left;
    }

    public ColorNode getRight() {
        return right;
    }
}
