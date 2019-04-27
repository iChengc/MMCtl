package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.invoke.SnsCommentCancelAction

class SnsCommentCancel : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no comment was found!")
        }
        val comment = if (args[0] is String) {
            args[0]
        } else {
            StrUtils.toNumberJson(args[0])
        }

        return Actions.execute(SnsCommentCancelAction::class.java, actionId, comment)
    }

    override fun key(): String? {
        return "snsCommentCancel"
    }
}