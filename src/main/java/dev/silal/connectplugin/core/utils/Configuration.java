package dev.silal.connectplugin.core.utils;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.value.ConfigValue;
import dev.silal.connectplugin.core.utils.value.FileConfiguration;
import dev.silal.connectplugin.core.utils.value.MapConfigValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    public final FileConfiguration config;

    private ConfigValue<String> apiKey;

    private MapConfigValue<String> boundScoreboards;

    public Configuration(ConnectPlugin plugin) {
        this.config = new FileConfiguration( plugin.getPluginFolder() + File.separator + "config.yaml");

        this.apiKey = new ConfigValue<>("apiKey", String.class, this.config, "YOUR_API_TOKEN");
        this.boundScoreboards = new MapConfigValue<>("boundScoreboards", this.config, new HashMap<>());
    }

    /**
    * Get the api key wich the plugin uses
    *
    * @return Api key
    * */
   public String getApiKey() {
        return this.apiKey.get();
   }

   /**
   * Set the api key wich the plugin uses
   *
   * @param key Api key
   * */
   public void setApiKey(String key) {
       this.apiKey.set(key);
   }

   /**
   * Get all the scoreboards bound to a data key
   *
   * @return Map of bound scoreboards
   * */
   public Map<String, String> getBoundScoreboards() {
       return this.boundScoreboards.get();
   }

   /**
   * Set all bound scoreboards
   *
   * @param scoreboards The new scoreboards wich are bound
   * */
   public void setBoundScoreboards(Map<String, String> scoreboards) {
       this.boundScoreboards.set(scoreboards);
   }

   /**
   * Add a bound scoreboard
   *
   * @param scoreboard The scoreboard to bind
   * @param key The key to bind it to
   * */
   public void addBoundScoreboard(String scoreboard, String key) {
       Map<String, String> s = getBoundScoreboards();
       s.put(scoreboard, key);
       setBoundScoreboards(s);
   }

   /**
   * Remove a bound scoreboard
   *
   * @param scoreboard The scoreboard to remove
   * */
    public void removeBoundScoreboard(String scoreboard) {
        Map<String, String> s = getBoundScoreboards();
        s.remove(scoreboard);
        setBoundScoreboards(s);
    }

    /**
    * Get the data key a scoreboard is bound to
    *
    * @param scoreboard The scoreboard to get the key of
    * @return The data key to wich it is bound
    * */
    public String getBoundScoreboardKey(String scoreboard) {
       return getBoundScoreboards().get(scoreboard);
    }

}
