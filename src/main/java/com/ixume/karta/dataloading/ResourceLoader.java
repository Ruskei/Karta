package com.ixume.karta.dataloading;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixume.karta.Main;
import com.ixume.karta.colormapping.ExperimentalMapColorMatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ResourceLoader {
    public static void init() {
        File dataFolder = Main.getInstance().getDataFolder();
        dataFolder.mkdirs();

        loadColorMappings();
        loadResources();
    }

    private static void loadResources() {
        File dataFolder = Main.getInstance().getDataFolder();
        for (File image : dataFolder.listFiles((dir, name) -> name.endsWith(".png"))) {
            try {
                BufferedImage bufferedImage = ImageIO.read(image);
                SpriteManager.addSprite(image.getName(), new MapColorImage(bufferedImage));
            } catch (final IOException ignored) {}
        }
    }

    private static void loadColorMappings() {
        JsonObject obj;
        try (
                InputStreamReader reader = new InputStreamReader(Main.getInstance().getResource("color_mappings.json"))) {
            obj = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (
                IOException ex) {
            throw new RuntimeException(ex);
        }

        final JsonObject version = obj.get("1.20").getAsJsonObject();
        for (
                String key : version.keySet()) {
            final JsonObject colorObj = version.get(key).getAsJsonObject();
            final JsonArray colorArr = colorObj.get("colors").getAsJsonArray();
            int id = Integer.parseInt(key);
            for (int variant = 0; variant < colorArr.size(); variant++) {
                int rgb = colorArr.get(variant).getAsInt();
                final Color color = new Color(rgb, false);
                ExperimentalMapColorMatcher.colorIDMap.put(color, id * 4 + variant);
            }
        }

        ExperimentalMapColorMatcher.init();
    }
}
