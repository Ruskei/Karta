package com.ixume.karta.gui;

import com.ixume.karta.colormapping.ExperimentalMapColorMatcher;
import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.dataloading.SpriteManager;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.render.TextRenderer;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;

import java.awt.*;

public class ScrollTooltip extends CursorTooltip {
    private int width;
    private int height;
    private MapColorImage scrollImage;
    private final java.util.List<String> lines;

    public ScrollTooltip(MapScreen mapScreen, int zIndex, int x, int y, int width, int height, java.util.List<String> lines) {
        super(mapScreen, zIndex, x, y);

        this.width = width;
        this.height = height;
        isVisible = false;
        this.lines = lines;

        scrollImage = generateBackgroundImage(width, height);
    }

    public ScrollTooltip(MapScreen mapScreen, int zIndex, int x, int y, java.util.List<String> lines) {
        super(mapScreen, zIndex, x, y);

        Vector2i dimensions = TextRenderer.dimensionsFromLines(lines);
        this.width = dimensions.x / 2 + 8 + 10;
        this.height = dimensions.y / 2 + 16;
        isVisible = false;
        this.lines = lines;

        scrollImage = generateBackgroundImage(width, height);
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + width * 4, y + height * 4);
        return activeSections;
    }

    @Override
    public void drawElement() {
        if (scrollImage.getWidth() != width || scrollImage.getHeight() != height) {
            scrollImage = generateBackgroundImage(width, height);
        }

        RenderUtils.drawImage(mapScreen, x, y, scrollImage, 4);
        //draw text
        TextRenderer.renderLines(mapScreen, lines, x + 32, y - 28 - 18 + height * 4, 2, (byte) 34, true);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setX(int x) {
        this.x = Math.max(0, x);
        this.x = Math.min(mapScreen.getWidth() * 128 - width * 4 - 1, x);
    }

    public void setY(int y) {
        this.y = Math.max(0, y);
        this.y = Math.min(mapScreen.getHeight() * 128 - height * 4 - 1, y);
    }

    private MapColorImage generateBackgroundImage(int width, int height) {
        byte[][] scroll = new byte[width][height];
        //draw base between ends
        for (int y = 6; y < height - 6; y++) {
            for (int x = 5; x < width - 5; x++) {
                if (x == 5 || x == width - 6) {
                    //dark edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(159, 100, 58), false);
                } else if (x == 6 || x == width - 7) {
                    //dark inner edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(227, 160, 102), false);
                } else {
                    //light inside
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(233, 177, 139), false);
                }
            }
        }

        //draw top
        for (int y = 0; y <= 5; y++) {
            for (int x = 8; x < width; x++) {
                if ((y == 0 || y == 5) && x == width - 1) continue;

                if ((y != 0 && y != 5) && x == width - 1) {
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(159, 100, 58), false);
                } else if ((y == 0 || y == 5) && x != width - 1) {
                    //top dark edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(159, 100, 58), false);
                } else if (y == 4) {
                    //dark inner edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(227, 160, 102), false);
                } else {
                    //light inside
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(255, 240, 234), false);
                }
            }
        }

        //draw bottom
        for (int y = height - 6; y <= height - 1; y++) {
            for (int x = 0; x < width - 8; x++) {
                if ((y == height - 6 || y == height - 1) && x == 0) continue;

                if (y != height - 6 && y != height -1 && x == 0) {
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(159, 100, 58), false);
                } else if (y == height - 6 || y == height - 1) {
                    //top dark edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(159, 100, 58), false);
                } else if (y == height - 2) {
                    //dark inner edge
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(227, 160, 102), false);
                } else {
                    //light inside
                    scroll[x][y] = ExperimentalMapColorMatcher.matchColor(new Color(255, 240, 234), false);
                }
            }
        }

        //draw corners
        MapColorImage corner = SpriteManager.getSprite("scroll_corner");
        for (int y = 0; y < 6; y++) {
            for (int x = 5; x < 13; x++) {
                scroll[x][y] = corner.getPixel(x - 5, y);
            }
        }

        for (int y = height - 6; y < height; y++) {
            for (int x = width - 13; x < width - 5; x++) {
                scroll[x][y] = corner.getPixel(width - x - 1 - 5, height - y - 1);
            }
        }

        return new MapColorImage(scroll, width, height);
    }
}
