package com.cc.core.wechat.invoke

import android.app.Application
import android.content.Intent
import com.cc.core.ApplicationContext
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat

class OpenUrlAction : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val i = Intent()
        i.setClassName(ApplicationContext.application(), Wechat.Resources.WEBVIEW_ACTIVITY_CLASS_NAME)
        i.putExtra("rawUrl", args[0].toString())
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        (ApplicationContext.application() as Application).startActivity(i)
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:open_url"
    }
}