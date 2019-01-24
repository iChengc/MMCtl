package com.cc.wechatmanager.xposed;

import android.app.AndroidAppHelper;
import android.util.Log;

import com.cc.wechatmanager.MyApplication;
import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.wechat.HookNames;
import com.cc.wechatmanager.wechat.Wechat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage {
    private static final String TAG = "XposedMain";
    private static List<BaseXposedHook> hooks = new ArrayList<>();
    static {
        loadHooks();
    }

    private static void loadHooks() {
        try {
            for (String name : HookNames.HOOK_NAMES) {
                addHook(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addHook(String className) throws Exception {
        Class hook = Class.forName(className);
        hooks.add((BaseXposedHook) hook.newInstance());
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.e("Xposed", "Xposed handle load package:" + lpparam.packageName + "    AppInfo:" + lpparam.appInfo + "   processName:" + lpparam.processName);
        if(lpparam.packageName.equals(Wechat.WECHAT_PACKAGE_NAME)){
            XposedBridge.log(TAG + ">>process: "+lpparam.processName);
            if(lpparam.processName.equals(Wechat.WECHAT_PACKAGE_NAME)){
                XposedBridge.log(TAG+">>开始hook微信主进程");
                for (BaseXposedHook h : hooks) {
                    h.hook(lpparam.classLoader);
                }
                MyApplication.init(AndroidAppHelper.currentApplication());
            }

        }

    }
}
