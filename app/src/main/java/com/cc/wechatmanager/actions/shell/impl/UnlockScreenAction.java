package com.cc.wechatmanager.actions.shell.impl;

import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.utils.DeviceUtils;

public class UnlockScreenAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        DeviceUtils.unlockScreen();
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "UnlockScreen";
    }
}
