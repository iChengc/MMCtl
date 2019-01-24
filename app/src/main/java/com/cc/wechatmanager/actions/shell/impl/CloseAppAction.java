package com.cc.wechatmanager.actions.shell.impl;


import com.cc.wechatmanager.actions.Action;
import com.cc.wechatmanager.actions.ActionResult;
import com.cc.wechatmanager.shell.ShellCommands;
import com.cc.wechatmanager.shell.ShellUtils;

public class CloseAppAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        try {
            ShellUtils.runShell(true, ShellCommands.genCmd(ShellCommands.STOP_APP_CMD, args));
            return ActionResult.successResult();
        } catch (Exception e) {
            return ActionResult.failedResult(e);
        }
    }

    @Override
    public String key() {
        return "CloseApp";
    }
}
