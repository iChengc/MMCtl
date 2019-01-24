package com.cc.core.xposed;

import android.util.Log;


import com.cc.core.wechat.Wechat;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedMain implements IXposedHookLoadPackage {
    private static final String TAG = "XposedMain";


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Log.e(TAG, "Xposed handle load package:" + lpparam.packageName + "    AppInfo:" + lpparam.appInfo + "   processName:" + lpparam.processName);
        if(lpparam.packageName.equals(Wechat.WECHAT_PACKAGE_NAME)){
            XposedBridge.log(TAG + ">>process: "+lpparam.processName);
            if(lpparam.processName.equals(Wechat.WECHAT_PACKAGE_NAME)){
                Wechat.start(lpparam.classLoader);
            }

        }

    }
}
