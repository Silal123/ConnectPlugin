package dev.silal.connectplugin.core.errors;

public class UnknownUserError extends Exception {

    public UnknownUserError() {
        super("A user with the given uuid was not found. That means the user is not linked or the uuid is wrong!");
    }

}
