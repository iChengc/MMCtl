package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.wechat.invoke.CreateGroupAction

class CreateGroup : Action {
<<<<<<< 756ff7e6e23b2476cfd7526e001b58005773acbe
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        return Actions.execute(CreateGroupAction::class.java, actionId, *args)
=======
    override fun execute(vararg args: Any?): ActionResult? {
        return Actions.execute(CreateGroupAction::class.java, *args)
>>>>>>> create group
    }

    override fun key(): String? {
        return "createGroup"
    }
}