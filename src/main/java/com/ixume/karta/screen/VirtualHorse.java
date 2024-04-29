package com.ixume.karta.screen;

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.joml.Vector3d;

import java.lang.reflect.Field;

public class VirtualHorse {
    private int entityID;
    private final Player player;
    private final Vector3d pos;

    public VirtualHorse(Player player, Vector3d pos) {
        this.player = player;
        this.pos = pos.add(0, player.getEyeHeight() + 0.5, 0);
    }

    public void lock() {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        Horse horse = new Horse(EntityType.HORSE, serverPlayer.level());
        entityID = horse.getId();
        horse.setYRot(0);
        horse.setXRot(0);
        horse.setYHeadRot(0);
        horse.setYBodyRot(0);
        horse.setInvisible(false);
        horse.setNoGravity(true);
        horse.setNoAi(true);
        horse.setPos(pos.x, pos.y, pos.z);

        ClientboundSetPassengersPacket passengersPacket = new ClientboundSetPassengersPacket(horse);

        try {
            Field passengersField = ClientboundSetPassengersPacket.class.getDeclaredField("b");
            passengersField.setAccessible(true);
            passengersField.set(passengersPacket, new int[]{craftPlayer.getEntityId()});
            passengersField.setAccessible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ItemStack stick = new ItemStack(Items.STICK);
        for (int slot = 36; slot <= 43; slot++) serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(0, 0, slot, stick));
        serverPlayer.connection.send(new ClientboundSetCarriedItemPacket(4));

        serverPlayer.connection.send(new ClientboundAddEntityPacket(horse));
        serverPlayer.connection.send(new ClientboundSetEntityDataPacket(entityID, horse.getEntityData().packDirty()));
        serverPlayer.connection.send(new ClientboundSetCameraPacket(horse));
        serverPlayer.connection.send(passengersPacket);

        craftPlayer.getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 3));
    }

    public void unlock() {
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        craftPlayer.getHandle().connection.send(new ClientboundRemoveEntitiesPacket(entityID));

        craftPlayer.getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, 1));
        player.teleport(player);
        serverPlayer.connection.send(new ClientboundSetCameraPacket(serverPlayer));
    }
}
