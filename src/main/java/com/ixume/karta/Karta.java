package com.ixume.karta;

import com.ixume.karta.packets.PacketHandler;
import com.ixume.karta.screen.ScreensManager;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.map.MinecraftFont;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3d;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Karta extends JavaPlugin implements Listener {
    private PacketHandler packetHandler;
    private PlayerLocationManager locationManager;
    private static Karta plugin;

    public static Karta getInstance() {return plugin;}
    private static BukkitTask tick;

    public String text;

    @Override
    public void onEnable() {
        System.out.println("privet mir");
        plugin = this;
        registerPacketHandler(new PacketHandler());
        registerLocationManager(new PlayerLocationManager() {
            @Override
            public Vector3d intakePlayer(Player player) {
                return PlayerLocationManager.super.intakePlayer(player);
            }
        });

        Bukkit.getPluginManager().registerEvents(this, this);
        text = "";

        tick = Bukkit.getScheduler().runTaskTimerAsynchronously(this, ScreensManager::tick, 1L, 1L);

        if (getInstance() == null) System.out.println("null thingi");
    }

    public void registerPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void registerLocationManager(PlayerLocationManager locationManager) {this.locationManager = locationManager;}

    public PlayerLocationManager getLocationManager() {
        return locationManager;
    }

    @Override
    public void onDisable() {
        tick.cancel();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        packetHandler.injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ScreensManager.onLeave(event.getPlayer());
        packetHandler.removePlayer(event.getPlayer());
    }
}
