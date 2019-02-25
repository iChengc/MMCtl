package com.cc.core.wechat.hook;

import com.cc.core.wechat.MessageUtils;
import com.cc.core.wechat.WeChatMessageType;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static com.cc.core.wechat.Wechat.Hook.Message.MessageSyncExtensionClass;
import static com.cc.core.wechat.Wechat.Hook.Message.MessageSyncExtensionProcessCommonMessageFunc;
import static com.cc.core.wechat.Wechat.Hook.Message.ProtocolAddMsgInfoClass;
import static com.cc.core.wechat.Wechat.Hook.Message.SyncMessageNotifierClass;

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
                                Wechat.Hook.Message.MessageInfoFieldId);
                        MessageUtils.Companion.receiveNewMessage(messageInfo);
                    }

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object messageInfo = XposedHelpers.getObjectField(param.args[0],
                                Wechat.Hook.Message.MessageInfoFieldId);
                        int msgType = XposedHelpers.getIntField(messageInfo,
                                Wechat.Hook.Message.MessageTypeFieldId);
                        if (msgType != WeChatMessageType.CANCELABLE_GROUP_OPERATION) {
                            return;
                        }

                        MessageUtils.Companion.processOperationMessage(messageInfo);
                    }
                });
    }

}
