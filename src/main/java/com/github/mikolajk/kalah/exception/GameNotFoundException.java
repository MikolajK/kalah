package com.github.mikolajk.kalah.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String cause) {
        super(cause);
    }
}
