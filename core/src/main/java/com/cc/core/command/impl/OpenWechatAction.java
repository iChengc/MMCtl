package com.cc.core.command.impl;

import com.cc.core.MyApplication;
import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.wechat.Wechat;

public class OpenWechatAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        Wechat.startApp(MyApplication.application());
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "openWechat";
    }
}
