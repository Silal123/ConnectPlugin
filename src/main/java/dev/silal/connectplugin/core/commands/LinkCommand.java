package dev.silal.connectplugin.core.commands;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import dev.silal.connectplugin.core.utils.Prefix;
import dev.silal.connectplugin.core.utils.Request;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TraderLlama;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (args.length < 1) {
            p.sendMessage(Prefix.SYSTEM.key() + "Please go to §a" + ConnectPlugin.FRE_BASE + "/connect§7 to link");
            return true;
        }
        String token = args[0];

        Bukkit.getScheduler().runTaskAsynchronously(ConnectPlugin.getInstance(), () -> {
            try {
                Request req = new Request("http://localhost:8080/connect/confirm/" + token).body(new JsonManager().addProperty("uuid", p.getUniqueId().toString())).method(Request.Method.POST).send();
                JsonManager json = req.getResponseJson();

                if (req.getResponseCode() == 200 && json.getInt("status") == 0) {
                    p.sendMessage(Prefix.SYSTEM.key() + "You are now linked to §a" + json.getNumber("id"));
                    return;
                }

                if (req.getResponseCode() == 404) {
                    p.sendMessage(Prefix.SYSTEM.key() + "Invalid link §etoken§7!");
                    return;
                }

                p.sendMessage(Prefix.SYSTEM.key() + "Unexpected §cerror§7!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
