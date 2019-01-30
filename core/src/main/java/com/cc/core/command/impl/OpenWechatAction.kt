package com.cc.core.command.impl

import com.cc.core.ApplicationContext
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat

class OpenWechatAction : Action {
    override fun key(): String? {
        return "openWechat"
    }

    override fun execute(vararg args: Any?): ActionResult? {
        Wechat.startApp(ApplicationContext.application())
        return ActionResult.successResult()
    }
}