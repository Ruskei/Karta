package com.ixume.karta.packets;

import com.ixume.karta.Main;
import com.ixume.karta.screen.ScreensManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.RelativeMovement;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.EnumSet;

public class ScreenPacketChannelHandler extends ChannelDuplexHandler {
    private final Player player;

    public ScreenPacketChannelHandler(Player player) {
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        if (packet instanceof ServerboundSetCarriedItemPacket carriedItemPacket) {
            if(ScreensManager.onPlayerScroll(carriedItemPacket, player)) ((CraftPlayer) player).getHandle().connection.send(new ClientboundSetCarriedItemPacket(4));
        } else if (packet instanceof ServerboundCommandSuggestionPacket) {
            ScreensManager.onPlayerTextInput(((ServerboundCommandSuggestionPacket) packet).getCommand(), player);
        } else if (packet instanceof ServerboundSwingPacket) {
            ScreensManager.onPlayerSwing(((ServerboundSwingPacket) packet), player);
        } else if (packet instanceof ServerboundMovePlayerPacket movePacket) {
            if (ScreensManager.onPlayerMove(movePacket, player)) {
                if (movePacket.xRot != 0 || movePacket.yRot != 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        ((CraftPlayer) player).getHandle().connection.send(new ClientboundPlayerPositionPacket(movePacket.x, movePacket.y, movePacket.z, 0, 0, EnumSet.noneOf(RelativeMovement.class), 0));
                    });
                }

                return;
            }
        } else if (packet instanceof ServerboundInteractPacket interactPacket) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                Field idField;
                try {
                    idField = ServerboundInteractPacket.class.getDeclaredField("a");
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
                idField.setAccessible(true);
                        try {
                            System.out.println("interact: " + idField.get(interactPacket));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                idField.setAccessible(false);
            });
        } else if (packet instanceof ServerboundAcceptTeleportationPacket) {
            if (ScreensManager.getScreen(player).isPresent()) {
                return;
            }
        } else {
//                    System.out.println(packet.toString());
        }

        super.channelRead(channelHandlerContext, packet);
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise promise) {
        if (ScreensManager.getScreen(player).isPresent() && packet instanceof ClientboundSetTimePacket clientboundSetTimePacket) {
            System.out.println("blocked packet");
            return;
        }

        channelHandlerContext.write(packet, promise);
    }
}
