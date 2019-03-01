package com.cc.core.wechat.hook;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.cc.core.ApplicationContext;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import org.jetbrains.annotations.NotNull;

import de.robv.android.xposed.XC_MethodHook;

public class AvoidUpdateHooks extends BaseXposedHook {
    @Override
    public void hook(@NotNull ClassLoader classLoader) {
        hookMethod("com.tencent.mm.plugin.webview.stub.WebViewStubProxyUI", Wechat.WECHAT_CLASSLOADER, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if ( param.thisObject instanceof Activity) {
                    Activity activity = (Activity) param.thisObject;
                    activity.finish();
                    Toast.makeText(ApplicationContext.application(), "更新已被禁止", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
