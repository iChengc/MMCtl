package com.cc.core.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collection;

public class StrUtils {
    private static final Gson gson = new Gson();

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type clazz) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String join(Iterable iterable, String delemiter) {
        if (iterable == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (Object object : iterable) {
            if (counter > 0) {
                sb.append(delemiter);
            }

            sb.append(object);

            counter++;
        }

        return sb.toString();
    }

    public static CharSequence stringNotNull(CharSequence s) {
        if (s == null) {
            return "";
        }

        return s;
    }
}
