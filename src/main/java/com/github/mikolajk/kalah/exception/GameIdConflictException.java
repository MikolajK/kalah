package com.github.mikolajk.kalah.exception;

public class GameIdConflictException extends RuntimeException {
    public GameIdConflictException(String cause) {
        super(cause);
    }
}
