package dev.silal.connectplugin.core.connection;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.errors.UnexpectedConnectionError;
import dev.silal.connectplugin.core.errors.UnknownUserError;
import dev.silal.connectplugin.core.utils.Request;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
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

    public UUID getUUIDByDiscordId(long id) throws UnexpectedConnectionError, UnknownUserError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE + "/account/u/" + id).method(Request.Method.GET).send();
            int status = request.getResponseCode();

            if (status == 404) {
                throw new UnknownUserError();
            }

            if (status != 200 || request.getResponseJson() == null) {
                throw new UnexpectedConnectionError();
            }

            return UUID.fromString(request.getResponseJson().getString("uuid"));
        } catch (IOException | IllegalArgumentException ioex) {
            throw new UnexpectedConnectionError();
        }
    }
}
