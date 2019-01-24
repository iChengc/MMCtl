package com.cc.core.utils;

import java.util.List;

public class Utils {
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
