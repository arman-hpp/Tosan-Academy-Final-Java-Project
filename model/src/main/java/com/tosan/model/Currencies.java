package com.tosan.model;

public enum Currencies {
    Rial("R"),
    Dollar("$");

    private final String symbol;

    Currencies(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

