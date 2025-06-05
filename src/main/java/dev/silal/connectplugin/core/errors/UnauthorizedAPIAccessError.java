package dev.silal.connectplugin.core.errors;

public class UnauthorizedAPIAccessError extends Exception {

    public UnauthorizedAPIAccessError() {
        super("You are unauthorized to access our api. Please try using a other token in your config.yaml");
    }

}
