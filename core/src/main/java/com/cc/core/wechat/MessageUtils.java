package com.cc.core.wechat;

import android.content.Intent;
import android.database.Cursor;

import com.cc.core.ApplicationContext;
import com.cc.core.utils.FileUtil;
import com.cc.core.utils.MD5;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.VideoMessage;

import com.cc.core.wechat.model.message.WeChatMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnUtil;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnUtilGetFileKeyFunc;

public class MessageUtils {
    private MessageUtils(){}
    public static final String RECEIVE_MESSAGE_BROADCAST = "com.cc.core.wechat.Receive_Message_Broadcast";

    private static String GetMsgSvrIdSql = "select msgId from message where msgSvrId = ?";

    public static Gson messageDeserializeGson() {
        return new GsonBuilder().registerTypeAdapter(WeChatMessage.class, new MessageTypeAdapter()).create();
    }

    public static String downloadImage(ImageMessage message, String details) {
        Map<String, String> map = HookUtils.Companion.xmlToMap(details, "msg");

        // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);
        String savePath = new File(FileUtil.getImageCacheDirectory(), MD5.getMD5(details + System.currentTimeMillis()) + ".jpg").getAbsolutePath();
        long msgId = getMessageIdByServId(message.getMsgServId());
        Object fileKey = XposedHelpers.callStaticMethod(XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
            ModelCdnUtilGetFileKeyFunc,
                "downimg", message.getCreateTime(), message.getTarget(), msgId + "");
        Object request = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER));
        XposedHelpers.setObjectField(request, "fileKey", fileKey);
        XposedHelpers.setObjectField(request, "fileid", map.get(".msg.img.$cdnmidimgurl"));
        XposedHelpers.setObjectField(request, "savePath", savePath);
        XposedHelpers.setObjectField(request, "aeskey", map.get(".msg.img.$aeskey"));
        XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map.get(".msg.img.$length")));
        XposedHelpers.setIntField(request, "fileType", 2);
        XposedHelpers.setIntField(request, "transforTimeoutSeconds", 600);
        XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0);
        XposedHelpers.setBooleanField(request, "isAutoStart", true);
        XposedHelpers.setIntField(request, "chatType", StrUtils.isGroupWechatId(message.getTarget()) ? 1 : 0);
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER), "startC2CDownload", request);
        // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);

        return savePath;
    }

    public static String downloadVideo(VideoMessage message, String details) {
        Map<String, String> map = HookUtils.Companion.xmlToMap(details, "msg");

        String savePath = new File(FileUtil.getImageCacheDirectory(), MD5.getMD5(details + System.currentTimeMillis()) + ".mp4").getAbsolutePath();
        long msgId = getMessageIdByServId(message.getMsgServId());
        Object fileKey = XposedHelpers.callStaticMethod(XposedHelpers.findClass(ModelCdnUtil, Wechat.WECHAT_CLASSLOADER),
            ModelCdnUtilGetFileKeyFunc,
                "downimg", message.getCreateTime() / 1000, message.getTarget(), msgId + "");
        Object request = XposedHelpers.newInstance(XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER));

        XposedHelpers.setObjectField(request, "fileKey", fileKey);
        XposedHelpers.setObjectField(request, "fileid", map.get(".msg.img.$cdnmidimgurl"));
        XposedHelpers.setObjectField(request, "savePath", savePath);
        XposedHelpers.setObjectField(request, "aeskey", map.get(".msg.img.$aeskey"));
        XposedHelpers.setIntField(request, "fileSize", Integer.valueOf(map.get(".msg.img.$length")));
        XposedHelpers.setIntField(request, "fileType", 2);
        XposedHelpers.setIntField(request, "transforTimeoutSeconds", 600);
        XposedHelpers.setIntField(request, "queueTimeoutSeconds", 0);
        XposedHelpers.setBooleanField(request, "isAutoStart", true);
        XposedHelpers.setIntField(request, "chatType", StrUtils.isGroupWechatId(message.getTarget()) ? 1 : 0);
        XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic", Wechat.WECHAT_CLASSLOADER), "startC2CDownload", request);
        // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);

        return savePath;
    }

    public static long getMessageIdByServId(String servId) {
        Cursor cursor = HookUtils.Companion.executeRawQuery(GetMsgSvrIdSql, servId);
        long msgId = -1;
        if (cursor.moveToFirst()) {
            msgId = cursor.getLong(cursor.getColumnIndex("msgId"));
        }
        cursor.close();
        return msgId;
    }

    /**
     * <msg>
     *     	<appmsg appid="" sdkver="0">
     *     		<title>蔚来ES8，百公里烧35~40升柴油的电动车</title>
     *     		<des>尽管我早就知道蔚来要搞幺蛾子，但没想到这么邪乎。</des>
     *     		<action />
     *     		<type>5</type>
     *     		<showtype>0</showtype>
     *     		<soundtype>0</soundtype>
     *     		<mediatagname />
     *     		<messageext />
     *     		<messageaction />
     *     		<content />
     *     		<contentattr>0</contentattr>
     *     		<url>http://mp.weixin.qq.com/s?__biz=MzIxMTQ3NTUxOQ==&amp;mid=2247483835&amp;idx=1&amp;sn=9d31784a2743af7c0870e0ff643defbe&amp;chksm=97558ceba02205fdf1f7e4220982acf82def12b02933c93827c01013124c3dce4df7a4ddbf56&amp;mpshare=1&amp;scene=1&amp;srcid=0220YC1yZfdnp61e3prdokdm#rd</url>
     *     		<lowurl />
     *     		<dataurl />
     *     		<lowdataurl />
     *     		<appattach>
     *     			<totallen>0</totallen>
     *     			<attachid />
     *     			<emoticonmd5 />
     *     			<fileext />
     *     			<cdnthumburl>3055020100044e304c020100020421e3c3ff02033d11fe020435f516d202045c6e046e0427777875706c6f61645f3634313335373339354063686174726f6f6d315f313535303731333936350204010400030201000400</cdnthumburl>
     *     			<cdnthumbmd5>01c02f8a365230b687b433101e70cd55</cdnthumbmd5>
     *     			<cdnthumblength>7089</cdnthumblength>
     *     			<cdnthumbwidth>160</cdnthumbwidth>
     *     			<cdnthumbheight>160</cdnthumbheight>
     *     			<cdnthumbaeskey>c6b589dfeba455fd715750e2eaec26f8</cdnthumbaeskey>
     *     			<aeskey>c6b589dfeba455fd715750e2eaec26f8</aeskey>
     *     			<encryver>0</encryver>
     *     			<filekey>9640220477@chatroom5_1550728021</filekey>
     *     		</appattach>
     *     		<extinfo />
     *     		<sourceusername>gh_353c2ff556ef</sourceusername>
     *     		<sourcedisplayname />
     *     		<thumburl>https://mmbiz.qlogo.cn/mmbiz_jpg/l5N3ll27MAoppibBWFuqt0F0aV8luRias3PediappO5q8KLyTQnEyAaqiaxH1IqQQYUENWMmwn2g18XnFc6ZmTN0Pw/300?wx_fmt=jpeg&amp;wxfrom=1</thumburl>
     *     		<md5 />
     *     		<statextstr />
     *     	</appmsg>
     *     	<fromusername>xnhjcc</fromusername>
     *     	<scene>0</scene>
     *     	<appinfo>
     *     		<version>1</version>
     *     		<appname></appname>
     *     	</appinfo>
     *     	<commenturl></commenturl>
     *     </msg>
     * @param message
     * @param details
     */
    public static void processCardMessage(CardMessage message, String details) {
        if (message == null) {
            return;
        }

        Map<String, String> map = HookUtils.Companion.xmlToMap(details, "msg");
        if (map.isEmpty()) {
            return;
        }

        message.setDescription(map.get(".msg.appmsg.des"));

        message.setTitle(map.get(".msg.appmsg.title"));

        message.setThumbUrl(map.get(".msg.appmsg.thumburl"));

        message.setUrl(map.get(".msg.appmsg.url"));
    }

    public static String getEmojiImageUrl(String details) {

        Map<String, String> map = HookUtils.Companion.xmlToMap(details, "msg");
        return map.get(".msg.emoji.$cdnurl");
    }

    public static void notifyMessageReceived(WeChatMessage msg) {
        Intent i = new Intent(RECEIVE_MESSAGE_BROADCAST);
        i.putExtra("msg", StrUtils.toJson(msg));
        ApplicationContext.application().sendBroadcast(i);
    }
}
