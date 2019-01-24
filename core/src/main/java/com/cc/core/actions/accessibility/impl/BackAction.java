package com.cc.core.actions.accessibility.impl;

import com.cc.core.actions.ActionResult;
import com.cc.core.actions.accessibility.AccessibilityAction;

public class BackAction extends AccessibilityAction {
    @Override
    public ActionResult execute(Object... args) {
        boolean result = systemBack();
        return result ? ActionResult.successResult() : ActionResult.failedResult("can not execute back");
    }

    @Override
    public String key() {
        return "back";
    }
}
