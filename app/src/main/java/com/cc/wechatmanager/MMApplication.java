package com.cc.wechatmanager;

import com.cc.core.ApplicationContext;
import com.kcrason.highperformancefriendscircle.FriendsCircleApplication;

public class MMApplication extends FriendsCircleApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.setup(this);
    }
}
