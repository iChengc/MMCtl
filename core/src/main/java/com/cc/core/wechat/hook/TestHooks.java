package com.cc.core.wechat.hook;

import com.cc.core.xposed.BaseXposedHook;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class TestHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {
        /*hookMethod("com.tencent.mm.chatroom.c.g", classLoader, "g", String.class, List.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                new Exception().printStackTrace();

            }
        });*/
    }
}
