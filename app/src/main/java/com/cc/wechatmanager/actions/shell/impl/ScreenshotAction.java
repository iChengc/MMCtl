package com.cc.wechatmanager.actions.shell.impl;


import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.shell.ShellCommands;
import com.cc.wechatmanager.shell.ShellUtils;
import com.cc.wechatmanager.utils.FileUtil;

public class ScreenshotAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        try {
            String savePath = FileUtil.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
            ShellUtils.runShell(true, ShellCommands.genCmd(ShellCommands.SCREENSHOT_CMD, savePath));
            ActionResult result = ActionResult.successResult();
            result.data = savePath;
            return result;
        } catch (Exception e) {
            return ActionResult.failedResult(e);
        }
    }

    @Override
    public String key() {
        return "Screenshot";
    }
}
