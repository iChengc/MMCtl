package com.cc.core.command.impl;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.actions.Actions;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.invoke.OpenUrlAction;

public class OpenUrlCommandAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        ActionResult result = Actions.execute(OpenUrlAction.class, args);
        if (!result.isSuccess()) {
            return result;
        }
        if (args.length > 1) {
            Utils.sleep((Long) args[1]);
        }
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "openUrl";
    }
}
