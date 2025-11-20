package ru.tennis.exceptions;

public class PlayerNameNotFoundException extends RuntimeException{

    public PlayerNameNotFoundException(String message) {
        super(message);
    }
}
