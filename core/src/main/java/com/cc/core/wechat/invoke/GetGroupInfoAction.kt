package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult

class GetGroupInfoAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun key(): String? {
        return "wechat:getGroupInfo"
    }
}