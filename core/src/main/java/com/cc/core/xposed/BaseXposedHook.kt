package com.cc.core.xposed

import com.cc.core.log.KLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

abstract class BaseXposedHook : XposedHook {
    fun hookMethod(className: String, classLoader: ClassLoader, methodName: String, vararg params: Any) {
        XposedHelpers.findAndHookMethod(className, classLoader, methodName, *params)
    }

    fun findClass(className: String, classLoader: ClassLoader): Class<*> {
        return XposedHelpers.findClass(className, classLoader)
    }

    fun hookConstructor(className: String, classLoader: ClassLoader, vararg params: Any?) {
        KLog.e("-----PPPP>>>>>>" + (if(params == null) "null" else params.size) + "  " + (params[params.size -1] is XC_MethodHook))

        XposedHelpers.findAndHookConstructor(className, classLoader, params)
    }
}