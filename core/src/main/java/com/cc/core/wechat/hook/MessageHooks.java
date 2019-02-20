package com.cc.core.wechat.hook;

import android.util.Log;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.WechatMessageType;
import com.cc.core.wechat.model.ImageMessage;
import com.cc.core.wechat.model.TextMessage;
import com.cc.core.wechat.model.WeChatMessage;
import com.cc.core.xposed.BaseXposedHook;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.MessageSyncExtensionClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.MessageSyncExtensionProcessCommonMessageFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.ProtocolAddMsgInfoClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.SyncMessageNotifierClass;

public class MessageHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(MessageSyncExtensionClass, classLoader, MessageSyncExtensionProcessCommonMessageFunc,
                findClass(ProtocolAddMsgInfoClass, classLoader),
                findClass(SyncMessageNotifierClass, classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        Object messageInfo = XposedHelpers.getObjectField(param.args[0], Wechat.HookMethodFunctions.Message.MessageInfoFieldId);

                        int msgType = XposedHelpers.getIntField(messageInfo, Wechat.HookMethodFunctions.Message.MessageTypeFieldId);

                        Object obj = XposedHelpers.getObjectField(messageInfo, Wechat.HookMethodFunctions.Message.MessageToFieldId);
                        String to = obj == null ? "" : obj.toString();

                        String from, content;
                        obj = XposedHelpers.getObjectField(messageInfo, Wechat.HookMethodFunctions.Message.MessageContentFieldId);
                        if (StrUtils.isGroupWechatId(to)) {
                            String[] info = obj == null ? new String[]{"", ""} : obj.toString().split(":", 2);
                            from = info[0];
                            content = info.length > 1 ? info[1] : "";
                            if (content.charAt(0) == '\n') {
                                content = content.substring(1, content.length());
                            }
                        } else {
                            content = obj.toString();
                            from = to;
                        }
                        long dateTime = XposedHelpers.getLongField(messageInfo, Wechat.HookMethodFunctions.Message.MessageDatetimeFieldId);

                        obj = XposedHelpers.getObjectField(messageInfo, Wechat.HookMethodFunctions.Message.MessageServIdFieldId);
                        String msgservId = obj == null ? "" : obj.toString();

                        WeChatMessage msg;
                        if (msgType == WechatMessageType.IMAGE) {
                            msg = new ImageMessage();
                            ((ImageMessage) msg).setImageUrl(content);
                        } else {
                            msg = new TextMessage();
                            ((TextMessage) msg).setContent(content);
                        }
                        msg.setFrom(from);
                        msg.setTarget(to);
                        msg.setCreateTime(dateTime * 1000);
                        msg.setType(msgType);
                        msg.setMsgServId(msgservId);

                        if (msg instanceof ImageMessage) {
                            ((ImageMessage) msg).setImageUrl(MessageUtils.downloadImage((ImageMessage) msg));
                            KLog.e("message:" + new File(((ImageMessage) msg).getImageUrl()).exists(), StrUtils.toJson(msg));
                        }
                        KLog.e("message", StrUtils.toJson(msg));
                    }
                });
    }
}
