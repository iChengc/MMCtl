package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.xposed.BaseXposedHook;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.*;

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

       /* hookMethod(NetSceneQueueClass, classLoader, NetSceneEndFunc, int.class, int.class, String.class, findClass(NetSceneRequestClass, classLoader), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Object request = param.args[param.args.length - 1];
                    OnSceneEndListener listener = listeners.remove(request);

                    if (listener != null) {
                        listener.onSceneEnd((Integer) param.args[0], (Integer) param.args[1], (String) param.args[2]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.beforeHookedMethod(param);
            }
        });*/
    }
}
