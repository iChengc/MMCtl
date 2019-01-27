package com.cc.core.wechat.hook;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.cc.core.MyApplication;
import com.cc.core.log.KLog;
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
                KLog.e("Start Activity:" + activity.getClass().getName() + "  Intent:"  + bundle2String(activity.getIntent().getExtras()));
            }
        });
        hookMethod(Activity.class.getName(), classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                KLog.e("Resume Activity:" + activity.getClass().getName());
            }
        });

        hookMethod(Activity.class.getName(), classLoader, "onDestroy", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                KLog.e("Destroy Activity:" + activity.getClass().getName());
            }
        });
    }

    private String bundle2String(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        for (String k : bundle.keySet()) {
            sb.append(k);
            sb.append(":");
            Object data = bundle.get(k);
            sb.append(data == null ? "null" : bundle.get(k).toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}