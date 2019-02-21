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
                        MessageUtils.Companion.receiveNewMessage(messageInfo);
                    }
                });
    }

}
