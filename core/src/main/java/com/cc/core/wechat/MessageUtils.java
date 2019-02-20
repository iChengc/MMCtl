package com.cc.core.wechat;

import android.database.Cursor;

import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;
import com.cc.core.utils.MD5;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.hook.RemoteRespHooks;
import com.cc.core.wechat.model.ImageMessage;
import com.cc.core.wechat.model.VideoMessage;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnTranHelper;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.ModelCdnTranHelperGetFileKeyFunc;

public class MessageUtils {
    private MessageUtils(){}
    private static ArrayBlockingQueue<String> lock = new ArrayBlockingQueue<>(1);
    private static String GetMsgSvrIdSql = "select msgId from message where msgSvrId = ?";
    private static String GetImgMsgCntSql = "select count(msgId) from message where type = ?";

    public static String downloadImage(ImageMessage message) {
        Map<String, String> map = HookUtils.Companion.xmlToMap(message.getImageUrl(), "msg");

        // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);
        String savePath = new File(FileUtil.getImageCacheDirectory(), MD5.getMD5(message.getImageUrl() + System.currentTimeMillis()) + ".jpg").getAbsolutePath();
        long msgId = getMessageIdByServId(message.getMsgServId());
        Object fileKey = XposedHelpers.callStaticMethod(XposedHelpers.findClass(ModelCdnTranHelper, Wechat.WECHAT_CLASSLOADER), ModelCdnTranHelperGetFileKeyFunc,
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

    public static String downloadVideo(VideoMessage message) {
        Map<String, String> map = HookUtils.Companion.xmlToMap(message.getVideoUrl(), "msg");

        // localObject = com.tencent.mm.modelcdntran.d.a("downimg", field_createTime, field_talker, field_msgId);
        String savePath = new File(FileUtil.getImageCacheDirectory(), MD5.getMD5(message.getVideoUrl() + System.currentTimeMillis()) + ".jpg").getAbsolutePath();
        long msgId = getMessageIdByServId(message.getMsgServId());
        Object fileKey = XposedHelpers.callStaticMethod(XposedHelpers.findClass(ModelCdnTranHelper, Wechat.WECHAT_CLASSLOADER), ModelCdnTranHelperGetFileKeyFunc,
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

    public static long getImageMessageCnt() {
        Cursor cursor = HookUtils.Companion.executeRawQuery(GetImgMsgCntSql, WechatMessageType.IMAGE + "");
        long msgCnt = -1;
        if (cursor.moveToFirst()) {
            msgCnt = cursor.getLong(0);
        }
        cursor.close();
        return msgCnt;
    }
}
