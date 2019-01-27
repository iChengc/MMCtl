package com.cc.core.wechat.invoke;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;

public class GetUserInfoAction implements Action {

    @Override
    public ActionResult execute(Object... args) {
        return null;
    }

    @Override
    public String key() {
        return null;
    }
}
