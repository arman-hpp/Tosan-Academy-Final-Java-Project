package com.tosan.model;

public enum Currencies {
    rial("Rial"),
    dollar("Dollar");

    private final String displayValue;

    Currencies(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}

