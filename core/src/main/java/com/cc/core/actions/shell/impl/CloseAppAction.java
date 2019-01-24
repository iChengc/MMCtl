package com.cc.core.actions.shell.impl;


import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.shell.ShellCommands;
import com.cc.core.shell.ShellUtils;

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
