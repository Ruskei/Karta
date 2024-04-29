package com.ixume.karta.colormapping;

import org.joml.Vector3i;

import java.awt.Color;
import java.util.*;

public class ExperimentalMapColorMatcher {
    public static HashMap<Color, Integer> colorIDMap = new HashMap<>();
    private static final byte[][][] cachedColors = new byte[256][256][256];
    private static ColorTree colorTree;

    public static void init() {
        colorTree = new ColorTree(colorIDMap.entrySet().stream().map((e -> new ColorPoint(new Vector3i(e.getKey().getRed(), e.getKey().getGreen(), e.getKey().getBlue()), e.getValue()))).toList());
    }

    public static byte matchColor(Color point, boolean perceptual) {
        byte value = cachedColors[point.getRed()][point.getGreen()][point.getBlue()];
        if (value != 0) {
            return value;
        } else {
            PointDistancePair closest = colorTree.getClosest(colorTree.rootNode, new Vector3i(point.getRed(), point.getGreen(), point.getBlue()), perceptual);
//            if (perceptual) {
            cachedColors[point.getRed()][point.getGreen()][point.getBlue()] = (byte) closest.vector.getId();
//            }

//            System.out.println("R: " + color.getRed() + " G: " + color.getGreen() + " B: " + color.getBlue() + " ID: " + closest.vector.getId());
            return (byte) closest.vector.getId();
        }
    }
}
