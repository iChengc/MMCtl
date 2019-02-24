package com.cc.core;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;

import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;
import com.cc.core.wechat.Wechat;


public class ApplicationContext {
    public static String PACKAGE_NAME = "com.cc.wechatmanager";
    private static Application application;
    public static Activity forgroundActivity;

    public static void setup(Application application) {
        init(application);
        //enableAccessibility();]

        // clear cache
        WorkerHandler.postOnWorkThread(new Runnable() {
            @Override public void run() {
                FileUtil.clearCache();
            }
        });
    }

    public static void init(Application application) {
        KLog.e(">>>> Application init:" + (application == null ? "null" : application.getClass().getName()));
        ApplicationContext.application = application;
        WorkerHandler.getInstance().init();
        Wechat.initEnvironment(Wechat.WECHAT_PACKAGE_NAME);
        KLog.enableLog2Console(KLog.POLICY_MASK);
        if (application != null) {
            PACKAGE_NAME = application.getPackageName();
        }
    }

    public static Application application() {
        if (application == null) {
            application = AndroidAppHelper.currentApplication();
        }

        return application;
    }
}
