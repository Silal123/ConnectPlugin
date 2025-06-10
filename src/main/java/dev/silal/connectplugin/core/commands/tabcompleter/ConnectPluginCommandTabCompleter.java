package dev.silal.connectplugin.core.commands.tabcompleter;

import dev.silal.connectplugin.ConnectPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConnectPluginCommandTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> res = new ArrayList<>();

        if (args.length == 1) {
            if ("scoreboard".startsWith(args[0])) res.add("scoreboard");
        }

        if (args.length == 2) {
            if ("bind".startsWith(args[1])) res.add("bind");
            if ("remove".startsWith(args[1])) res.add("remove");
            if ("list".startsWith(args[1])) res.add("list");
        }

        if (args.length == 3 && args[1].equals("bind")) {
            for (Objective o : ConnectPlugin.getInstance().getServer().getScoreboardManager().getMainScoreboard().getObjectives()) {
                if (o.getName().startsWith(args[2])) res.add(o.getName());
            }
        }

        if (args.length == 3 && args[1].equals("remove")) {
            for (String obj : ConnectPlugin.getInstance().getConfiguration().getBoundScoreboards().keySet()) {
                if (obj.startsWith(args[2])) res.add(obj);
            }
        }

        return res;
    }
}
