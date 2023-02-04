package com.tosan.model;

public enum Currencies {
    rial("Rial", "R"),
    dollar("Dollar", "$");

    private final String displayValue;
    private final String symbol;

    Currencies(String displayValue, String symbol) {
        this.displayValue = displayValue;
        this.symbol = symbol;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getSymbol() {
        return symbol;
    }
}

