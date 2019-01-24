package com.cc.wechatmanager.xposed;

import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HookClockIn extends BaseXposedHook {
    @Override
    public void hook(final ClassLoader classLoader) {
        hookMethod("com.cmcc.attendancesystem.volley.JSONPostRequest",  classLoader,"startLoadWithJsonObject", Context.class, String.class, String.class, Class.class, String.class, new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedBridge.log("XposedCaller Start to hook login!");
                if (((String)param.args[2]).startsWith("https://kq.migu.cn:10443/miguattend/user/checkAccount.html")) {
                    hookLoginResponse(classLoader);
                }
            }
        });
    }

    private void hookLoginResponse(ClassLoader classLoader) {
        hookMethod("com.cmcc.attendancesystem.volley.JSONPostRequest$OnLoadCompleteListener", classLoader, "onLoadSuccess", findClass("com.cmcc.attendancesystem.bean.CheckAccountResponse", classLoader), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
        });
    }
}
