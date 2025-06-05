package dev.silal.connectplugin.core.commands;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.errors.*;
import dev.silal.connectplugin.core.utils.Permission;
import dev.silal.connectplugin.core.utils.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class DataCommand implements CommandExecutor {

    public static boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (Exception e) {}
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if (sender instanceof Player p && !(p.hasPermission(Permission.GET_DATA.key()) || p.hasPermission(Permission.SET_DATA.key()))) {
            sender.sendMessage(Prefix.SYSTEM.key() + "You don`t have §cpermission§7 to do that!");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a §eplayer§7!");
            return true;
        }

        UUID uuid = null;
        if (isValidUUID(args[0])) {
            uuid = UUID.fromString(args[0]);
        }

        if (uuid == null && Bukkit.getOfflinePlayerIfCached(args[0]) != null) {
            uuid = Bukkit.getOfflinePlayerIfCached(args[0]).getUniqueId();
        }

        if (args.length < 2) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Please select your §eaction§7 (set/get)!");
            return true;
        }
        String action = args[1];

        if (!List.of("get", "set").contains(action)) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Please select a §evalid§7 option (set/get)!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Prefix.SYSTEM.key() + "Please select a §ekey§7!");
            return true;
        }
        String key = args[2];

        final UUID final_uuid = uuid;

        if (action.equals("get")) {
            Bukkit.getScheduler().runTaskAsynchronously(ConnectPlugin.getInstance(), () -> {
                String value = null;
                try {
                    value = ConnectPlugin.getInstance().getDataStorage().getPlayer(final_uuid).getValue(key);
                } catch (UnexpectedConnectionError uce) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "We had an §cunexpected error§7 while calling your data!");
                    return;
                } catch (UnauthorizedAPIAccessError uaae) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Your api token is §cnot valid§7. Please update your config!");
                    return;
                } catch (UnknownUserError uue) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "A user with that uuid/name was not found!");
                    return;
                }

                sender.sendMessage(Prefix.SYSTEM.key() + "The data §e" + key + "§7 of the player is §a" + value + "§7!");
            });
            return true;
        }

        if (action.equals("set")) {
            if (args.length < 4) {
                sender.sendMessage(Prefix.SYSTEM.key() + "Please specify a §evalue§7!");
                return true;
            }

            String value = null;
            for (int i = 3; i < args.length; i++) {
                if (value == null) value = "";
                value += args[i] + " ";
            }
            if (value.endsWith(" ")) value = value.substring(0, value.length() -1);

            final String final_value = value;

            Bukkit.getScheduler().runTaskAsynchronously(ConnectPlugin.getInstance(), () -> {
                try {
                    ConnectPlugin.getInstance().getDataStorage().getPlayer(final_uuid).setValue(key, final_value);
                } catch (UnexpectedConnectionError uce) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "We had an §cunexpected error§7 while calling your data!");
                    return;
                } catch (UnauthorizedAPIAccessError uaae) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "Your api token is §cnot valid§7. Please update your config!");
                    return;
                } catch (UnknownUserError uue) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "A user with that uuid/name was not found!");
                    return;
                } catch (InvalidDataKeyError idke) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "The given §ekey§7 (" + key + ") is not valid! Please add it using the setup menu on your discord server!");
                    return;
                } catch (ValueTooLongError vtle) {
                    sender.sendMessage(Prefix.SYSTEM.key() + "The value is §etoo long§7! The maximum is 100 characters!");
                    return;
                }

                sender.sendMessage(Prefix.SYSTEM.key() + "The data value §e" + key + "§7 is not §a" + final_value + "§7!");
            });
            return true;
        }

        sender.sendMessage(Prefix.SYSTEM.key() + "Option not found!");
        return true;
    }
}
