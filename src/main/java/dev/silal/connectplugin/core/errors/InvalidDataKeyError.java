package dev.silal.connectplugin.core.errors;

public class InvalidDataKeyError extends Exception {

    public InvalidDataKeyError() {
        super("One or more of the given keys are not valid! Please add the keys you want to use via the key setup menu!");
    }

}
