package dev.silal.connectplugin;

import dev.silal.connectplugin.core.commands.DataCommand;
import dev.silal.connectplugin.core.commands.LinkCommand;
import dev.silal.connectplugin.core.connection.DataStorage;
import dev.silal.connectplugin.core.utils.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ConnectPlugin extends JavaPlugin {

    public static String API_BASE = "http://localhost:8080";
    public static String FRE_BASE = "http://localhost:5173";

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

    @Override
    public void onEnable() {
        instance = this;
        pluginFolder = getDataFolder();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        config = new Configuration(this);
        dataStorage = new DataStorage(this);

        if (config.getApiKey().equals("YOUR_API_TOKEN")) {
            getLogger().warning("Please change the api token!");
        }

        getCommand("link").setExecutor(new LinkCommand());
        getCommand("data").setExecutor(new DataCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
