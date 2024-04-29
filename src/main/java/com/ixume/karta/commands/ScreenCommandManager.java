package com.ixume.karta.commands;

import com.google.common.base.Predicates;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ScreenCommandManager {
    public static void enableTextInput(Player p) {
        RootCommandNode<SharedSuggestionProvider> commandNode = new RootCommandNode<SharedSuggestionProvider>();

        commandNode.addChild(
                new ArgumentCommandNode<>(
                        "command",
                        StringArgumentType.greedyString(),
                        null,
                        Predicates.alwaysTrue(),
                        null,
                        null,
                        true,
                        (ctx, builder) -> null
                )
        );

        Packet<?> COMMAND_PACKET = new ClientboundCommandsPacket(commandNode);
        CraftPlayer craftPlayer = ((CraftPlayer) p);

        craftPlayer.getHandle().connection.send(COMMAND_PACKET);
    }

    public static void disableTextInput(Player p) {
        p.updateCommands();
    }
}
