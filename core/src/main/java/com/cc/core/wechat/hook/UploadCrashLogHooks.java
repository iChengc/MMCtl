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
                Wechat.Hook.UploadCrashLogClass,
                classLoader,
                Wechat.Hook.UploadCrashLogFunc,
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String path = (String) getStaticObjectField(classLoader.loadClass(Wechat.Hook.ConstantsStorage), Wechat.Hook.WechatStorageCrashPath);
                        String crashLogFile = path + "crash/" + param.args[0] + "." + param.args[1] + ".crashlog";
                        // 删除crash日志文件，禁止上报
                        FileUtil.deleteFile(crashLogFile);
                    }
                });
        hookMethod(
                Wechat.Hook.UploadCrashWXRecoveryUploadServiceClass,
                classLoader,
                Wechat.Hook.UploadCrashWXRecoveryUploadServicePushDataFunc,
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
                Wechat.Hook.UploadCrashLogEnumClass,
                classLoader,
                Wechat.Hook.UploadCrashLogEnumFunc,
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
                Wechat.Hook.UploadCrashCrashUploaderServiceClass,
                classLoader,
                Wechat.Hook.UploadCrashCrashUploaderServiceOnHandleIntentFunc,
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
                Wechat.Hook.UploadCrashTraceRouteClass,
                classLoader,
                Wechat.Hook.UploadCrashTraceRouteFunc,
                findClass(Wechat.Hook.UploadCrashTraceRouteClass, classLoader),
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
                Wechat.Hook.UploadCrashStackReportUploaderClass,
                classLoader,
                Wechat.Hook.UploadCrashStackReportUploaderFunc,
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
                Wechat.Hook.SaveAnrWatchDogClass,
                classLoader,
                Wechat.Hook.SaveAnrWatchDogSetHandlerFunc,
                findClass(Wechat.Hook.SaveAnrWatchDogHandlerClass, classLoader),
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(final MethodHookParam param) throws Throwable {
                        if (param.args[0] != null && !param.args[0].getClass().getName().equals(Wechat.Hook.MMCrashReporter)) {
                            KLog.d(TAG, "set anr watch handler: " + param.args[0]);
                        }

                        return null;
                    }
                });
    }
}
