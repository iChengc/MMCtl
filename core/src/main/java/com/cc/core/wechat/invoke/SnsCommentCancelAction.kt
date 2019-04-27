package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Sns.SnsTimelineCancelCommentRequest
import com.cc.core.wechat.model.sns.SnsCommentRequest
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.newInstance

class SnsCommentCancelAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        /*if (args.size < 2) {
            return ActionResult.failedResult(actionId, "No enough arguments!")
        }

        val snsId = args[0] as String
        val commentId = args[1] as Double
        val deleteCommentRequest = newInstance(findClass(SnsTimelineCancelCommentRequest, Wechat.WECHAT_CLASSLOADER),
                snsId.toLong(), commentId.toInt())
        HookUtils.enqueueNetScene(deleteCommentRequest, 0)
        return ActionResult.successResult(actionId)*/
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "Illegal argument exception: no comment was found!")
        }

        val raw = args[0] as String
        val comment = StrUtils.fromNumberJson(raw, SnsCommentRequest::class.java)
        if (TextUtils.isEmpty(comment.snsId)) {
            return ActionResult.failedResult(actionId, "no sns id was found!")
        } else if (comment.comment == null) {
            return ActionResult.failedResult(actionId, "no deleted comment was found!")
        } else if (!TextUtils.equals(Wechat.LoginWechatId, comment!!.comment!!.wechatId)) {
            return ActionResult.failedResult(actionId, "you must delete your own comment")
        }

        val deleteComment = newInstance(findClass(Wechat.Hook.Sns.SnsTimelineCommentProtobuf, Wechat.WECHAT_CLASSLOADER))
        if (comment.comment != null) {
            XposedHelpers.setObjectField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommenterField, comment.comment!!.wechatId)
            XposedHelpers.setObjectField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommenterNameField, comment.comment!!.nickName)
            XposedHelpers.setIntField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommentIdField, comment.comment!!.id)
            XposedHelpers.setObjectField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommentReplay2Field, comment.comment!!.reply2)
            XposedHelpers.setIntField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommentReplay2IdField, comment.comment!!.replayId)
            XposedHelpers.setObjectField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommentContentField, comment.comment!!.content)
            XposedHelpers.setIntField(deleteComment, Wechat.Hook.Sns.SnsTimelineCommentTimeField, comment.comment!!.createTime.toInt())
        }
        val deleteCommentRequest = newInstance(findClass(SnsTimelineCancelCommentRequest, Wechat.WECHAT_CLASSLOADER),
                comment.snsId!!.toLong(), 4, deleteComment, null)
        HookUtils.enqueueNetScene(deleteCommentRequest, 0)

        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
       return "wechat:snsCommentCancel"
    }
}