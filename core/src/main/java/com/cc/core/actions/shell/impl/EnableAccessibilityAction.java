package com.cc.core.actions.shell.impl;

import android.content.ComponentName;
import android.provider.Settings;

import com.cc.core.ApplicationContext;
import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.actions.Actions;
import com.cc.core.actions.accessibility.WechatAccessibilityService;

public class EnableAccessibilityAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        ActionResult result = Actions.Companion.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ACCESSIBILITY_ENABLED, 0);
        if (!result.isSuccess()) {
            return result;
        }

        result = Actions.Companion.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, getAccessibilityKey());
        if (!result.isSuccess()) {
            return result;
        }

        result = Actions.Companion.execute(SystemSettingsAction.class, SystemSettingsAction.SettingsNameSpace.SECURE, Settings.Secure.ACCESSIBILITY_ENABLED, 1);

        return result;
    }

    private String getAccessibilityKey() {
        ComponentName cn = new ComponentName(ApplicationContext.application(), WechatAccessibilityService.class);
        return cn.flattenToString();
    }

    @Override
    public String key() {
        return "enableAccessibility";
    }
}
