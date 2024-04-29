package com.ixume.karta;

import java.util.ArrayList;
import java.util.List;

public class IDManager {
    private static int mapID = -1;
    private static final List<Integer> freeMapIDs = new ArrayList<>();

    public static int getMapID() {
        if (freeMapIDs.isEmpty()) return mapID--;
        else {
            int toReturn = freeMapIDs.get(0);
            freeMapIDs.remove(0);
            return freeMapIDs.get(toReturn);
        }
    }

    public static void returnID(int ID) {freeMapIDs.add(ID);}
}
