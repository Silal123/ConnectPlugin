package dev.silal.connectplugin;

import dev.silal.connectplugin.core.commands.DataCommand;
import dev.silal.connectplugin.core.commands.LinkCommand;
import dev.silal.connectplugin.core.commands.UnlinkCommand;
import dev.silal.connectplugin.core.commands.tabcompleter.DataCommandTabCompleter;
import dev.silal.connectplugin.core.connection.DataStorage;
import dev.silal.connectplugin.core.utils.Configuration;
import dev.silal.connectplugin.core.utils.JsonManager;
import dev.silal.connectplugin.core.utils.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ConnectPlugin extends JavaPlugin {

    public static String API_BASE = "https://api.conbot.xyz";
    public static String FRE_BASE = "https://localhost:5173";

    private static ConnectPlugin instance;
    public static ConnectPlugin getInstance() { return instance; }

    private File pluginFolder;
    public File getPluginFolder() {
        return pluginFolder;
    }

    private DataStorage dataStorage;
    public DataStorage getDataStorage() {
        return dataStorage;
    }

    private Configuration config;
    public Configuration getConfiguration() {
        return config;
    }

    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        pluginFolder = getDataFolder();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        config = new Configuration(this);
        dataStorage = new DataStorage(this);
        getLogger().info("Api ready!");

        getLogger().info("Checking for updates...");
        checkForUpdates();

        getLogger().info("Setting up Metrics...");
        this.metrics = new Metrics(this, 26121);

        getLogger().info("Checking api key...");
        if (config.getApiKey().equals("YOUR_API_TOKEN")) {
            getLogger().warning("Please change the api token!");
        }

        getLogger().info("Setting up commands...");
        getCommand("link").setExecutor(new LinkCommand());
        getCommand("unlink").setExecutor(new UnlinkCommand());

        getCommand("data").setExecutor(new DataCommand());
        getCommand("data").setTabCompleter(new DataCommandTabCompleter());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
