package dev.silal.connectplugin.core.listeners;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ConnectPlugin plugin;
    public PlayerJoinListener(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        this.plugin.getWebsocket().sendEvent("PLAYER_JOIN", new JsonManager().addProperty("account", new JsonManager().addProperty("name", p.getName()).addProperty("uuid", p.getUniqueId().toString())));
    }

}
