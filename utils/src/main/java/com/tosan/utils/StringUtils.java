package com.tosan.utils;

public class StringUtils {
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String capitalize(String value) {
        if(value == null)
            return null;

        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
