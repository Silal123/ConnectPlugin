package dev.silal.connectplugin.core.errors;

public class UnexpectedConnectionError extends Exception {

    public UnexpectedConnectionError() {
        super("An unexpected error occured while trying to connect to our api");
    }

}
