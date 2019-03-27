package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Sns.*
import com.cc.core.wechat.model.sns.SnsCommentRequest
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.*

class SnsCommentAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no comment was found!")
        }

        val raw = args[0] as String
        val comment = StrUtils.fromNumberJson(raw, SnsCommentRequest::class.java)

        val sns = SnsUtils.getWechatRawSnsInfo(comment.snsId!!.toLong()) ?: return ActionResult.failedResult(actionId, "can not find sns by id:${comment.snsId}")
        val replayComment = newInstance(findClass(SnsTimelineCommentProtobuf, Wechat.WECHAT_CLASSLOADER))
        if (comment.replayComment != null) {
            setObjectField(replayComment, SnsTimelineCommenterField, comment.replayComment!!.wechatId)
            setObjectField(replayComment, SnsTimelineCommenterNameField, comment.replayComment!!.nickName)
            setIntField(replayComment, SnsTimelineCommentIdField, comment.replayComment!!.id)
            setObjectField(replayComment, SnsTimelineCommentReplay2Field, comment.replayComment!!.reply2)
            setIntField(replayComment, SnsTimelineCommentReplay2IdField, comment.replayComment!!.replayId)
            setObjectField(replayComment, SnsTimelineCommentContentField, comment.replayComment!!.content)
            setLongField(replayComment, SnsTimelineCommentTimeField, comment.replayComment!!.createTime)
        }
        XposedHelpers.callStaticMethod(XposedHelpers.findClass(Wechat.Hook.Sns.SnsTimelineCommentHelper, Wechat.WECHAT_CLASSLOADER), Wechat.Hook.Sns.SnsTimelineCommentSend, sns, 2, comment.content, replayComment, 0, 0)

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:snsComment"
    }
}