package com.ixume.karta.render;

import com.google.common.base.Preconditions;
import com.ixume.karta.screen.MapScreen;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class TextRenderer {
    private static void renderText(MapScreen mapScreen, String text, int x, int y, int scale, byte color) {
        MinecraftFont font = MinecraftFont.Font;

        int xStart = x;
        Preconditions.checkArgument(font.isValid(text), "text (%s) contains invalid characters", text);

        for(int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y -= font.getHeight() * scale;
            } else {
                if (ch == 167) {
                    int j = text.indexOf(59, i);
                    Preconditions.checkArgument(j >= 0, "text (%s) unterminated color string", text);

                    try {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    } catch (NumberFormatException var12) {
                    }
                }

                MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));

                for(int r = 0; r < font.getHeight() * scale; r += scale) {
                    for(int c = 0; c < sprite.getWidth() * scale; c += scale) {
                        if (sprite.get(sprite.getHeight() - 1 - r / scale, c / scale)) {
                            if (scale == 1) {
                                RenderUtils.drawPixelRaw(mapScreen, x + c, y + r, color);
                            } else {
                                for (int h = 0; h < scale; ++h) {
                                    for (int o = 0; o < scale; ++o) {
                                        RenderUtils.drawPixelRaw(mapScreen, x + c + h, y + r + o, color);
                                    }
                                }
                            }
                        }
                    }
                }

                x += (sprite.getWidth() + 1) * scale;
            }
        }
    }

    public static void renderText(MapScreen mapScreen, String text, int x, int y, int scale, byte color, boolean shadowed) {
        if (shadowed) {
            renderText(mapScreen, text, x + scale, y - scale, scale, (byte) (29 * 4 + 2));
        }

        renderText(mapScreen, text, x, y, scale, color);
    }

    public static List<String> wrapText(String text, int width, int scale) {
        List<String> lines = new ArrayList<>();
        int startIndex = 0;
        int endIndex = 0;
        for(int i = 0; i < text.length(); ++i) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                endIndex = i;
                continue;
            }

            if (ch == '\n') {
                endIndex = i;
                String lineString = text.substring(startIndex, endIndex);
                lines.add(lineString);
                endIndex += 1;
                startIndex = endIndex;
                continue;
            }

            int currentWidth = MinecraftFont.Font.getWidth(text.substring(startIndex, i + 1)) * scale;
            if (currentWidth > width) {
                if (startIndex == endIndex) {//long word
                    endIndex = i;
                }

                String lineString = text.substring(startIndex, endIndex);
                lines.add(lineString);
                startIndex = ++endIndex;
            }
        }

        lines.add(text.substring(startIndex));

        return lines;
    }

    public static void renderLines(MapScreen mapScreen, List<String> lines, int x, int y, int scale, byte color, boolean isShadowed) {
        for (int i = 0; i < lines.size(); i++) {
            renderText(mapScreen, lines.get(i), x, y - i * MinecraftFont.Font.getHeight() * scale, scale, color, isShadowed);
        }
    }

    public static Vector2i dimensionsFromLines(List<String> lines) {
        return new Vector2i(lines.stream().mapToInt(MinecraftFont.Font::getWidth).reduce(0, (subtotal, current) -> Math.max(current, subtotal)), MinecraftFont.Font.getHeight() * lines.size());
    }
}
