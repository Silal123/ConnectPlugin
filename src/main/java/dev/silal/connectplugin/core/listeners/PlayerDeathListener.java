package dev.silal.connectplugin.core.listeners;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private ConnectPlugin plugin;
    public PlayerDeathListener(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        String reason = event.getDeathMessage();

        this.plugin.getWebsocket().sendEvent("PLAYER_DEATH", new JsonManager().addProperty("reason", reason).addProperty("account", new JsonManager().addProperty("name", p.getName()).addProperty("uuid", p.getUniqueId().toString())));
    }

}
