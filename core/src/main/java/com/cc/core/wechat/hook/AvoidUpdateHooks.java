package com.cc.core.wechat.hook;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.cc.core.ApplicationContext;
import com.cc.core.WorkerHandler;
import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.HookUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

public class AvoidUpdateHooks extends BaseXposedHook {
    @Override
    public void hook(@NotNull final ClassLoader classLoader) {
        hookMethod("com.tencent.mm.plugin.webview.stub.WebViewStubProxyUI", Wechat.WECHAT_CLASSLOADER, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.thisObject instanceof Activity) {
                    Activity activity = (Activity) param.thisObject;
                    Intent i = activity.getIntent();
                    if (i.hasExtra("update_type_key")) {
                        activity.finish();
                        Toast.makeText(ApplicationContext.application(), "更新已被禁止", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        hookMethod("com.tencent.mm.ui.MMAppMgr$Receiver", Wechat.WECHAT_CLASSLOADER, "onReceive", Context.class, Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent i = (Intent) param.args[1];
                        if ("com.tencent.mm.sandbox.updater.intent.ACTION_UPDATE".equals(i.getAction())) {
                            HookUtils.Companion.deleteUpdatedAPkFile();
                        }
                    }
                });

        WorkerHandler.postOnWorkThreadDelayed(new Runnable() {
            @Override public void run() {
                HookUtils.Companion.deleteUpdatedAPkFile();
            }
        }, 5000);
    }
}
