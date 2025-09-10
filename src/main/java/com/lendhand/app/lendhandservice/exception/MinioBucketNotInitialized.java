package com.lendhand.app.lendhandservice.exception;

public class MinioBucketNotInitialized extends RuntimeException {
    public MinioBucketNotInitialized(String message) {
        super(message);
    }

    public MinioBucketNotInitialized(String message, Throwable cause) {
      super(message, cause);
    };
}
