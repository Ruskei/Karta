package com.ixume.karta.screen;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class IndividualMap {
    private final Player player; // who the screen is being showed to
    private final int mapID; //id of the map; used for sending packets
    private int frameID; //entity id of frame
    public byte[] mapData; //128*128 array representing a color for each pixel
    private final Vec3i pos;
    private ItemFrame frame;
    public List<MapDecoration> icons;

    public IndividualMap(Player player, int mapID, Vec3i pos) {
        this.player = player;
        this.mapID = mapID;
        this.mapData = new byte[128 * 128];
        this.pos = pos;
        icons = new ArrayList<>();
        //fill with background pixels
        for (int i = 0; i < 128 * 128; i++) {mapData[i] = (byte) 0;}
    }

    public int getFrameID() {
        return frameID;
    }

    public void sendSpawnPacket() {
        ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
        ItemFrame frame = new ItemFrame(EntityType.GLOW_ITEM_FRAME, level);
        frame.setInvisible(true);
        frame.setRotation(0);
        frame.setDeltaMovement(Vec3.ZERO);
        frame.setPos(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        frame.setDirection(Direction.NORTH);

        ItemStack stack = new ItemStack(Items.FILLED_MAP);
        stack.getOrCreateTag().putInt("map", mapID);

        frame.setItem(stack);
        this.frame = frame;
        this.frameID = frame.getId();

        //just passing in the itemframe entity doesn't seem to properly add its data (direction, map), tho i'm probably just bad at programming
        ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(frame.getId(), frame.getUUID(),
                pos.getX(), pos.getY(), pos.getZ(), 0f, 0f,
                EntityType.GLOW_ITEM_FRAME,
                2, Vec3.ZERO, 0);

        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        serverPlayer.connection.send(new ClientboundMapItemDataPacket(mapID, (byte) 0, true, icons, new MapItemSavedData.MapPatch(0, 0, 128, 128, mapData)));
        serverPlayer.connection.send(spawnPacket);
        serverPlayer.connection.send(new ClientboundSetEntityDataPacket(frameID, frame.getEntityData().packDirty()));
    }

    //update map data
    public void sendTrackerPacket() {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        serverPlayer.connection.send(new ClientboundMapItemDataPacket(mapID, (byte) 0, true, icons, new MapItemSavedData.MapPatch(0, 0, 128, 128, mapData)));
    }
}
