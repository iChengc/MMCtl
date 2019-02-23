package com.cc.core.xposed

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

abstract class BaseXposedHook : XposedHook {
    fun hookMethod(className: String, classLoader: ClassLoader, methodName: String, vararg params: Any) : XC_MethodHook.Unhook  {
        return XposedHelpers.findAndHookMethod(className, classLoader, methodName, *params)
    }

    fun findClass(className: String, classLoader: ClassLoader): Class<*> {
        return XposedHelpers.findClass(className, classLoader)
    }
}