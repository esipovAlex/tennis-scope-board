package ru.tennis.exceptions;

public class PlayerNameNotFoundException extends RuntimeException {

    public PlayerNameNotFoundException() {
    }

    public PlayerNameNotFoundException(String message) {
        super(message);
    }
}
