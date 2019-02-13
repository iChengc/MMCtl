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
    private static Map<Object, OnSceneEndListener> listeners = new HashMap<>();
    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(NetSceneQueueClass, classLoader, NetSceneEnqueueFunc, findClass(NetSceneRequestClass, classLoader), int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                KLog.e("---->>>> enqueue net scene <<<<-----", param.args[1] + "----" + param.args[0].getClass().getName(), new Exception());
                KLog.e("---->>>> enqueue net scene <<<<-----", StrUtils.toJson(param.args[0]));

                super.beforeHookedMethod(param);
            }
        });

        hookMethod(NetSceneQueueClass, classLoader, NetSceneEndFunc, int.class, int.class, String.class, findClass(NetSceneRequestClass, classLoader), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Object request = param.args[param.args.length - 1];
                    OnSceneEndListener listener = listeners.remove(request);


                    Field fr = request.getClass().getField("dUE");
                    if (fr != null) {
                        Object o = fr.get(request);
                        Field[] filelds = o.getClass().getFields();
                        for (Field f : filelds) {
                            KLog.e("<<<<==== response ++++ ====>>>>>> " + f.getName() + "  " + f.get(o));
                        }

                        KLog.e("<<<<==== response ++++ ====>>>>>> " + StrUtils.toJson(o));
                    }
                    if (listener != null) {
                        listener.onSceneEnd((Integer) param.args[0], (Integer) param.args[1], (String) param.args[2]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.beforeHookedMethod(param);
            }
        });
    }

    public static void registerSceneListener(Object request, OnSceneEndListener l) {
        listeners.put(request, l);
    }

    public interface OnSceneEndListener {
        void onSceneEnd(int type, int code, String message);
    }
}
