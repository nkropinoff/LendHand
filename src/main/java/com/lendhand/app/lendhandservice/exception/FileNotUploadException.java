package com.lendhand.app.lendhandservice.exception;

public class FileNotUploadException extends RuntimeException {
    public FileNotUploadException(String message) {
        super(message);
    }

    public FileNotUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
