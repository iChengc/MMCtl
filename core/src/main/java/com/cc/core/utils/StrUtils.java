package com.cc.core.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Collection;

public class StrUtils {
    private static final Gson gson;
    private static final Gson numberGson;
    static {
        gson = new Gson();
        numberGson = new GsonBuilder().registerTypeAdapter(Double.class, new DoubleSerializer()).setPrettyPrinting().create();
    }

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

    public static <T> T fromNumberJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromNumberJson(String json, Type clazz) {
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        return gson.toJson(obj);
    }

    public static String toNumberJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        return numberGson.toJson(obj);
    }

    public static <T> String join(Iterable<T> iterable, String delemiter, StringGetter<T> getter) {
        if (iterable == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (T object : iterable) {
            if (counter > 0) {
                sb.append(delemiter);
            }

            if (getter != null) {
                sb.append(getter.getString(object));
            } else {
                sb.append(object);
            }

            counter++;
        }

        return sb.toString();
    }

    public interface StringGetter<T> {
        String getString(T data);
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

    public static boolean isGroupWechatId(String wechatId) {
        if (TextUtils.isEmpty(wechatId)) {
            return false;
        }

        return wechatId.endsWith("@chatroom");
    }

    private static class DoubleSerializer implements JsonSerializer<Double> {
        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return src == src.longValue() ? new JsonPrimitive(src.longValue()) : new JsonPrimitive(src);
        }
    }
}
