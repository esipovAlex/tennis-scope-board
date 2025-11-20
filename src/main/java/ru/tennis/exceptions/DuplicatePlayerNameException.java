package ru.tennis.exceptions;

public class DuplicatePlayerNameException extends RuntimeException {

    public DuplicatePlayerNameException(String message) {
        super(message);
    }
}
