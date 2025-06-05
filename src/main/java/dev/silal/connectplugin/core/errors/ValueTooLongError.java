package dev.silal.connectplugin.core.errors;

public class ValueTooLongError extends Exception {

    public ValueTooLongError() {
        super("The value is too long for our database. The maximum length is 100 characters!");
    }

}
