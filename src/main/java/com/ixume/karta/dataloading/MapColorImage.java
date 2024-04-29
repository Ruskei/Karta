package com.ixume.karta.dataloading;

import com.ixume.karta.colormapping.ExperimentalMapColorMatcher;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MapColorImage {
    private final int width;
    private final int height;
    private final byte[][] pixels;

    public MapColorImage(BufferedImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new byte[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelRGB = image.getRGB(x, y);
                int alpha = (pixelRGB>>24) & 0xff;
                if (alpha == 0) {
                    pixels[x][y] = (byte) 0;
                    continue;
                }
                //this is only done once, so it's fine to use perceptual color matching
                pixels[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(pixelRGB), false);
            }
        }
    }

    public MapColorImage(byte[][] pixels, int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte getPixel(int x, int y) {return pixels[x][y];}
}
