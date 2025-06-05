package dev.silal.connectplugin.core.utils;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.value.ConfigValue;
import dev.silal.connectplugin.core.utils.value.FileConfiguration;

import java.io.File;

public class Configuration {

    public final FileConfiguration config;

    private ConfigValue<String> apiKey;

    public Configuration(ConnectPlugin plugin) {
        this.config = new FileConfiguration( plugin.getPluginFolder() + File.separator + "config.yaml");

        this.apiKey = new ConfigValue<>("apiKey", String.class, this.config, "YOUR_API_TOKEN");
    }

    public String getApiKey() {
        return this.apiKey.get();
    }

    public void setApiKey(String key) {
        this.apiKey.set(key);
    }

}
