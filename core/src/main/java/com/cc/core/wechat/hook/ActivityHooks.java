package com.cc.core.wechat.hook;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.cc.core.MyApplication;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;

public class ActivityHooks extends BaseXposedHook {

    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(Activity.class.getName(), classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                MyApplication.forgroundActivity = activity;
            }
        });
    }
}