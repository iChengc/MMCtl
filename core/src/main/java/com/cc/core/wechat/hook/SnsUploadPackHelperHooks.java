package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.xposed.BaseXposedHook;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class SnsUploadPackHelperHooks extends BaseXposedHook {
    private static final String n = "com.tencent.mm.plugin.sns.model.ax";
    @Override
    public void hook(@NotNull ClassLoader classLoader) {
        hookMethod(n, classLoader, "MI", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MI <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MJ", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MJ <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MK", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MK <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "ML", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#ML <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MM", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MM <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MN", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MN <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MO", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MO <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MP", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MP <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MQ", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MQ <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "MR", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#MR <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "X", String.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#X <<<<<", param.args[0].toString() + " : " + param.args[1].toString() + " : " + param.args[2].toString());
            }
        });
        hookMethod(n, classLoader, "a", findClass("com.tencent.mm.protocal.c.atd", classLoader), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#a <<<<<", StrUtils.toJson(param.args[0]));
            }
        });
        hookMethod(n, classLoader, "ah", LinkedList.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#ah <<<<<", StrUtils.toJson(param.args[0]));
            }
        });
        hookMethod(n, classLoader, "ct", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#ct <<<<<", StrUtils.toJson(param.args[0]));
            }
        });
        hookMethod(n, classLoader, "cu", List.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#cu <<<<<", StrUtils.toJson(param.args[0]));
            }
        });
        hookMethod(n, classLoader, "eE", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#eE <<<<<", StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
            }
        });
        hookMethod(n, classLoader, "eG", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#eG <<<<<", StrUtils.toJson(param.args[0]) + " : " + StrUtils.toJson(param.args[1]));
            }
        });
        hookMethod(n, classLoader, "f", String.class, String.class, String.class, int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#f <<<<<", param.args[0] + " : " + param.args[1]
                        + " : " + param.args[2] + " : " + param.args[3] + " : " + param.args[4]);
            }
        });
        hookMethod(n, classLoader, "q", String.class, String.class, String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#q <<<<<", param.args[0] + " : " + param.args[1]
                      + " : " + param.args[2] + " : " + param.args[3]);
            }
        });
        hookMethod(n, classLoader, "setSessionId", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#setSessionId <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xf", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xf <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xg", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xg <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xh", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xh <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xi", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xi <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xj", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xj <<<<<", param.args[0].toString());
            }
        });
        hookMethod(n, classLoader, "xk", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e(">>>> UploadPackHelper#xk <<<<<", param.args[0].toString());
            }
        });
    }
}
