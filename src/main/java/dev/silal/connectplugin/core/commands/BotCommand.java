package dev.silal.connectplugin.core.commands;

import dev.silal.connectplugin.core.utils.Prefix;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class BotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Invite our bot on https://invite.conbot.xyz");
            return true;
        }

        TextComponent link = new TextComponent("https://invite.conbot.xyz");
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://invite.conbot.xyz"));

        TextComponent component = new TextComponent(Prefix.SYSTEM.key() + "Invite our bot on ");
        component.addExtra(link);

        sender.spigot().sendMessage(component);
        return true;
    }
}
