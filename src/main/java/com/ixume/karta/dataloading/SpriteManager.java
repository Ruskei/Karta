package com.ixume.karta.dataloading;

import java.util.HashMap;

public class SpriteManager {
    private static final HashMap<String, MapColorImage> sprites = new HashMap<>();

    public static void addSprite(String ID, MapColorImage image) {
        sprites.put(ID, image);
    }

    public static MapColorImage getSprite(String ID) {return sprites.get(ID);}
}
