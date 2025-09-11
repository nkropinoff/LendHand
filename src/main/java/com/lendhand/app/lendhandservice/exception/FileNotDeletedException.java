package com.lendhand.app.lendhandservice.exception;

public class FileNotDeletedException extends RuntimeException {
    public FileNotDeletedException(String message) {
        super(message);
    }
    public FileNotDeletedException(String message, Throwable cause) {
      super(message, cause);
    }
}
