package com.cc.core.xposed

import de.robv.android.xposed.XposedHelpers

abstract class BaseXposedHook : XposedHook {
    fun hookMethod(className: String, classLoader: ClassLoader, methodName: String, vararg params: Any) {
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, *params)
    }

    fun findClass(className: String, classLoader: ClassLoader): Class<*> {
        return XposedHelpers.findClass(className, classLoader)
    }
}