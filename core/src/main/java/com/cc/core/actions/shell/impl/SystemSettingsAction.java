package com.cc.core.actions.shell.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.shell.ShellUtils;
import com.cc.core.shell.core.CommandResult;
import com.cc.core.shell.core.Shell;

public class SystemSettingsAction implements Action {
    public static class SettingsNameSpace {
        public static final String SYSTEM = "system";
        public static final String SECURE = "secure";
        public static final String GLOBAL = "global";
    }

    @Override
    public ActionResult execute(Object... args) {
        if (args.length != 3) {
            return ActionResult.Companion.failedResult("The length of system settings args must be 3");
        }

        String cmd = String.format("settings put %s %s \"%s\"", args[0], args[1], args[2]);
        CommandResult result = ShellUtils.runShell(true, cmd);
        return result.isSuccessful() ? ActionResult.Companion.successResult() : ActionResult.Companion.failedResult(result.getStderr());
    }

    @Override
    public String key() {
        return "system_settings";
    }
}
