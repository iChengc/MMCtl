package com.cc.wechatmanager;

import com.cc.core.MyApplication;
import com.cc.core.log.KLog;

public class MMApplication extends MyApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        KLog.setLog2ConsoleEnabled(true);
    }
}
