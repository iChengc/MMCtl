package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.tool.XLogHooks

class HookXLogAction : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        var hook = Wechat.lookup(XLogHooks::class.java)
        if (hook == null){
            hook = XLogHooks()
            Wechat.addHook(hook)
        }

        (hook as XLogHooks).hook(Wechat.WECHAT_CLASSLOADER)
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:HookXLog"
    }
}