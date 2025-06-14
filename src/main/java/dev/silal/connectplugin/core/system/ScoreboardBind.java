package dev.silal.connectplugin.core.system;

import dev.silal.connectplugin.ConnectPlugin;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Map;

public class ScoreboardBind {

    private ConnectPlugin plugin;
    public ScoreboardBind(ConnectPlugin plugin) {
        this.plugin = plugin;
    }

    /**
    * Sync all scoreboard objectives wich are configured
    * */
    public void syncData() {
        for (Map.Entry<String, String> entry : plugin.getConfiguration().getBoundScoreboards().entrySet()) {
            String objective = entry.getKey();
            String key = entry.getValue();

            syncData(objective, key);
        }
    }

    /**
    * Sync the data of one configured scoreboard objective
    *
    * @param objective The objective to sync
    * */
    public void syncData(String objective) {
        String key = ConnectPlugin.getInstance().getConfiguration().getBoundScoreboardKey(objective);
        syncData(objective, key);
    }

    /**
    * Sync a objective to a data key
    *
    * @param objective The objective to sync
    * @param key The data key to sync it with
    * */
    public void syncData(String objective, String key) {
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();

        Objective obj = scoreboard.getObjective(objective);
        if (obj == null) return;

        for (String se : scoreboard.getEntries()) {
            Score score = obj.getScore(se);
            if (!score.isScoreSet()) continue;

            OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(se);
            if (offlinePlayer.getName() == null) continue;

            try {
                ConnectPlugin.getInstance().getDataStorage().getPlayer(offlinePlayer).setValue(key, String.valueOf(score.getScore()));
            } catch (Exception e) {}
        }
    }

}
