package com.ixume.karta.gui;

import com.ixume.karta.colormapping.ExperimentalMapColorMatcher;
import com.ixume.karta.gui.events.Tickable;
import com.ixume.karta.render.ActiveSectionUtils;
import com.ixume.karta.render.RenderUtils;
import com.ixume.karta.screen.MapScreen;

import java.awt.*;

public class HSLDisplay extends MapElement implements Tickable {
    private int lightness;

    public HSLDisplay(MapScreen mapScreen, int zIndex, int x, int y) {
        super(mapScreen, zIndex, x, y);
    }

    @Override
    public boolean[] updateActiveSections() {
        activeSections = ActiveSectionUtils.sectionsFromRectangle(mapScreen, x, y, x + 360, y + 100);
        return activeSections;
    }

    @Override
    public void tick() {
        hasChanged = true;

        lightness++;
        if (lightness == 100) lightness = 0;
    }

    @Override
    public void drawElement() {
        for (int saturation = 0; saturation <= 100; saturation++) {
            for (int hue = 0; hue <= 360; hue++) {
                RenderUtils.drawPixelRaw(mapScreen, x + hue, y + saturation, ExperimentalMapColorMatcher.matchColor(toRGB(hue, saturation, lightness, 1), true));
            }
        }
    }

    public static Color toRGB(float h, float s, float l, float alpha)
    {
        if (s <0.0f || s > 100.0f)
        {
            String message = "Color parameter outside of expected range - Saturation";
            throw new IllegalArgumentException( message );
        }

        if (l <0.0f || l > 100.0f)
        {
            String message = "Color parameter outside of expected range - Luminance";
            throw new IllegalArgumentException( message );
        }

        if (alpha <0.0f || alpha > 1.0f)
        {
            String message = "Color parameter outside of expected range - Alpha";
            throw new IllegalArgumentException( message );
        }

        //  Formula needs all values between 0 - 1.

        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        return new Color(r, g, b, alpha);
    }

    private static float HueToRGB(float p, float q, float h)
    {
        if (h < 0) h += 1;

        if (h > 1 ) h -= 1;

        if (6 * h < 1)
        {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1 )
        {
            return  q;
        }

        if (3 * h < 2)
        {
            return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
        }

        return p;
    }
}
