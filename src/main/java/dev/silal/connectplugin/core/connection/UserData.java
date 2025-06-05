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

    private JsonManager getDiscordData() throws UnexpectedConnectionError, UnknownError {
        try {
            Request request = new Request(ConnectPlugin.API_BASE + "/account/m/" + uuid.toString()).method(Request.Method.GET).send();
            int status = request.getResponseCode();

            if (status == 404) {
                throw new UnknownError();
            }

            if (status != 200) {
                throw new UnexpectedConnectionError();
            }

            return request.getResponseJson();
        } catch (IOException ioE) {
            throw new UnexpectedConnectionError();
        }
    }

    public int getDiscordId() throws UnexpectedConnectionError, UnknownError {
        JsonManager discordData = getDiscordData();
        if (!discordData.hasKey("id")) {
            throw new UnexpectedConnectionError();
        }
        return discordData.getInt("id");
    }

    public String getDiscordName() throws UnexpectedConnectionError, UnknownError {
        JsonManager discordData = getDiscordData();
        return discordData.hasKey("name") ? discordData.getString("name") : null;
    }
}
