package com.cc.core.actions.shell.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.utils.FileUtil;

public class ClearCacheAction implements Action {
    @Override
    public ActionResult execute(String actionId, Object... args) {
        FileUtil.clearCache();
        return ActionResult.Companion.successResult(actionId);
    }

    @Override
    public String key() {
        return "ClearCache";
    }
}
