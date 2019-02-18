package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.invoke.SendMessageAction
import com.cc.core.wechat.model.ImageMessage
import com.cc.core.wechat.model.VideoMessage
import com.cc.core.wechat.model.WeChatMessage

class SendMessage : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult("No message was provided")
        }
        val gson = Utils.messageDeserializeGson()

        KLog.e("*******", StrUtils.toJson(args))
        val msg = gson.fromJson(args[0].toString(), WeChatMessage::class.java)
        if (msg is ImageMessage) {
            val path = Utils.downloadFile(msg.getImageUrl(), false)
            msg.setImageUrl(path)
        } else if (msg is VideoMessage) {
            val path = Utils.downloadFile(msg.getVideoUrl(), true)
            msg.setVideoUrl(path)
        }
        return Actions.execute(SendMessageAction::class.java, StrUtils.toJson(msg))
    }

    override fun key(): String? {
        return "sendMessage"
    }
 }