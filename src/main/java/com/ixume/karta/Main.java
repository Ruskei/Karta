package com.ixume.karta;

import com.ixume.karta.dataloading.*;
import com.ixume.karta.commands.ExperimentalGUICommand;
import com.ixume.karta.packets.PacketHandler;
import com.ixume.karta.screen.ScreensManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.joml.Vector3d;

public final class Main extends JavaPlugin implements Listener {
    private PacketHandler packetHandler;
    private PlayerLocationManager locationManager;
    private static Main plugin;

    public static Main getInstance() {return plugin;}
    private static BukkitTask tick;

    public String text;

    @Override
    public void onEnable() {
        plugin = this;
        registerPacketHandler(new PacketHandler());
        registerLocationManager(new PlayerLocationManager() {
            @Override
            public Vector3d intakePlayer(Player player) {
                return PlayerLocationManager.super.intakePlayer(player);
            }
        });

        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("servergui").setExecutor(new ExperimentalGUICommand());
        getCommand("servergui").setTabCompleter(new ExperimentalGUICommand());

        System.out.println("hallo");
        ResourceLoader.init();
        text = "";

        tick = Bukkit.getScheduler().runTaskTimerAsynchronously(this, ScreensManager::tick, 1L, 1L);
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
