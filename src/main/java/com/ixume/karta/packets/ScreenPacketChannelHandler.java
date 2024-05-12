package com.ixume.karta.packets;

import com.ixume.karta.Karta;
import com.ixume.karta.screen.ScreensManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class ScreenPacketChannelHandler extends ChannelDuplexHandler {
    private final Player player;
    private double recentXRot = 0;
    private double recentYRot = 0;

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
            if (ScreensManager.onPlayerMove(movePacket.yRot - recentYRot, movePacket.xRot - recentXRot, player)) {
                recentXRot = movePacket.xRot;
                recentYRot = movePacket.yRot;
                return;
            }
        } else if (packet instanceof ServerboundInteractPacket interactPacket) {
            Bukkit.getScheduler().runTask(Karta.getInstance(), () -> {

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
