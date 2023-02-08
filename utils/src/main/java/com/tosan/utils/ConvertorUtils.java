package com.tosan.utils;

public final class ConvertorUtils {
    public static Long tryParseLong(String value, Long defaultVal) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static Integer tryParseInt(String value, Integer defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
