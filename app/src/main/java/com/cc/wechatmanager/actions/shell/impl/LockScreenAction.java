package com.cc.wechatmanager.actions.shell.impl;

import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.utils.DeviceUtils;

public class LockScreenAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        try {
            DeviceUtils.lockScreen();
            return ActionResult.successResult();
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.failedResult(e);
        }
    }

    @Override
    public String key() {
        return "LockScreen";
    }
}
