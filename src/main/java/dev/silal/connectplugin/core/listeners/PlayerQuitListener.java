package dev.silal.connectplugin.core.listeners;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final ConnectPlugin plugin;
    public PlayerQuitListener(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        this.plugin.getWebsocket().sendEvent("PLAYER_QUIT", new JsonManager().addProperty("account", new JsonManager().addProperty("name", p.getName()).addProperty("uuid", p.getUniqueId().toString())));
    }

}
