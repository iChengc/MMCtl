package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.model.TextMessage;
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
        KLog.e("===>>>>>" + ProtocolAddMsgInfoClass);
        hookMethod(MessageSyncExtensionClass, classLoader, MessageSyncExtensionProcessCommonMessageFunc,
                findClass(ProtocolAddMsgInfoClass, classLoader),
                findClass(SyncMessageNotifierClass, classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object messageInfo = XposedHelpers.getObjectField(param.args[0], Wechat.HookMethodFunctions.Message.MessageInfoFieldId);

                        int msgType = XposedHelpers.getIntField(messageInfo, Wechat.HookMethodFunctions.Message.MessageTypeFieldId);

                        Object obj = XposedHelpers.getObjectField(messageInfo, Wechat.HookMethodFunctions.Message.MessageContentFieldId);
                        String[] info = obj == null ? new String[]{"", ""} : obj.toString().split(":", 2);

                        String from = info[0];
                        String content = info.length > 1 ? info[1] : "";
                        if (content.charAt(0) == '\n') {
                            content = content.substring(1, content.length());
                        }

                        obj = XposedHelpers.getObjectField(messageInfo, Wechat.HookMethodFunctions.Message.MessageToFieldId);
                        String to = obj == null ? "" : obj.toString();

                        long dateTime = XposedHelpers.getLongField(messageInfo, Wechat.HookMethodFunctions.Message.MessageDatetimeFieldId);

                        TextMessage msg = new TextMessage();
                        msg.setFrom(from);
                        msg.setTarget(to);
                        msg.setContent(content);
                        msg.setCreateTime(dateTime * 1000);
                        msg.setType(msgType);
                        KLog.e("message", StrUtils.toJson(msg));

                    }
                });
    }
}
