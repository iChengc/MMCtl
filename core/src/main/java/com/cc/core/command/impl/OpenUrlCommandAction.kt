package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.actions.accessibility.impl.BackAction
import com.cc.core.utils.Utils
import com.cc.core.wechat.invoke.OpenUrlAction

class OpenUrlCommandAction : Action {
    override fun execute(actiongId : String, vararg args: Any?): ActionResult? {
        val result = Actions.execute(OpenUrlAction::class.java, actiongId,  args)
        if (!result!!.isSuccess()) {
            return result
        }
        if (args.size > 1) {
            Utils.sleep((args[1] as Int).toLong())
            Actions.execute(BackAction::class.java, actiongId)
        }
        return ActionResult.successResult(actiongId)
    }

    override fun key(): String? {
        return "openUrl"
    }
}