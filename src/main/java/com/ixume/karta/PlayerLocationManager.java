package com.ixume.karta;

import org.bukkit.entity.Player;
import org.joml.Vector3d;

public interface PlayerLocationManager {
    default Vector3d intakePlayer(Player player) {
        return new Vector3d((int) Math.floor(player.getLocation().getX()),
                (int) Math.floor(player.getLocation().getY()) + 2048,
                (int) Math.floor(player.getLocation().getZ()));
    }
}
