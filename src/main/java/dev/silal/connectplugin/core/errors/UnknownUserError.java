package dev.silal.connectplugin.core.errors;

public class UnknownUserError extends Exception {

    public UnknownUserError() {
        super("A user with the given information was not found. That means the user is not linked!");
    }

}
