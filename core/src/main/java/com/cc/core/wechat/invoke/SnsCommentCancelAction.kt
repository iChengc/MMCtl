package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Sns.SnsTimelineCancelCommentRequest
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.newInstance

class SnsCommentCancelAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.size < 2) {
            return ActionResult.failedResult(actionId, "No enough arguments!")
        }

        val snsId = args[0] as String
        val commentId = args[1] as Double
        val deleteCommentRequest = newInstance(findClass(SnsTimelineCancelCommentRequest, Wechat.WECHAT_CLASSLOADER),
                snsId.toLong(), commentId.toInt())
        HookUtils.enqueueNetScene(deleteCommentRequest, 0)
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
       return "wechat:snsCommentCancel"
    }
}