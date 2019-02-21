package com.cc.core.wechat.invoke

import com.cc.core.ApplicationContext
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.MessageUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.*
import com.cc.core.wechat.model.message.ImageMessage
import com.cc.core.wechat.model.message.TextMessage
import com.cc.core.wechat.model.message.VideoMessage
import com.cc.core.wechat.model.message.WeChatMessage
import de.robv.android.xposed.XposedHelpers
import java.util.ArrayList
import java.util.HashMap

class SendMessageAction : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult("No message was provided")
        }
        val gson = MessageUtils.messageDeserializeGson()

        val msg = gson.fromJson(args[0].toString(), WeChatMessage::class.java)
        if (msg is TextMessage) {
            sendTextMessage(msg)
        } else if (msg is ImageMessage) {
            sendImageMessage(msg)
        } else if (msg is VideoMessage) {
            sendVideoMessage(msg)
        } else {
            return ActionResult.failedResult("Not implement " + msg.javaClass.simpleName)
        }

        return ActionResult.successResult()
    }

    private fun sendTextMessage(msg: TextMessage) {
        val messageType = 1
        var flag = 0
        var messageAttributes: MutableMap<String, String>? = null
        if (msg.getAtUsers() != null && !msg.getAtUsers()!!.isEmpty()) {
            flag = 291

            messageAttributes = HashMap()
            messageAttributes["atuserlist"] = "<![CDATA[" + StrUtils.join(msg.getAtUsers()!!.asIterable(), ",") + "]]>"
        }
        val request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.NetSceneSendMsgClass, Wechat.WECHAT_CLASSLOADER),
                msg.getTarget(), msg.getContent(), messageType, flag, messageAttributes)
        HookUtils.enqueueNetScene(request, 0)
    }

    private fun sendImageMessage(msg: ImageMessage) {

        val data = arrayOf(3, msg.getFrom(), msg.getTarget(), msg.getImageUrl(), 1, null, 0, "", "", true, NetSceneUploadMsgImgMaskResId)
        val request = XposedHelpers.newInstance(XposedHelpers.findClass(NetSceneUploadMsgImg, Wechat.WECHAT_CLASSLOADER), *data)
        HookUtils.enqueueNetScene(request, 0)
    }

    private fun sendVideoMessage(msg: VideoMessage) {

        val path = ArrayList<String?>()
        path.add(msg.getVideoUrl())
        val handler = XposedHelpers.newInstance(XposedHelpers.findClass(NetSceneUploadMsgVideo, Wechat.WECHAT_CLASSLOADER),
                ApplicationContext.forgroundActivity, path, null, msg.getTarget(), 2, null)

        XposedHelpers.callStaticMethod(XposedHelpers.findClass(UploadMsgVideoHandler, Wechat.WECHAT_CLASSLOADER), "post", handler, "ChattingUI_importMultiVideo")
    }

    override fun key(): String? {
        return "wechat:sendMessage"
    }
}