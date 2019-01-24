package com.cc.wechatmanager.xposed;

import de.robv.android.xposed.XposedHelpers;

public abstract class BaseXposedHook implements XposedHook {
    protected void hookMethod(String className, ClassLoader classLoader, String methodName, Object... params) {
        XposedHelpers.findAndHookMethod(className, classLoader, className, params);
    }

    protected Class<?> findClass(String className, ClassLoader classLoader) {
        return XposedHelpers.findClass(className, classLoader);
    }
}
