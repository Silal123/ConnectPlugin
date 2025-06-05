package dev.silal.connectplugin.core.utils;

public enum Permission {

    SET_DATA("connect.data.set"),
    GET_DATA("connect.data.get");

    Permission(String key) {
        this.key = key;
    }
    private String key;

    public String key() { return key; }

}
