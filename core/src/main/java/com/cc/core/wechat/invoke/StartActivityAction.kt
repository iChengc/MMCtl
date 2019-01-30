package com.cc.core.wechat.invoke

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.cc.core.ApplicationContext
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult

class StartActivityAction : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult("unknown activity")
        }

        if (ApplicationContext.forgroundActivity == null) {
            return ActionResult.failedResult("Wechat does not start")
        }

        val intent = Intent()
        intent.setClassName(ApplicationContext.forgroundActivity, args[0].toString())
        if (args.size > 1) {
            val bundle = Bundle()
            for (i in 1 until args.size) {
                //intent.putExtra(args[i].toString(), args[++i]);
            }
            intent.putExtras(bundle)
        }
        (ApplicationContext.forgroundActivity as Activity).startActivity(intent)
        return ActionResult.successResult()
    }

    override fun key(): String? {
        return "wechat:startActivity"
    }
}