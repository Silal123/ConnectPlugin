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

    /**
    * Get the data of a player by the UUID
    *
    * @param uuid The uuid of the minecraft player
    * @return The UserData
    * */
    public UserData getPlayer(UUID uuid) {
        return new UserData(plugin, uuid);
    }

    /**
    * Get the data of a player
     *
    * @param p The player of wich to get the data
    * @return The UserData
    * */
    public UserData getPlayer(Player p) {
        return new UserData(plugin, p.getUniqueId());
    }

    /**
    * Get the data of a offline player
     *
    * @param p The offline player
    * @return The UserData
    * */
    public UserData getPlayer(OfflinePlayer p) {
        return new UserData(plugin, p.getUniqueId());
    }

    /**
    * Get the uuid of a discord user by the discord user id
    *
    * @param id The discord user id
    * @return The uuid of the discord user
    * @throws UnexpectedConnectionError If the connection to the api fails
    * @throws UnknownUserError If the user is not linked
    * */
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
