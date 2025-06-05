package dev.silal.connectplugin.core.utils;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Request {

    public enum Method {
        GET, POST, PUT
    }

    private final String url;
    private Method method;
    private JsonManager body;
    private int responseCode;
    private JsonManager responseJson;
    private String responseRaw;
    private final Map<String, String> headers = new HashMap<>();

    public Request(String url) {
        this.url = url;
        this.method = Method.GET;

        header("Content-Type", "application/json");
        header("Accept", "application/json");
    }

    public Request method(Method method) {
        this.method = method;
        return this;
    }

    public Request body(JsonManager body) {
        this.body = body;
        return this;
    }

    public Request header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Request send() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method.name());

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (method == Method.POST || method == Method.PUT) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.toJsonString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        responseCode = connection.getResponseCode();

        InputStream is = (responseCode >= 200 && responseCode < 400) ?
                connection.getInputStream() : connection.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseBuilder.append(line);
        }
        in.close();
        responseRaw = responseBuilder.toString();

        if (JsonManager.isValidJson(responseRaw)) {
            responseJson = new JsonManager(responseRaw);
        }

        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseRaw() {
        return responseRaw;
    }

    public JsonManager getResponseJson() {
        return responseJson;
    }
}
