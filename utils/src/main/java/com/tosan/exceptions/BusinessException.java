package com.tosan.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String error) {
        super(String.format("Error: %s", error));
    }

    public String getEncodedMessage() {
        return this.getMessage().replace(' ', '+');
    }
}