package com.cc.core.actions.shell.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.utils.DeviceUtils;

public class LockScreenAction implements Action {
    @Override
    public ActionResult execute(String actiongId, Object... args) {
        try {
            DeviceUtils.lockScreen();
            return ActionResult.Companion.successResult(actiongId);
        } catch (Exception e) {
            e.printStackTrace();
            return ActionResult.Companion.failedResult(actiongId, e);
        }
    }

    @Override
    public String key() {
        return "LockScreen";
    }
}
