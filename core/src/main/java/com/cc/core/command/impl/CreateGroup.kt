package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.wechat.invoke.CreateGroupAction

class CreateGroup : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        return Actions.execute(CreateGroupAction::class.java, *args)
    }

    override fun key(): String? {
        return "createGroup"
    }
}