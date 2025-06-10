package dev.silal.connectplugin.core.connection;

import dev.silal.connectplugin.ConnectPlugin;
import dev.silal.connectplugin.core.utils.JsonManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class ConnectWebsocket implements WebSocket.Listener {

    /**
    * Tells if the server is connected or not
    * */
    public boolean open = false;

    /**
    * The connect plugin in wich the websocket is used
    * */
    private ConnectPlugin plugin;

    /**
    * The websocket itself
    * */
    private WebSocket socket;

    public void setSocket(WebSocket socket) { this.socket = socket; }
    public WebSocket getSocket() { return this.socket; }

    public ConnectWebsocket(ConnectPlugin plugin) {
        this.plugin = plugin;
    }


    /**
    * Called on Connection open
    *
    * @param webSocket The websocket wich opened
    * */
    @Override
    public void onOpen(WebSocket webSocket) {
        open = true;
        plugin.getLogger().info("Websocket connection opened");
        webSocket.sendText(new JsonManager().addProperty("token", plugin.getConfiguration().getApiKey()).toJsonString(), true);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    /**
    * Called on Message receive
    *
    * @param webSocket The websocket wich the message was sent in
    * @param data The message data
    * @param last If it is the last message
    * */
    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        onMessage(data.toString());
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    /**
    * Called by onText is a submethod wich handles a message as a string
    *
    * @param message The message as a string
    * */
    public void onMessage(String message) {
        System.out.println(message);
        if (!JsonManager.isValidJson(message)) return;
        JsonManager json = new JsonManager(message);

        if (!json.hasKey("op")) return;
        int op = json.getInt("op");

        if (op == 1) {
            if (!json.hasKey("d") || !json.hasKey("t")) return;
            JsonManager data = json.get("d");
            String event = json.getString("t");

            if (event.equals("MESSAGE_SEND")) {
                if (!data.hasKey("account") || !data.hasKey("message")) return;
                JsonManager account = data.get("account");
                if (!account.hasKey("name")) return;
                String name = account.getString("name");
                String msg = data.getString("message");

                for (Player p : this.plugin.getServer().getOnlinePlayers()) {
                    p.sendMessage(name + "(Discord) > " + msg);
                }
            }
        }
    }

    /**
    * Called when the connection to the Server is closed
    *
    * @param webSocket The websocket wich is disconnected
    * @param statusCode The exit code
    * @param reason The reason of closure
    * */
    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        open = false;
        this.plugin.getLogger().warning("Websocket closed");
        reconnect();

        if (statusCode == 2) {
            this.plugin.getLogger().warning("Invalid token provided!");
        }

        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    /**
    * Called if a error occured in the connection
    *
    * @param webSocket The websocked in wich the error occured
    * @param ex The exception wich happened
    * */
    @Override
    public void onError(WebSocket webSocket, Throwable ex) {
        plugin.getLogger().warning("Websocket disconnect");
        ex.printStackTrace();
        reconnect();
    }

    /**
    * Used to send a message to the websocket server
    *
    * @param message The message to send
    * */
    public void sendMessage(String message) {
        if (socket != null && open) {
            System.out.println(message);
            socket.sendText(message, true);
        }
    }

    /**
    * Used to send json data to the websocket server
    *
    * @param manager The json manager to send
    * */
    public void sendJson(JsonManager manager) {
        sendMessage(manager.toJsonString());
    }

    /**
    * Used to send an event to the websocket server
    *
    * @param event Name of the event
    * @param data The data of the event
    * */
    public void sendEvent(String event, JsonManager data) {
        sendJson(buildEvent(event, data));
    }

    /**
    * Builder for a event
    *
    * @param event The name of the event
    * @param data The data of the event
    * @return The JsonManager with the event data
    * */
    public JsonManager buildEvent(String event, JsonManager data) {
        return new JsonManager().addProperty("op", 1).addProperty("d", data).addProperty("t", event);
    }

    /**
    * Used to reconnect the websocket
    * */
    public void reconnect() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, ConnectWebsocket::connectSocket, 20L * 10);
    }

    /**
    * Connect to the socket server
    * */
    public static void connectSocket() {
        Bukkit.getScheduler().runTaskAsynchronously(ConnectPlugin.getInstance(), () -> {
            try {
                ConnectPlugin.getInstance().getLogger().info("Creating websocket connection");
                URI uri = URI.create(ConnectPlugin.API_WEBSOCKET);

                ConnectWebsocket listener = new ConnectWebsocket(ConnectPlugin.getInstance());

                HttpClient client = HttpClient.newHttpClient();

                WebSocket socket = client.newWebSocketBuilder()
                        .buildAsync(uri, listener)
                        .join();

                listener.setSocket(socket);
                ConnectPlugin.getInstance().setWebsocket(listener);
            } catch (Exception e) {
                ConnectPlugin.getInstance().getLogger().warning("Failed to reconnect!");

                try { Thread.sleep(10_000); } catch (Exception ex) {}
                ConnectPlugin.getInstance().getLogger().info("Reconnecting...");
                connectSocket();
            }
        });
    }

}
