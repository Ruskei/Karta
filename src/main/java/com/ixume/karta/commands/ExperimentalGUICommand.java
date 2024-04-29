package com.ixume.karta.commands;

import com.ixume.karta.screen.ScreensManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ExperimentalGUICommand implements CommandExecutor, TabExecutor {

    public static final String[] args0 = {"alchemist"};

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) return false;

        if (args.length >= 1) {
            switch (args[0]) {
                case "alchemist" -> {
//                    ScreensManager.onOpenAlchemist(p);
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        final List<String> completion = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], List.of(args0), completion);
        Collections.sort(completion);
        return completion;
    }
}
