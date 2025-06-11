package dev.silal.connectplugin;

import dev.silal.connectplugin.core.Scheduler;
import dev.silal.connectplugin.core.commands.*;
import dev.silal.connectplugin.core.commands.tabcompleter.ConnectPluginCommandTabCompleter;
import dev.silal.connectplugin.core.commands.tabcompleter.DataCommandTabCompleter;
import dev.silal.connectplugin.core.connection.DataStorage;
import dev.silal.connectplugin.core.connection.ConnectWebsocket;
import dev.silal.connectplugin.core.listeners.PlayerChatListener;
import dev.silal.connectplugin.core.listeners.PlayerDeathListener;
import dev.silal.connectplugin.core.listeners.PlayerJoinListener;
import dev.silal.connectplugin.core.listeners.PlayerQuitListener;
import dev.silal.connectplugin.core.placeholderapi.ConnectPlaceholder;
import dev.silal.connectplugin.core.system.ScoreboardBind;
import dev.silal.connectplugin.core.utils.Configuration;
import dev.silal.connectplugin.core.utils.JsonManager;
import dev.silal.connectplugin.core.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public final class ConnectPlugin extends JavaPlugin {

    private static final boolean development = false;

    /**
    * Get if the plugin is in development
    *
    * @return Development status
    * */
    public static boolean isDevelopment() { return development; }

    public static String API_BASE = development ? "http://localhost:8080" : "https://api.conbot.xyz";
    public static String FRE_BASE = "https://localhost:5173";

    public static String API_WEBSOCKET = development ? "ws://localhost:8080/ws" : "wss://api.conbot.xyz/ws";

    private static ConnectPlugin instance;
    /**
    * Get the plugin instance
    *
    * @return The instance of ConnectPlugin
    * */
    public static ConnectPlugin getInstance() { return instance; }

    private File pluginFolder;
    /**
    * Get the data folder of the plugin
    *
    * @return The data folder
    * */
    public File getPluginFolder() {
        return pluginFolder;
    }

    private DataStorage dataStorage;
    /**
    * Get the data Storage wich is used to get the UserData
    *
    * @return The DataStorage object
    * */
    public DataStorage getDataStorage() {
        return dataStorage;
    }

    private Configuration config;
    /**
    * The configuration of the plugin
    *
    * @return The plugin Configuration
    * */
    public Configuration getConfiguration() {
        return config;
    }

    private ConnectWebsocket websocket;
    /**
    * Get the websocket wich is connected to the api
    *
    * @return The websocket
    * */
    public ConnectWebsocket getWebsocket() {
        return websocket;
    }

    /**
    * Set the Websocket (Not recommended)
    *
    * @param client The websocket client
    * */
    public void setWebsocket(ConnectWebsocket client) {
        this.websocket = client;
    }

    private ScoreboardBind scoreboardBind;
    /**
    * Get the ScoreboardBind object wich is used to sync the scoreboard with the database
    *
    * @return The scoreboard binding
    * */
    public ScoreboardBind getScoreboardBind() {
        return scoreboardBind;
    }

    /**
    * Get a Json object with the server dat
    *
    * @return a JsonManage with the server data
    * */
    public JsonManager getServerData() {
        JsonManager players = new JsonManager();
        Bukkit.getOnlinePlayers().forEach(player -> players.addProperty(player.getUniqueId().toString(), player.getName()));

        JsonManager plugins = new JsonManager();
        Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins()).toList().forEach(plugin1 -> { plugins.addProperty(plugin1.getName(), plugin1.getDescription().getVersion()); });

        return new JsonManager().addProperty("players", players).addProperty("motd", Bukkit.getServer().getMotd()).addProperty("max_players", Bukkit.getServer().getMaxPlayers()).addProperty("whitelist", Bukkit.getServer().hasWhitelist()).addProperty("server_version", Bukkit.getServer().getVersion()).addProperty("minecraft_version", Bukkit.getServer().getBukkitVersion()).addProperty("plugins", plugins);
    }

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        pluginFolder = getDataFolder();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        config = new Configuration(this);
        dataStorage = new DataStorage(this);
        scoreboardBind = new ScoreboardBind(this);
        getLogger().info("Api ready!");

        getLogger().info("Checking for updates...");
        checkForUpdates();

        getLogger().info("Setting up Metrics...");
        this.metrics = new Metrics(this, 26121);

        getLogger().info("Checking api key...");
        if (config.getApiKey().equals("YOUR_API_TOKEN")) {
            getLogger().warning("Please change the api token!");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ConnectPlaceholder(this).register();
        }

        getLogger().info("Connecting to websocket...");
        ConnectWebsocket.connectSocket();

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new PlayerJoinListener(this), this);
        manager.registerEvents(new PlayerQuitListener(this), this);
        manager.registerEvents(new PlayerChatListener(this), this);
        manager.registerEvents(new PlayerDeathListener(this), this);

        getLogger().info("Setting up commands...");
        getCommand("link").setExecutor(new LinkCommand());
        getCommand("unlink").setExecutor(new UnlinkCommand());

        getCommand("data").setExecutor(new DataCommand());
        getCommand("data").setTabCompleter(new DataCommandTabCompleter());

        getCommand("bot").setExecutor(new BotCommand());

        getCommand("connectplugin").setExecutor(new ConnectPluginCommand());
        getCommand("connectplugin").setTabCompleter(new ConnectPluginCommandTabCompleter());

        Scheduler.start();
    }

    @Override
    public void onDisable() {
        if (getWebsocket() != null && getWebsocket().getSocket() != null) {
            getLogger().info("Closing websocket!");
            getWebsocket().getSocket().sendClose(10, "Server shutdown").thenRun(() -> { getLogger().info("Websocket closed!"); });
            getWebsocket().getSocket().abort();
        }
        Scheduler.stop();
    }

    private void checkForUpdates() {
        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                String apiUrl = "https://api.github.com/repos/Silal123/ConnectPlugin/releases/latest";
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JsonManager release = new JsonManager(json.toString());
                String latestVersion = release.getString("tag_name").replace("v", "");

                String currentVersion = getDescription().getVersion();

                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    getLogger().warning("There is a new version available: " + latestVersion + "; Download: https://plugin.conbot.xyz");
                    getLogger().warning("You are using: " + currentVersion);
                } else {
                    getLogger().info("Your plugin is up to date!");
                }

            } catch (Exception e) {
                getLogger().warning("Error while checking for updates: " + e.getMessage());
            }
        });
    }
}
