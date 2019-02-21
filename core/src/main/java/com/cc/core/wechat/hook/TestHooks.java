package com.cc.core.wechat.hook;

import android.content.Context;
import android.content.DialogInterface;
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

        XposedHelpers.findAndHookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "startC2CDownload",
                XposedHelpers.findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadRequest", Wechat.WECHAT_CLASSLOADER),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++startC2CDownload++==>>>>>>  ", param.args[0] + "  " + StrUtils.toJson(param.args[0]), new Exception());
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.chatroom.c.g", classLoader, String.class, List.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        KLog.e("====++Create group++==>>>>>>  ", param.args[0]
                                + "  " + param.args[1], new Exception());
                    }
                });
        /**
         * l(int paramInt1, int paramInt2)
         *
         *
         *   public l(int paramInt1, int paramInt2, byte paramByte)
         *
         *
         *   public l(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4)
         *
         *
         *   public l(int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, final com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4, int paramInt5, float paramFloat1, float paramFloat2)
         *
         *
         *   public l(long paramLong, String paramString1, String paramString2, String paramString3, int paramInt1, final com.tencent.mm.ah.g paramG, int paramInt2, String paramString4, String paramString5, int paramInt3)
         *
         *   public l(String paramString1, String paramString2, String paramString3, int paramInt)
         *   {
         *     this(paramString1, paramString2, paramString3, paramInt, "", "");
         *   }
         *
         *   public l(String paramString1, String paramString2, String paramString3, int paramInt1, com.tencent.mm.ah.g paramG, int paramInt2, a paramA, int paramInt3)
         *   {
         */

        // int paramInt1, String paramString1, String paramString2, String paramString3, int paramInt2, com.tencent.mm.ah.g paramG, int paramInt3, String paramString4, String paramString5, boolean paramBoolean, int paramInt4
        /*XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                XposedHelpers.findClass("com.tencent.mm.as.l$a", Wechat.WECHAT_CLASSLOADER), int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" : o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, String.class, String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" : o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, long.class, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.as.l", classLoader, int.class, String.class, String.class, String.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ah.g", Wechat.WECHAT_CLASSLOADER), int.class,
                String.class, String.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });*/
        /*XposedHelpers.findAndHookMethod("com.tencent.mm.ap.d", classLoader, "a", long.class, long.class, int.class, Object.class,
                int.class, XposedHelpers.findClass("com.tencent.mm.ap.d$a", Wechat.WECHAT_CLASSLOADER), int.class,

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });
        XposedHelpers.findAndHookConstructor("com.tencent.mm.ap.k", classLoader, long.class, long.class, int.class,
                XposedHelpers.findClass("com.tencent.mm.af.g", Wechat.WECHAT_CLASSLOADER), int.class,

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        for (Object o : param.args) {
                            KLog.e("====+++++==>>>>>>  ", o == null ? "null" :  o.toString());
                        }
                    }
                });*/
    }
}
