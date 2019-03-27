package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Sns.SnsTimelineCommentHelper
import com.cc.core.wechat.Wechat.Hook.Sns.SnsTimelineCommentSend
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass

class SnsLikeAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty() || args[0] == null) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no sns id was found!")
        }
        val snsId = args[0] as String
        val sns = SnsUtils.getWechatRawSnsInfo(snsId.toLong()) ?: return ActionResult.failedResult(actionId, "can not find sns by id:$snsId")

        callStaticMethod(findClass(SnsTimelineCommentHelper, Wechat.WECHAT_CLASSLOADER), SnsTimelineCommentSend, sns, 1, "", null, 0, 0)

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:snsLike"
    }
}