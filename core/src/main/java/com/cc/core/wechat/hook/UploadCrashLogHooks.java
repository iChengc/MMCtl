package com.cc.core.wechat.hook;

import android.content.Intent;

import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import org.json.JSONArray;

import java.util.concurrent.Callable;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;

import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;

public class UploadCrashLogHooks extends BaseXposedHook {
    private static final String TAG = UploadCrashLogHooks.class.getSimpleName();

    @Override
    public void hook(final ClassLoader classLoader) {
        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashLogClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashLogFunc,
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String path = (String) getStaticObjectField(classLoader.loadClass(Wechat.HookMethodFunctions.ConstantsStorage), Wechat.HookMethodFunctions.WechatStorageCrashPath);
                        String crashLogFile = path + "crash/" + param.args[0] + "." + param.args[1] + ".crashlog";
                        // 删除crash日志文件，禁止上报
                        FileUtil.deleteFile(crashLogFile);
                    }
                });
        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashWXRecoveryUploadServiceClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashWXRecoveryUploadServicePushDataFunc,
                JSONArray.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.d(TAG, "WXRecoveryUploadService: " + StrUtils.toJson(param.args[0]));
                        // 清除上报的日志
                        param.args[0] = new JSONArray();
                    }
                });
        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashLogEnumClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashLogEnumFunc,
                String.class,
                Callable.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Callable callable = (Callable) param.args[1];
                        final String data = StrUtils.toJson(callable.call());

                        // 清除上报的日志
                        param.args[0] = "";
                    }
                });

        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashCrashUploaderServiceClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashCrashUploaderServiceOnHandleIntentFunc,
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.d(TAG, "CrashUploaderService");
                        // 清除上报的日志
                        param.args[0] = null;
                    }
                });
        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashTraceRouteClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashTraceRouteFunc,
                findClass(Wechat.HookMethodFunctions.UploadCrashTraceRouteClass, classLoader),
                String.class,
                byte[].class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                        KLog.d(TAG, "trace route: " + param.args[1]);
                        return null;
                    }
                });
        hookMethod(
                Wechat.HookMethodFunctions.UploadCrashStackReportUploaderClass,
                classLoader,
                Wechat.HookMethodFunctions.UploadCrashStackReportUploaderFunc,
                byte[].class,
                String.class,
                String.class,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        KLog.d(TAG, "StackReportUploader");
                        // 清除上报的日志
                        param.args[0] = new byte[0];
                    }
                });
        hookMethod(
                Wechat.HookMethodFunctions.SaveAnrWatchDogClass,
                classLoader,
                Wechat.HookMethodFunctions.SaveAnrWatchDogSetHandlerFunc,
                findClass(Wechat.HookMethodFunctions.SaveAnrWatchDogHandlerClass, classLoader),
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                        if (param.args[0] != null && !param.args[0].getClass().getName().equals(Wechat.HookMethodFunctions.MMCrashReporter)) {
                            KLog.d(TAG, "set anr watch handler: " + param.args[0]);
                        }

                        return null;
                    }
                });
    }
}
