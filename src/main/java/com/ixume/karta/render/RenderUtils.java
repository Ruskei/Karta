package com.ixume.karta.render;

import com.ixume.karta.dataloading.MapColorImage;
import com.ixume.karta.screen.IndividualMap;
import com.ixume.karta.screen.MapScreen;
import org.joml.Vector2i;

public class RenderUtils {
    public static boolean inBounds(MapScreen screen, int x, int y) {
        return !(x < 0 || x >= screen.getWidth() * 128 || y < 0 || y >= screen.getHeight() * 128);
    }

    public static void drawPixelRaw(MapScreen mapScreen, int x, int y, byte color) {
        if (color == 0) return;
        int sectionIndex = sectionFromPos(mapScreen.getWidth(), x, y);
        if (inBounds(mapScreen, x, y)) {
            IndividualMap activeMap = mapScreen.getMaps().get(sectionIndex);
            activeMap.mapData[(int) ((x % 128) + ((127f - (y % 128f)) * 128f))] = color;
        }
    }

    public static void drawPixelRaw(MapScreen mapScreen, int section, int x, int y, byte color) {
        mapScreen.getMaps().get(section).mapData[x + y * 128] = color;
    }

    public static void drawLineRaw(MapScreen mapScreen, int x1, int y1, int x2, int y2, byte color) {
        int deltax = Math.abs(x2 - x1);
        int deltay = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        int error = deltax - deltay;
        int e2;

        while (true) {
            RenderUtils.drawPixelRaw(mapScreen, x1, y1, color);

            if (x1 == x2 && y1 == y2)
                break;

            e2 = 2 * error;
            if (e2 > -deltay) {
                error = error - deltay;
                x1 = x1 + sx;
            }

            if (e2 < deltax) {
                error = error + deltax;
                y1 = y1 + sy;
            }
        }
    }

    public static boolean[] drawRectangle(MapScreen mapScreen, int x1, int y1, int x2, int y2, byte color) {
        drawLineRaw(mapScreen, x1, y1, x2, y1, color);
        drawLineRaw(mapScreen, x2, y1, x2, y2, color);
        drawLineRaw(mapScreen, x2, y2, x1, y2, color);
        drawLineRaw(mapScreen, x1, y2, x1, y1, color);

        boolean[] activeSections = emptyActiveSections(mapScreen);

        for (int i = (int) Math.floor(y1 / 128f); i <= (int) Math.floor(y2 / 128f); i++) {
            for (int j = (int) Math.floor(x1 / 128f); j <= (int) Math.floor(x2 / 128f); j++) {
                activeSections[j + i * mapScreen.getWidth()] = true;
            }
        }

        return activeSections;
    }

    public static boolean[] drawShadedRectangle(MapScreen mapScreen, int x1, int y1, int x2, int y2) {
        drawPixelRaw(mapScreen, x1, y2, (byte) (8 * 4 + 2));
        drawLineRaw(mapScreen, x1 + 1, y2, x2 - 1, y2, (byte) (8 * 4 + 1));
        drawLineRaw(mapScreen, x1, y2 - 1, x1, y1 + 1, (byte) (8 * 4 + 1));
        fillRectangle(mapScreen, x1 + 1, y1 + 1, x2 - 1, y2 - 1, (byte) (3 * 4));
        drawPixelRaw(mapScreen, x1, y1, (byte) (22 * 4 + 1));
        drawPixelRaw(mapScreen, x2, y2, (byte) (22 * 4 + 1));
        drawLineRaw(mapScreen, x1 + 1, y1, x2 - 1, y1, (byte) (22 * 4));
        drawLineRaw(mapScreen, x2, y2 - 1, x2, y1 + 1, (byte) (22 * 4));
        drawPixelRaw(mapScreen, x2, y1, (byte) (21 * 4 + 2));

        boolean[] activeSections = emptyActiveSections(mapScreen);

        for (int i = (int) Math.floor(y1 / 128f); i <= (int) Math.floor(y2 / 128f); i++) {
            for (int j = (int) Math.floor(x1 / 128f); j <= (int) Math.floor(x2 / 128f); j++) {
                activeSections[j + i * mapScreen.getWidth()] = true;
            }
        }

        return activeSections;
    }

