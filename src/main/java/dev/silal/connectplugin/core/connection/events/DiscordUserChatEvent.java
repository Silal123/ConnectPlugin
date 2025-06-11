package dev.silal.connectplugin.core.connection.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DiscordUserChatEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final String discordUserName;
    private final long discordUserId;
    private String message;
    private String format;
    private boolean cancelled = false;

    public DiscordUserChatEvent(String discordUserName, long discordUserId, String message) {
        this.discordUserName = discordUserName;
        this.discordUserId = discordUserId;
        this.message = message;
        this.format = "<[dc] " + discordUserName + "> %s"; // Default format
    }

    public String getDiscordUserName() {
        return discordUserName;
    }

    public long getDiscordUserId() {
        return discordUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
