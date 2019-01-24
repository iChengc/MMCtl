package com.cc.core.wechat.invoke;

import android.content.Intent;

import com.cc.core.MyApplication;
import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.wechat.Wechat;

public class OpenUrlAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        Intent i = new Intent();
        i.setClassName(MyApplication.application(), Wechat.Resources.WEBVIEW_ACTIVITY_CLASS_NAME);
        i.putExtra("rawUrl", args[0].toString());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.application().startActivity(i);
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "wechat:open_url";
    }
}
