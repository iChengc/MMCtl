package com.cc.wechatmanager.actions.shell.impl;

import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.utils.FileUtil;

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
