package com.cc.core.wechat

import android.content.Intent
import com.cc.core.ApplicationContext
import com.cc.core.log.KLog
import com.cc.core.utils.FileUtil
import com.cc.core.utils.MD5
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnUtil
import com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnUtilGetFileKeyFunc
import com.cc.core.wechat.hook.CdnLogicHooks
import com.cc.core.wechat.model.message.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MessageUtils {
    companion object {
        const val RECEIVE_MESSAGE_BROADCAST = "com.cc.core.wechat.Receive_Message_Broadcast"

        private const val GetMsgSvrIdSql = "select msgId from message where msgSvrId = ?"

        private val lock = ArrayBlockingQueue<String>(1)
        private val executorService = Executors.newFixedThreadPool(5)

        fun receiveNewMessage(messageInfo : Object) {
            executorService.submit { processNewMessage(messageInfo) }
        }

        private fun processNewMessage(messageInfo : Object) {
            KLog.e("message ===>>>", StrUtils.toJson(messageInfo))
            val msgType = XposedHelpers.getIntField(messageInfo,
                    Wechat.HookMethodFunctions.Message.MessageTypeFieldId)

            var obj: Any? = XposedHelpers.getObjectField(messageInfo,
                    Wechat.HookMethodFunctions.Message.MessageToFieldId)
            val to = obj?.toString() ?: ""

            val from: String
            var content: String
            obj = XposedHelpers.getObjectField(messageInfo,
                    Wechat.HookMethodFunctions.Message.MessageContentFieldId)
            if (StrUtils.isGroupWechatId(to)) {
                val info = obj?.toString()?.split(":".toRegex(), 2)?.toTypedArray()
                        ?: arrayOf("", "")
                from = info[0]
                content = if (info.size > 1) info[1] else ""
                if (content[0] == '\n') {
                    content = content.substring(1)
                }
            } else {
                content = obj!!.toString()
                from = to
            }

            KLog.e("message content ===>>>", content)
            val dateTime = XposedHelpers.getLongField(messageInfo,
                    Wechat.HookMethodFunctions.Message.MessageDatetimeFieldId)

            obj = XposedHelpers.getObjectField(messageInfo,
                    Wechat.HookMethodFunctions.Message.MessageServIdFieldId)
            val msgservId = obj?.toString() ?: ""

            val msg: WeChatMessage
            when (msgType) {
                WeChatMessageType.IMAGE, WeChatMessageType.EMOJI -> {
                    msg = ImageMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setType(msgType)
                    if (WeChatMessageType.EMOJI == msgType) {
                        msg.setImageUrl(MessageUtils.getEmojiImageUrl(content)!!)
                    } else {
                        msg.setImageUrl(
                                MessageUtils.downloadImage(msg, content))
                    }
                }
                WeChatMessageType.VIDEO -> {
                    msg = VideoMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setVideoUrl(MessageUtils.downloadVideo(msg, content))
                }
                WeChatMessageType.TEXT -> {
                    msg = TextMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    msg.setContent(content)
                }
                WeChatMessageType.CARD -> {
                    msg = CardMessage()
                    msg.setFrom(from)
                    msg.setTarget(to)
                    msg.setCreateTime(dateTime)
                    msg.setMsgServId(msgservId)
                    MessageUtils.processCardMessage(msg, content)
                }
                else -> {
                    msg = UnsupportMessage()
                    msg.setContent("[不支持的消息格式]")
                    msg.setMessageDetails(content)
                    msg.setType(msgType)
                }
            }
            KLog.e("message", StrUtils.toJson(msg))
            MessageUtils.notifyMessageReceived(msg)
        }

        fun messageDeserializeGson(): Gson {
            return GsonBuilder().registerTypeAdapter(WeChatMessage::class.java, MessageTypeAdapter())
                    .create()
        }

        fun downloadImage(message: ImageMessage, details: String): String {
            val map = HookUtils.xmlToMap(details, "msg")

            val savePath = File(
                    FileUtil.getImageCacheDirectory(),
                    MD5.getMD5(details + System.currentTimeMillis())!! + ".jpg"
            ).absolutePath
            val msgId = getMessageIdByServId(message.getMsgServId())
            val fileKey = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
                    ModelCdnUtilGetFileKeyFunc,
                    "downimg", message.getCreateTime(), message.getTarget(), msgId.toString() + ""
            )
            val request = XposedHelpers.newInstance(
                    XposedHelpers.findClass(
                            "com.tencent.mars.cdn.CdnLogic\$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER
                    )
            )
            XposedHelpers.setObjectField(request, "fileKey", fileKey)
            XposedHelpers.setObjectField(request, "savePath", savePath)
            XposedHelpers.setObjectField(request, "fileid", map[".msg.img.\$cdnmidimgurl"])
            XposedHelpers.setObjectField(request, "aeskey", map[".msg.img.\$aeskey"])
            XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map[".msg.img.\$length"]))
            XposedHelpers.setIntField(request, "fileType", 2)
            XposedHelpers.setIntField(request, "transforTimeoutSeconds", 600)
            XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0)
            XposedHelpers.setBooleanField(request, "isAutoStart", true)
            XposedHelpers.setIntField(
                    request, "chatType", if (StrUtils.isGroupWechatId(message.getTarget())) 1 else 0
            )
            XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER),
                    "startC2CDownload", request
            )
            waitForDownloadFinish(fileKey.toString())

            // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);

            return savePath
        }

        fun downloadVideo(message: VideoMessage, details: String): String {
            val map = HookUtils.xmlToMap(details, "msg")

            val fileName = System.currentTimeMillis().toString() + ""
            val savePath = File(FileUtil.getVideoCacheDirectory(), "$fileName.mp4").absolutePath
            val fileKey = XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
                    ModelCdnUtilGetFileKeyFunc,
                    "downvideo", message.getCreateTime(), message.getTarget(), fileName
            )
            val request = XposedHelpers.newInstance(
                    XposedHelpers.findClass(
                            "com.tencent.mars.cdn.CdnLogic\$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER
                    )
            )

            XposedHelpers.setObjectField(request, "fileKey", fileKey)
            XposedHelpers.setObjectField(request, "savePath", savePath)
            XposedHelpers.setObjectField(request, "fileid", map[".msg.videomsg.\$cdnvideourl"])
            XposedHelpers.setObjectField(request, "aeskey", map[".msg.videomsg.\$aeskey"])
            XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map[".msg.videomsg.\$length"]))
            XposedHelpers.setIntField(request, "fileType", 4)
            XposedHelpers.setIntField(request, "transforTimeoutSeconds", 0)
            XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0)
            XposedHelpers.setBooleanField(request, "isAutoStart", true)
            XposedHelpers.setIntField(
                    request, "chatType", if (StrUtils.isGroupWechatId(message.getTarget())) 1 else 0
            )
            XposedHelpers.callStaticMethod(
                    XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER),
                    "startVideoStreamingDownload", request, 0
            )
            waitForDownloadFinish(fileKey.toString())
            return savePath
        }

        fun getMessageIdByServId(servId: String?): Long {
            val cursor = HookUtils.executeRawQuery(GetMsgSvrIdSql, servId!!)
            var msgId: Long = -1
            if (cursor.moveToFirst()) {
                msgId = cursor.getLong(cursor.getColumnIndex("msgId"))
            }
            cursor.close()
            return msgId
        }

        /**
         * <msg>
         * <appmsg appid="" sdkver="0">
         * <title>蔚来ES8，百公里烧35~40升柴油的电动车</title>
         * <des>尽管我早就知道蔚来要搞幺蛾子，但没想到这么邪乎。</des>
         * <action></action>
         * <type>5</type>
         * <showtype>0</showtype>
         * <soundtype>0</soundtype>
         * <mediatagname></mediatagname>
         * <messageext></messageext>
         * <messageaction></messageaction>
         * <content></content>
         * <contentattr>0</contentattr>
         * <url>http://mp.weixin.qq.com/s?__biz=MzIxMTQ3NTUxOQ==&amp;mid=2247483835&amp;idx=1&amp;sn=9d31784a2743af7c0870e0ff643defbe&amp;chksm=97558ceba02205fdf1f7e4220982acf82def12b02933c93827c01013124c3dce4df7a4ddbf56&amp;mpshare=1&amp;scene=1&amp;srcid=0220YC1yZfdnp61e3prdokdm#rd</url>
         * <lowurl></lowurl>
         * <dataurl></dataurl>
         * <lowdataurl></lowdataurl>
         * <appattach>
         * <totallen>0</totallen>
         * <attachid></attachid>
         * <emoticonmd5></emoticonmd5>
         * <fileext></fileext>
         * <cdnthumburl>3055020100044e304c020100020421e3c3ff02033d11fe020435f516d202045c6e046e0427777875706c6f61645f3634313335373339354063686174726f6f6d315f313535303731333936350204010400030201000400</cdnthumburl>
         * <cdnthumbmd5>01c02f8a365230b687b433101e70cd55</cdnthumbmd5>
         * <cdnthumblength>7089</cdnthumblength>
         * <cdnthumbwidth>160</cdnthumbwidth>
         * <cdnthumbheight>160</cdnthumbheight>
         * <cdnthumbaeskey>c6b589dfeba455fd715750e2eaec26f8</cdnthumbaeskey>
         * <aeskey>c6b589dfeba455fd715750e2eaec26f8</aeskey>
         * <encryver>0</encryver>
         * <filekey>9640220477@chatroom5_1550728021</filekey>
        </appattach> *
         * <extinfo></extinfo>
         * <sourceusername>gh_353c2ff556ef</sourceusername>
         * <sourcedisplayname></sourcedisplayname>
         * <thumburl>https://mmbiz.qlogo.cn/mmbiz_jpg/l5N3ll27MAoppibBWFuqt0F0aV8luRias3PediappO5q8KLyTQnEyAaqiaxH1IqQQYUENWMmwn2g18XnFc6ZmTN0Pw/300?wx_fmt=jpeg&amp;wxfrom=1</thumburl>
         * <md5></md5>
         * <statextstr></statextstr>
        </appmsg> *
         * <fromusername>xnhjcc</fromusername>
         * <scene>0</scene>
         * <appinfo>
         * <version>1</version>
         * <appname></appname>
        </appinfo> *
         * <commenturl></commenturl>
        </msg> *
         * @param message
         * @param details
         */
        fun processCardMessage(message: CardMessage?, details: String) {
            if (message == null) {
                return
            }

            val map = HookUtils.xmlToMap(details, "msg")
            if (map.isEmpty()) {
                return
            }

            message.setDescription(map[".msg.appmsg.des"])
            message.setTitle(map[".msg.appmsg.title"])
            message.setThumbUrl(map[".msg.appmsg.thumburl"])
            message.setUrl(map[".msg.appmsg.url"])
            message.setCardType(map[".msg.appmsg.type"])

            if ("8".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：斗图表情]")
            }else if ("6".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：文件传输]")
            } else if ("33".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：小程序分享]")
            } else if ("2001".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：红包/收款]")
            } else if ("2000".equals(message.getCardType())) {
                message.setDescription("[不支持的卡片消息（${message.getCardType()}）：转账]")
            }
        }

        fun getEmojiImageUrl(details: String): String? {

            val map = HookUtils.xmlToMap(details, "msg")
            return map[".msg.emoji.\$cdnurl"]
        }

        fun notifyMessageReceived(msg: WeChatMessage) {
            val i = Intent(RECEIVE_MESSAGE_BROADCAST)
            i.putExtra("msg", StrUtils.toJson(msg))
            ApplicationContext.application().sendBroadcast(i)
        }

        private fun waitForDownloadFinish(fileKey: String?) {
            CdnLogicHooks.registerDownloadListeners(fileKey, object : CdnLogicHooks.CdnDownloadFinishListener {
                override fun onFinishDownload(fileKey: String) {
                    lock.put(fileKey)
                }
            })
            lock.poll(30, TimeUnit.SECONDS)
        }
    }
}