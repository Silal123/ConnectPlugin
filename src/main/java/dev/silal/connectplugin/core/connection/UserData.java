package dev.silal.connectplugin.core.connection;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.errors.*;
import dev.silal.connectplugin.core.utils.JsonManager;
import dev.silal.connectplugin.core.utils.Request;

import java.io.IOException;
import java.util.UUID;

public class UserData {

    private final ConnectPlugin plugin;
    private final UUID uuid;

    public UserData(ConnectPlugin plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;
    }

    /**
    * Set the value of a player by key
    *
    * @param key The key to the data
    * @param value The value to set
    * @throws UnexpectedConnectionError The connection to the api failed
    * @throws UnauthorizedAPIAccessError The api token in the config is not valid
    * @throws UnknownUserError The user is not linked
    * @throws InvalidDataKeyError The key is not registered using the discord setup
    * @throws ValueTooLongError The given data value is too long to be stored
    * */
    public void setValue(String key, String value) throws UnexpectedConnectionError, UnauthorizedAPIAccessError, UnknownUserError, InvalidDataKeyError, ValueTooLongError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE + "/players/" + uuid.toString() + "/values").body(new JsonManager().addProperty(key, value)).method(Request.Method.PUT).header("Authorization", plugin.getConfiguration().getApiKey()).send();
            int status = request.getResponseCode();

            if (status == 401) {
                throw new UnauthorizedAPIAccessError();
            }

            if (status == 400) {
                throw new UnknownUserError();
            }

            if (status == 422) {
                throw new InvalidDataKeyError();
            }

            if (status == 413) {
                throw new ValueTooLongError();
            }

            if (status != 200) {
                throw new UnexpectedConnectionError();
            }
        } catch (IOException ioE) {
            throw new UnexpectedConnectionError();
        }
    }

    /**
    * Get the data value of a player
    *
    * @param key The key to the data
    * @return The value of the key
    * @throws UnexpectedConnectionError The connection to the api failed
    * @throws UnauthorizedAPIAccessError The api token in the config is not valid
    * @throws UnknownUserError The user is not linked
    * */
    public String getValue(String key) throws UnexpectedConnectionError, UnauthorizedAPIAccessError, UnknownUserError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE + "/players/" + uuid.toString() + "/values/" + key).method(Request.Method.GET).header("Authorization", plugin.getConfiguration().getApiKey()).send();
            int status = request.getResponseCode();

            if (status == 401) {
                throw new UnauthorizedAPIAccessError();
            }

            if (status == 400) {
                throw new UnknownUserError();
            }

            if (status != 200) {
                throw new UnexpectedConnectionError();
            }

            if (request.getResponseJson() == null || !request.getResponseJson().hasKey("data")) {
                throw new UnexpectedConnectionError();
            }

            return request.getResponseJson().getString("data");
        } catch (IOException ioE) {
            throw new UnexpectedConnectionError();
        }
    }

    /**
    * Check if a user is linked or not
    *
    * @return If the player is linked
    * @throws UnexpectedConnectionError The connection to the api failed
    * */
    public boolean isLinked() throws UnexpectedConnectionError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE).method(Request.Method.GET).send();
            int status = request.getResponseCode();
            JsonManager json = request.getResponseJson();

            if (status == 404 && json != null && json.hasKey("linked") && !json.getBoolean("linked")) {
                return false;
            }

            if (status != 200) {
                throw new UnexpectedConnectionError();
            }

            return true;
        } catch (IOException ioE) {
            throw new UnexpectedConnectionError();
        }
    }

    private JsonManager getDiscordData() throws UnexpectedConnectionError, UnknownUserError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE + "/account/m/" + uuid.toString()).method(Request.Method.GET).send();
            int status = request.getResponseCode();

            if (status == 404) {
                throw new UnknownUserError();
            }

            if (status != 200) {
                throw new UnexpectedConnectionError();
            }

            return request.getResponseJson();
        } catch (IOException ioE) {
            throw new UnexpectedConnectionError();
        }
    }

    /**
    * Get the discord id of the player
    *
    * @return Discord user id
    * @throws UnexpectedConnectionError The connection to the api failed
    * @throws UnknownUserError The user is not linked
    * */
    public long getDiscordId() throws UnexpectedConnectionError, UnknownUserError {
        JsonManager discordData = getDiscordData();
        if (!discordData.hasKey("id")) {
            throw new UnexpectedConnectionError();
        }
        return discordData.getLong("id");
    }

    /**
    * Get the discord name of the player
    *
    * @return Discord name
    * @throws UnexpectedConnectionError The connection to the api failed
    * @throws UnknownUserError The user is not linked
    * */
    public String getDiscordName() throws UnexpectedConnectionError, UnknownUserError {
        JsonManager discordData = getDiscordData();
        return discordData.hasKey("name") ? discordData.getString("name") : null;
    }
}
