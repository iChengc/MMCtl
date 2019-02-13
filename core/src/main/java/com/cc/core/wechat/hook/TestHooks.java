package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
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

        hookMethod("com.tencent.mm.plugin.messenger.a.f", classLoader, "a", int.class, int.class, int.class, String.class,
                XposedHelpers.findClass("com.tencent.mm.network.q", classLoader), byte[].class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Field[] filelds = param.thisObject.getClass().getFields();
                        for (Field f : filelds) {
                            KLog.e("<<<<==== response ====>>>>>> " + f.getName() + "  " + f.get(param.thisObject));
                        }
                    }
                });
    }
}
