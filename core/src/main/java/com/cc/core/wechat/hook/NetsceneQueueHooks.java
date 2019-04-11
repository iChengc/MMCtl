package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.xposed.BaseXposedHook;

import static com.cc.core.wechat.Wechat.Hook.NetScene.*;

import de.robv.android.xposed.XC_MethodHook;

public class NetsceneQueueHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(NetSceneQueueClass, classLoader, NetSceneEnqueueFunc, findClass(NetSceneRequestClass, classLoader), int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e("---->>>> enqueue net scene <<<<-----", param.args[1] + "----" + param.args[0].getClass().getName(), new Exception());

                super.beforeHookedMethod(param);
            }
        });
    }
}
