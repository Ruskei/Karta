package com.ixume.karta.screen;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScreensManager {
    private static final List<MapScreen> screens = new ArrayList<>();

    public static void tick() {
        for (MapScreen screen : screens) {
            screen.tick();
        }
    }

    public static void openScreen(MapScreen screen) {
        Optional<MapScreen> optionalScreen = getScreen(screen.getPlayer());
        optionalScreen.ifPresent((k) -> {
            screens.remove(k);
            k.closeScreen();
        });

        screen.init();
        screens.add(screen);
        screen.sendFull();
    }

    public static boolean onPlayerMove(ServerboundMovePlayerPacket packet, Player player) {
        if (packet.xRot == 0f && packet.yRot == 0f) {
            return false;
        }

        Optional<MapScreen> optionalScreen = getScreen(player);

        optionalScreen.ifPresent(k -> k.onPlayerMove(packet.getYRot(0), packet.getXRot(0)));
        return optionalScreen.isPresent();
    }

    public static void onPlayerSwing(ServerboundSwingPacket packet, Player player) {
        Optional<MapScreen> optionalScreen = getScreen(player);
        optionalScreen.ifPresent(MapScreen::onPlayerSwing);
    }

    public static boolean onPlayerScroll(ServerboundSetCarriedItemPacket packet, Player player) {
        Optional<MapScreen> optionalScreen = getScreen(player);

        optionalScreen.ifPresent(k -> {
            if (packet.getSlot() != 4) {
                k.onScroll(packet.getSlot() < 4);
            }
        });

        return optionalScreen.isPresent();
    }

    public static void onPlayerTextInput(String input, Player player) {
        Optional<MapScreen> optionalScreen = getScreen(player);

        if (optionalScreen.isPresent()) {
            input = input.replaceFirst("/", "");
            optionalScreen.get().onPlayerTextInput(input);
        }
    }

    public static void onLeave(Player player) {
        getScreen(player).ifPresent((k) -> {
            k.onPlayerDisconnect();
            screens.remove(k);
        });
    }

    public static Optional<MapScreen> getScreen(Player player) {
        return screens.stream().filter(k -> k.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst();
    }
}
