package com.cc.wechatmanager;

import android.app.Activity;
import android.app.Application;

import com.cc.wechatmanager.data.db.DaoMaster;
import com.cc.wechatmanager.data.db.DaoSession;

import org.greenrobot.greendao.database.Database;

public class MyApplication extends Application {
    public static final String PACKAGE_NAME = "com.cc.wechatmanager";
    public static Application application;
    public static Activity forgroundActivity;
    private static DaoSession session;
    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
        setupDatabase();
    }

    public static void init(Application application) {
        MyApplication.application = application;
        WorkerHandler.getInstance().init();
    }

    private void setupDatabase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "wechat");
        Database db = helper.getWritableDb();
        DaoMaster master = new DaoMaster(db);
        session = master.newSession();
    }
}
