package com.cc.core.actions.shell.impl;


import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.shell.ShellCommands;
import com.cc.core.shell.ShellUtils;

public class CloseAppAction implements Action {
    @Override
    public ActionResult execute(String actionId, Object... args) {
        try {
            ShellUtils.runShell(true, ShellCommands.genCmd(ShellCommands.STOP_APP_CMD, args));
            return ActionResult.Companion.successResult(actionId);
        } catch (Exception e) {
            return ActionResult.Companion.failedResult(actionId, e);
        }
    }

    @Override
    public String key() {
        return "CloseApp";
    }
}
