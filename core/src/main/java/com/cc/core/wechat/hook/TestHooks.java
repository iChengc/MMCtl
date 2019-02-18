package com.cc.core.wechat.hook;

import android.content.Context;
import android.content.Intent;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TestHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {

        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, int.class, List.class, List.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, int.class, List.class, List.class, List.class, String.class, String.class, Map.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, String.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.m", classLoader, String.class, String.class, int.class, byte.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("=====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.brandservice.b.h", classLoader, String.class, long.class, int.class, int.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object o : param.args) {
                    KLog.e("==== brandservice ====>>>>>> " + StrUtils.toJson(o));
                }

            }
        });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.pluginsdk.model.j", classLoader,
                Context.class, List.class, Intent.class, String.class, int.class, XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.j$a", Wechat.WECHAT_CLASSLOADER),
        new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                for (Object f : param.args) {
                    KLog.e("======>>>>>>  ", f == null ? "null" : f.toString());
                }
                KLog.e("======>>>>>>  ", new Exception());
            }
        });

        XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "startVideoStreamingDownload",
                 XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER), int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====+++++==>>>>>>  ", param.args[1] + "  " + StrUtils.toJson(param.args[0]));
                    }
                });
    }
}
