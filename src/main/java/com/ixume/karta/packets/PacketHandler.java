package com.ixume.karta.packets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketHandler {
    public PacketHandler() {
    }

    public void removePlayer(Player player) {
        try {
            Field connectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("c");
            connectionField.setAccessible(true);
            Channel channel = ((Connection) connectionField.get(((CraftPlayer) player).getHandle().connection)).channel;
            connectionField.setAccessible(false);
            channel.eventLoop().submit(() -> {
                channel.pipeline().remove(player.getName());
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ScreenPacketChannelHandler(player);

        try {
            Field connectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("c");
            connectionField.setAccessible(true);
            ChannelPipeline pipeline = ((Connection) connectionField.get(((CraftPlayer) player).getHandle().connection)).channel.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
            connectionField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
