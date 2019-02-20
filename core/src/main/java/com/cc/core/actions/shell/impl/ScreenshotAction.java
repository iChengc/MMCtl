package com.cc.core.actions.shell.impl;


import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.shell.ShellCommands;
import com.cc.core.shell.ShellUtils;
import com.cc.core.utils.FileUtil;

public class ScreenshotAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        try {
            String savePath = FileUtil.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
            ShellUtils.runShell(true, ShellCommands.genCmd(ShellCommands.SCREENSHOT_CMD, savePath));
            ActionResult result = ActionResult.Companion.successResult();
            result.setData(savePath);
            return result;
        } catch (Exception e) {
            return ActionResult.Companion.failedResult(e);
        }
    }

    @Override
    public String key() {
        return "Screenshot";
    }
}
