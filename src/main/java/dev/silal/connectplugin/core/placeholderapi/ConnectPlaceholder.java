package dev.silal.connectplugin.core.placeholderapi;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.connection.UserData;
import dev.silal.connectplugin.core.errors.UnauthorizedAPIAccessError;
import dev.silal.connectplugin.core.errors.UnexpectedConnectionError;
import dev.silal.connectplugin.core.errors.UnknownUserError;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConnectPlaceholder extends PlaceholderExpansion {

    private final ConnectPlugin plugin;

    public ConnectPlaceholder(ConnectPlugin plugin) { this.plugin = plugin; }

    @Override
    public @NotNull String getIdentifier() {
        return "con";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Silal";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        UserData data = plugin.getDataStorage().getPlayer(player);

        if (identifier.equalsIgnoreCase("discord_id")) {
            try {
                return String.valueOf(data.getDiscordId());
            } catch (UnknownUserError ue) {
                return "not linked";
            } catch (UnexpectedConnectionError uce) {
                return "error";
            }
        }

        if (identifier.equalsIgnoreCase("discord_name")) {
            try {
                return data.getDiscordName();
            } catch (UnknownUserError ue) {
                return "not linked";
            } catch (UnexpectedConnectionError uce) {
                return "error";
            }
        }

        if (identifier.startsWith("key_")) {
            String key = identifier.substring(4);

            try {
                return data.getValue(key);
            } catch (UnknownUserError ue) {
                return "not linked";
            } catch (UnauthorizedAPIAccessError uaae) {
                return "invalid token";
            } catch (UnexpectedConnectionError uce) {
                return "error";
            }
        }

        return null;
    }
}
