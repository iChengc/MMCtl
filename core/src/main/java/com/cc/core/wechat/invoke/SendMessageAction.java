package com.cc.core.wechat.invoke;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;

public class SendMessageAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        return null;
    }

    @Override
    public String key() {
        return "wechat:sendMessage";
    }
}
