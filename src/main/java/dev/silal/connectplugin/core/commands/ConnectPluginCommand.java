package dev.silal.connectplugin.core.commands;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.Permission;
import dev.silal.connectplugin.core.utils.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConnectPluginCommand implements CommandExecutor {

    /**
    * The /cp or /connectplugin or /conpl command
    * */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission(Permission.SETUP_SERVER.key())) {
            sender.sendMessage(Prefix.SYSTEM.key() + "You don`t have §cpermission§7 to do that!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Please specify what you want to §econfigure§7!");
            return true;
        }
        String option = args[0];

        if (option.equals("scoreboard")) {
            if (args.length < 2) {
                sender.sendMessage(Prefix.SYSTEM.key() + "Please specify what you want to §edo§7 with the scoreboard!");
                return true;
            }
            String scoreboardOption = args[1];

            if (scoreboardOption.equals("bind")) {
                if (args.length < 3) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a §escoreboard objective§7!");
                    return true;
                }
                String scoreboardObjective = args[2];

                Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                Objective objective = scoreboard.getObjective(scoreboardObjective);

                if (objective == null) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a existing §escoreboard objective§7!");
                    return true;
                }

                if (args.length < 4) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a §edata key§7 you want to bind it to!");
                    return true;
                }
                String dataKey = args[3];

                if (ConnectPlugin.getInstance().getConfiguration().getBoundScoreboards().containsKey(scoreboardObjective)) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "The scoreboard is §ealready bound§7 to a data key!");
                    return true;
                }

                ConnectPlugin.getInstance().getConfiguration().addBoundScoreboard(scoreboardObjective, dataKey);
                ConnectPlugin.getInstance().getScoreboardBind().syncData(scoreboardObjective, dataKey);
                sender.sendMessage(Prefix.SYSTEM.key() + "The scoreboard §a" + scoreboardObjective + "§7 was bound to the key §e" + dataKey + "§7");
                return true;
            }

            if (scoreboardOption.equals("remove")) {
                if (args.length < 3) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a §escoreboard objective§7!");
                    return true;
                }
                String scoreboardObjective = args[2];

                if (!ConnectPlugin.getInstance().getConfiguration().getBoundScoreboards().containsKey(scoreboardObjective)) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "The scoreboard is §enot bound§7!");
                    return true;
                }

                ConnectPlugin.getInstance().getConfiguration().removeBoundScoreboard(scoreboardObjective);
                sender.sendMessage(Prefix.SYSTEM.key() + "The scoreboard was §asuccessfully§7 unbound!");
                return true;
            }

            if (scoreboardOption.equals("list")) {
                sender.sendMessage(Prefix.SYSTEM.key() + "§a§lList of all bound scoreboards:");
                for (Map.Entry<String, String> entry : ConnectPlugin.getInstance().getConfiguration().getBoundScoreboards().entrySet()) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "§8- §7" + entry.getKey() + "§8:§7 " + entry.getValue());
                }
                return true;
            }

            sender.sendMessage(Prefix.SYSTEM.key() + "The scoreboard option §e" + scoreboardOption + "§7 was not found!");
            return true;
        }

        if (option.equals("apiKey")) {
            if (args.length < 2) {
                sender.sendMessage(Prefix.SYSTEM.key() + "Please specify your §eapi key§7!");
                return true;
            }
            String apiKey = args[1];

            ConnectPlugin.getInstance().getConfiguration().setApiKey(apiKey);
            sender.sendMessage(Prefix.SYSTEM.key() + "The api key was §asuccessfully§7 updated!");
            return true;
        }

        sender.sendMessage(Prefix.SYSTEM.key() + "The option §e" + option + "§7 was not found!");
        return true;
    }
}