    public static boolean[] fillRectangle(MapScreen mapScreen, int x1, int y1, int x2, int y2, byte color) {
        //loop through every section that needs to be drawn in
        boolean[] activeSections = emptyActiveSections(mapScreen);
        if (color == 0) return activeSections;
        for (int sectionY = (int) Math.floor(y1 / 128f); sectionY <= (int) Math.floor(y2 / 128f); sectionY++) {
            for (int sectionX = (int) Math.floor(x1 / 128f); sectionX <= (int) Math.floor(x2 / 128f); sectionX++) {
                if (sectionY < 0 || sectionY >= mapScreen.getWidth() || sectionX < 0 || sectionX >= mapScreen.getWidth()) continue;

                int currentIndex = sectionX + sectionY * mapScreen.getWidth();
//                System.out.println("sectionY : " + sectionY + " sectionX : " + sectionX + " currentIndex : " + currentIndex);
                activeSections[currentIndex] = true;
                IndividualMap activeMap = mapScreen.getMaps().get(currentIndex);
                int sectionX1 = (int) Math.floor(Math.max(0, x1 - (sectionX * 128f)));
                int sectionY1 = (int) Math.floor(Math.max(0, y1 - (sectionY * 128f)));
                int sectionX2 = (int) Math.floor(Math.max(0, x2 - (sectionX * 128f)));
                int sectionY2 = (int) Math.floor(Math.max(0, y2 - (sectionY * 128f)));

                sectionX1 = (int) Math.floor(Math.min(127, sectionX1));
                sectionY1 = (int) Math.floor(Math.min(127, sectionY1));
                sectionX2 = (int) Math.floor(Math.min(127, sectionX2));
                sectionY2 = (int) Math.floor(Math.min(127, sectionY2));
//                System.out.println( "x1 : " + sectionX1 + " y1 : " + sectionY1 + " x2 : " + sectionX2 + " y2 : " + sectionY2);

                for (int y = sectionY1; y <= sectionY2; y++) {
                    for (int x = sectionX1; x <= sectionX2; x++) {
                        activeMap.mapData[x + (127 - y) * 128] = color;
                    }
                }
            }
        }

        return activeSections;
    }

    public static boolean[] drawImage(MapScreen mapScreen, int x, int y, int xMax, int yMax, MapColorImage image, int upscale) {
        for (int yPixel = yMax; yPixel >= 0; yPixel--) {
            for (int xPixel = 0; xPixel < xMax; xPixel++) {
                if (upscale == 1) {
                    drawPixelRaw(mapScreen, x + xPixel, y - yPixel + image.getHeight() - 1, image.getPixel(xPixel, yPixel));
                } else {
                    fillRectangle(mapScreen, x + xPixel * upscale, y - yPixel * upscale + (image.getHeight() -1) * upscale, x + xPixel * upscale + upscale - 1, y - yPixel * upscale + upscale - 1 + (image.getHeight() -1) * upscale, image.getPixel(xPixel, yPixel));
                }
            }
        }

        boolean[] activeSections = emptyActiveSections(mapScreen);

        for (int i = (int) Math.floor(y / 128f); i <= (int) Math.floor((y + image.getHeight() * upscale) / 128f); i++) {
            for (int j = (int) Math.floor(x / 128f); j <= (int) Math.floor((x + image.getWidth() * upscale) / 128f); j++) {
                activeSections[j + i * mapScreen.getWidth()] = true;
            }
        }

        return activeSections;
    }

    public static boolean[] drawImage(MapScreen mapScreen, int x, int y, MapColorImage image, int upscale) {
        return drawImage(mapScreen, x, y, image.getWidth(), image.getHeight() - 1, image, upscale);
    }

    public static int sectionFromPos(int width, int x, int y) {
        return (int) (Math.floor(x / 128f) + Math.floor(y / 128f) * width);
    }

    public static Vector2i posFromSection(int width, int section) {
        return new Vector2i(section % width * 128, (int) (Math.floor(section / width) * 128));
    }

    public static boolean[] emptyActiveSections(MapScreen mapScreen) {
        boolean[] toReturn = new boolean[mapScreen.getWidth() * mapScreen.getHeight()];
        for (int i = 0; i < mapScreen.getWidth() * mapScreen.getHeight(); i++) {
            toReturn[i] = false;
        }

        return toReturn;
    }
}
