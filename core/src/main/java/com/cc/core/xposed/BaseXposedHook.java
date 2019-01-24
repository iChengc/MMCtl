package com.cc.core.xposed;

import de.robv.android.xposed.XposedHelpers;

public abstract class BaseXposedHook implements XposedHook {
    protected void hookMethod(String className, ClassLoader classLoader, String methodName, Object... params) {
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, params);
    }

    protected Class<?> findClass(String className, ClassLoader classLoader) {
        return XposedHelpers.findClass(className, classLoader);
    }
}
