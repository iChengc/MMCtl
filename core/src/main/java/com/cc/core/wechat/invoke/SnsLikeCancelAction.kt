package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.Wechat
import de.robv.android.xposed.XposedHelpers

class SnsLikeCancelAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no sns id was found!")
        }

        val key = "sns_table_${args[0]}"

        XposedHelpers.callStaticMethod(XposedHelpers.findClass(Wechat.Hook.Sns.SnsTimelineCommentHelper, Wechat.WECHAT_CLASSLOADER), Wechat.Hook.Sns.SnsTimelineCancelLike, key)

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:snsLikeCancel"
    }
}