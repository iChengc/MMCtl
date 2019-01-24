package com.cc.core;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Intent;

import com.cc.core.actions.Actions;
import com.cc.core.actions.accessibility.WechatAccessibilityService;
import com.cc.core.actions.shell.impl.EnableAccessibilityAction;
import com.cc.core.data.db.DaoMaster;
import com.cc.core.data.db.DaoSession;
import com.cc.core.log.KLog;

import org.greenrobot.greendao.database.Database;

public class MyApplication extends Application {
    public static final String PACKAGE_NAME = "com.cc.wechatmanager";
    private static Application application;
    public static Activity forgroundActivity;
    private static DaoSession session;
    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
        setupDatabase();
        KLog.e("Enable accessibility result:" + Actions.execute(EnableAccessibilityAction.class));

    }

    public static void init(Application application) {
        KLog.e(">>>> Application init:" + (application == null ? "null" : application.getClass().getName()));
        MyApplication.application = application;
        WorkerHandler.getInstance().init();
    }

    public static Application application() {
        if (application == null) {
            application = AndroidAppHelper.currentApplication();
        }

        return application;
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wechat");
        Database db = helper.getWritableDb();
        DaoMaster master = new DaoMaster(db);
        session = master.newSession();
    }
}
