package com.cc.core.actions.shell.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.utils.FileUtil;

public class ClearCacheAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        FileUtil.clearCache();
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "ClearCache";
    }
}
