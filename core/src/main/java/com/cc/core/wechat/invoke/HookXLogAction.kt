package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.XLogHooks

class HookXLogAction : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        val hook = Wechat.lookup(XLogHooks::class.java)
        if (hook == null){
            return ActionResult.failedResult("Can not find XLogHooks!")
        }

        (hook as XLogHooks).hook(Wechat.WECHAT_CLASSLOADER)
        return ActionResult.successResult()
    }

    override fun key(): String? {
        return "wechat:HookXLog"
    }
}