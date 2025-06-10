package dev.silal.connectplugin.core.listeners;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final ConnectPlugin plugin;
    public PlayerChatListener(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        String message = event.getMessage();

        this.plugin.getWebsocket().sendEvent("MESSAGE_SEND", new JsonManager().addProperty("message", message).addProperty("account", new JsonManager().addProperty("name", p.getName()).addProperty("uuid", p.getUniqueId().toString())));
    }

}
