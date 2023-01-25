package com.tosan.utils;

import java.lang.reflect.Field;
import java.util.*;

public final class EnumUtils {
    public static Map<Integer, String> GetEnumNames(Class<?> clazz) {
        var enumElementsMap = new HashMap<Integer, String>();
        try {
            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                System.out.println(field.getName());
                enumElementsMap.put(
                        ((Enum<?>) field.get(null)).ordinal(),
                        field.getName()
                );
            }
            return enumElementsMap;
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }
}
