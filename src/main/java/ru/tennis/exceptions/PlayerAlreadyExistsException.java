package ru.tennis.exceptions;

public class PlayerAlreadyExistsException extends RuntimeException{

    public PlayerAlreadyExistsException(String message) {
        super(message);
    }
}
