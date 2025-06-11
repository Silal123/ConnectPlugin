package dev.silal.connectplugin.core;

import dev.silal.connectplugin.ConnectPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    public final static int ONE_TICK = 1;
    public final static int ONE_SECOND = 20 * ONE_TICK;
    public final static int ONE_MINUTE = 60 * ONE_SECOND;

    private final static List<Integer> schedulers = new ArrayList<>();
    private static boolean isFirstRun = true;

    public static void stop() {
        for (int task : schedulers) {
            Bukkit.getScheduler().cancelTask(task);
        }
    }

    public static void start() {
        /*
        * ONE_MINUTE scheduler
        * */
        schedulers.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(ConnectPlugin.getInstance(), () -> {
            ConnectPlugin.getInstance().getScoreboardBind().syncData();
        }, 0, ONE_MINUTE));

        /*
        * 10 * ONE_MINUTE = 10 Minute scheduler
        * */
        schedulers.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(ConnectPlugin.getInstance(), () -> {
            if (!isFirstRun) Bukkit.getScheduler().runTaskAsynchronously(ConnectPlugin.getInstance(), () -> { ConnectPlugin.getInstance().getWebsocket().sendEvent("DATA_UPDATE", ConnectPlugin.getInstance().getServerData()); });
        }, 0, ONE_MINUTE * 10));

        Bukkit.getScheduler().runTaskLaterAsynchronously(ConnectPlugin.getInstance(), () -> { isFirstRun = false; }, ONE_SECOND);
    }

}
