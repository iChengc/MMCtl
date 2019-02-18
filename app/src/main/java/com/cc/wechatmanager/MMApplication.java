package com.cc.wechatmanager;

import android.app.Application;

import com.cc.core.ApplicationContext;
import com.cc.core.log.KLog;

public class MMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.setup(this);
    }
}
