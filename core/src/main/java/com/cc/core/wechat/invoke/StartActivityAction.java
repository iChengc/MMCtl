package com.cc.core.wechat.invoke;

import android.content.Intent;
import android.os.Bundle;

import com.cc.core.MyApplication;
import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;

public class StartActivityAction implements Action {
    @Override
    public ActionResult execute(Object... args) {
        if (args == null || args.length <= 0) {
            return ActionResult.failedResult("unknown activity");
        }

        if (MyApplication.forgroundActivity == null) {
            return ActionResult.failedResult("Wechat does not start");
        }

        Intent intent = new Intent();
        intent.setClassName(MyApplication.forgroundActivity, args[0].toString());
        if (args.length > 1) {
            Bundle bundle = new Bundle();
            for (int i = 1; i < args.length; ++i) {
                //intent.putExtra(args[i].toString(), args[++i]);
            }
            intent.putExtras(bundle);
        }
        MyApplication.forgroundActivity.startActivity(intent);
        return ActionResult.successResult();
    }

    @Override
    public String key() {
        return "wechat:startActivity";
    }
}
