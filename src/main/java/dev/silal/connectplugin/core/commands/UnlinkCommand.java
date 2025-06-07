package dev.silal.connectplugin.core.commands;

import dev.silal.connectplugin.core.utils.Prefix;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnlinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;

        p.sendMessage(Prefix.SYSTEM.key() + "Please user §a/unlink§7 on a discord server with the ConnectBot on it!");
        p.sendMessage(Prefix.SYSTEM.key() + "§eNote§7: If you cant access your discord account please contact us!");

        return true;
    }
}
