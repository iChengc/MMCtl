package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.WeChatMessageType;
import com.cc.core.wechat.model.message.CardMessage;
import com.cc.core.wechat.model.message.ImageMessage;
import com.cc.core.wechat.model.message.TextMessage;
import com.cc.core.wechat.model.message.UnsupportMessage;
import com.cc.core.wechat.model.message.VideoMessage;
import com.cc.core.wechat.model.message.WeChatMessage;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.MessageSyncExtensionClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.MessageSyncExtensionProcessCommonMessageFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.ProtocolAddMsgInfoClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Message.SyncMessageNotifierClass;

public class MessageHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(MessageSyncExtensionClass, classLoader,
            MessageSyncExtensionProcessCommonMessageFunc,
            findClass(ProtocolAddMsgInfoClass, classLoader),
            findClass(SyncMessageNotifierClass, classLoader),
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    Object messageInfo = XposedHelpers.getObjectField(param.args[0],
                        Wechat.HookMethodFunctions.Message.MessageInfoFieldId);

                    KLog.e("message ===>>>", StrUtils.toJson(messageInfo));
                    int msgType = XposedHelpers.getIntField(messageInfo,
                        Wechat.HookMethodFunctions.Message.MessageTypeFieldId);

                    Object obj = XposedHelpers.getObjectField(messageInfo,
                        Wechat.HookMethodFunctions.Message.MessageToFieldId);
                    String to = obj == null ? "" : obj.toString();

                    String from, content;
                    obj = XposedHelpers.getObjectField(messageInfo,
                        Wechat.HookMethodFunctions.Message.MessageContentFieldId);
                    if (StrUtils.isGroupWechatId(to)) {
                        String[] info =
                            obj == null ? new String[] { "", "" } : obj.toString().split(":", 2);
                        from = info[0];
                        content = info.length > 1 ? info[1] : "";
                        if (content.charAt(0) == '\n') {
                            content = content.substring(1);
                        }
                    } else {
                        content = obj.toString();
                        from = to;
                    }

                    long dateTime = XposedHelpers.getLongField(messageInfo,
                        Wechat.HookMethodFunctions.Message.MessageDatetimeFieldId);

                    obj = XposedHelpers.getObjectField(messageInfo,
                        Wechat.HookMethodFunctions.Message.MessageServIdFieldId);
                    String msgservId = obj == null ? "" : obj.toString();

                    WeChatMessage msg;
                    switch (msgType) {
                        case WeChatMessageType.IMAGE:
                        case WeChatMessageType.EMOJI:
                            msg = new ImageMessage();
                            msg.setFrom(from);
                            msg.setTarget(to);
                            msg.setCreateTime(dateTime);
                            msg.setMsgServId(msgservId);
                            msg.setType(msgType);
                            if (WeChatMessageType.EMOJI == msgType) {
                                ((ImageMessage) msg).setImageUrl(MessageUtils.Companion.getEmojiImageUrl(content));
                            } else {
                                ((ImageMessage) msg).setImageUrl(
                                    MessageUtils.Companion.downloadImage((ImageMessage) msg, content));
                            }
                            break;
                        case WeChatMessageType.VIDEO:
                            msg = new VideoMessage();
                            msg.setFrom(from);
                            msg.setTarget(to);
                            msg.setCreateTime(dateTime);
                            msg.setMsgServId(msgservId);
                            ((VideoMessage) msg).setVideoUrl(MessageUtils.Companion.downloadVideo((VideoMessage) msg, content));
                            break;
                        case WeChatMessageType.TEXT:
                            msg = new TextMessage();
                            msg.setFrom(from);
                            msg.setTarget(to);
                            msg.setCreateTime(dateTime);
                            msg.setMsgServId(msgservId);
                            ((TextMessage) msg).setContent(content);
                            break;
                        case WeChatMessageType.CARD:
                            msg = new CardMessage();
                            msg.setFrom(from);
                            msg.setTarget(to);
                            msg.setCreateTime(dateTime);
                            msg.setMsgServId(msgservId);
                            MessageUtils.Companion.processCardMessage((CardMessage) msg, content);
                            break;
                        default:
                            msg = new UnsupportMessage();
                            ((UnsupportMessage) msg).setContent("[不支持的消息格式]");
                            ((UnsupportMessage) msg).setMessageDetails(content);
                            msg.setType(msgType);
                    }
                    KLog.e("message", StrUtils.toJson(msg));
                    MessageUtils.Companion.notifyMessageReceived(msg);
                }
            });
    }

}
