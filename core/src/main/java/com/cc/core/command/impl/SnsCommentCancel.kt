package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.wechat.invoke.SnsCommentCancelAction

class SnsCommentCancel : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.size < 2) {
            return ActionResult.failedResult(actionId, "No enough arguments!")
        }
        return Actions.execute(SnsCommentCancelAction::class.java, actionId, *args)
    }

    override fun key(): String? {
        return "snsCommentCancel"
    }
}