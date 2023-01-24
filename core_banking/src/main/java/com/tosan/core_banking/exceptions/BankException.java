package com.tosan.core_banking.exceptions;

public final class BankException extends RuntimeException {
    public BankException(String error) {
        super(String.format("Error: %s", error));
    }

    public String getEncodedMessage() {
        return this.getMessage().replace(' ', '+');
    }
}
