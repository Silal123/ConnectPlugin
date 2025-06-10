package dev.silal.connectplugin.core;

import dev.silal.connectplugin.ConnectPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {

    private final static int ONE_TICK = 1;
    private final static int ONE_SECOND = 20 * ONE_TICK;
    private final static int ONE_MINUTE = 60 * ONE_SECOND;

    private final static List<Integer> schedulers = new ArrayList<>();

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
    }

}
