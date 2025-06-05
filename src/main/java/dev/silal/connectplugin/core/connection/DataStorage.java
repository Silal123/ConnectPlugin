package dev.silal.connectplugin.core.connection;

import dev.silal.connectplugin.ConnectPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DataStorage {

    private final ConnectPlugin plugin;

    public DataStorage(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    public UserData getPlayer(UUID uuid) {
        return new UserData(plugin, uuid);
    }

    public UserData getPlayer(Player p) {
        return new UserData(plugin, p.getUniqueId());
    }

    public UserData getPlayer(OfflinePlayer p) {
        return new UserData(plugin, p.getUniqueId());
    }
}
