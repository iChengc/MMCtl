package com.cc.core.xposed

interface XposedHook {
    fun hook(classLoader: ClassLoader)
}