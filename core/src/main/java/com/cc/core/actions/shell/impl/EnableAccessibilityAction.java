package com.cc.core.actions.shell.impl;

import android.provider.Settings;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.actions.Actions;
import com.cc.core.actions.accessibility.WechatAccessibilityService;

public class EnableAccessibilityAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        ActionResult result = Actions.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        if (!result.isSuccess()) {
            return result;
        }

        result = Actions.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, WechatAccessibilityService.ACCESSIBILITY_SERVICE_NAME);
        if (!result.isSuccess()) {
            return result;
        }

        result = Actions.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ACCESSIBILITY_ENABLED, 1);

        return result;
    }

    @Override
    public String key() {
        return "enable_accessibility";
    }
}
