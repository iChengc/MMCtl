package com.cc.core.wechat.hook.tool;

import android.util.Log;

import com.cc.core.log.KLog;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XLogHooks extends BaseXposedHook {
    private XC_MethodHook.Unhook logVerboseUnhook;
    private XC_MethodHook.Unhook logDebugUnhook;
    private XC_MethodHook.Unhook logInfoUnhook;
    private XC_MethodHook.Unhook logWarnUnhook;
    private XC_MethodHook.Unhook logErrorUnhook;
    /*private XC_MethodHook.Unhook logDebugWithStackUnhook;
    private XC_MethodHook.Unhook logInfoWithStackUnhook;
    private XC_MethodHook.Unhook logErrorWithStackUnhook;
    private XC_MethodHook.Unhook getLogLevelUnhook;*/

    public void hook(ClassLoader classLoader) {
        if (logVerboseUnhook == null) {
            logVerboseUnhook = findAndHookMethod(Wechat.HookMethodFunctions.LOGGER, classLoader, "v", String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.VERBOSE, param.args);
                }
            });
        }

        if (logDebugUnhook == null) {
            logDebugUnhook = findAndHookMethod(Wechat.HookMethodFunctions.LOGGER, classLoader, "d", String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.DEBUG, param.args);
                }
            });
        }

        if (logInfoUnhook == null) {
            logInfoUnhook = findAndHookMethod(Wechat.HookMethodFunctions.LOGGER, classLoader, "i", String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.INFO, param.args);
                }
            });
        }

        if (logWarnUnhook == null) {
            logWarnUnhook = findAndHookMethod(Wechat.HookMethodFunctions.LOGGER, classLoader, "w", String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.WARN, param.args);
                }
            });
        }

        if (logErrorUnhook == null) {
            logErrorUnhook = findAndHookMethod(Wechat.HookMethodFunctions.LOGGER, classLoader, "e", String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.ERROR, param.args);
                }
            });
        }

        /*if (logDebugWithStackUnhook == null) {
            logDebugWithStackUnhook = findAndHookMethod(VersionParam.LoggerClass, classLoader, VersionParam.LoggerDebugWithStackFunc, String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.DEBUG, param.args);
                }
            });
        }

        if (logInfoWithStackUnhook == null) {
            logInfoWithStackUnhook = findAndHookMethod(VersionParam.LoggerClass, classLoader, VersionParam.LoggerInfoWithStackFunc, String.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.INFO, param.args);
                }
            });
        }

        if (logErrorWithStackUnhook == null) {
            logErrorWithStackUnhook = findAndHookMethod(VersionParam.LoggerClass, classLoader, VersionParam.LoggerPrintErrStackTraceFunc, String.class, Throwable.class, String.class, Object[].class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    log(Log.ERROR, param.args);
                }
            });
        }

        if (getLogLevelUnhook == null) {
            getLogLevelUnhook = findAndHookMethod(VersionParam.LoggerClass, classLoader, VersionParam.LoggerGetLogLevelFunc, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (DiagnosticUtil.getWechatToDebugEnv()) {
                        param.setResult(1);
                    }
                }
            });
        }*/
    }

    public void unhook() {
        if (logVerboseUnhook != null) {
            logVerboseUnhook.unhook();
            logVerboseUnhook = null;
        }

        if (logDebugUnhook != null) {
            logDebugUnhook.unhook();
            logDebugUnhook = null;
        }

        if (logInfoUnhook != null) {
            logInfoUnhook.unhook();
            logInfoUnhook = null;
        }

        if (logWarnUnhook != null) {
            logWarnUnhook.unhook();
            logWarnUnhook = null;
        }

        if (logErrorUnhook != null) {
            logErrorUnhook.unhook();
            logErrorUnhook = null;
        }

        /*if (logDebugWithStackUnhook != null) {
            logDebugWithStackUnhook.unhook();
            logDebugWithStackUnhook = null;
        }

        if (logInfoWithStackUnhook != null) {
            logInfoWithStackUnhook.unhook();
            logInfoWithStackUnhook = null;
        }

        if (logErrorWithStackUnhook != null) {
            logErrorWithStackUnhook.unhook();
            logErrorWithStackUnhook = null;
        }

        if (getLogLevelUnhook != null) {
            getLogLevelUnhook.unhook();
            getLogLevelUnhook = null;
        }*/
    }

    private void log(int level, Object[] args) {
        String tag = (String) args[0];
        if ("WxSplash.SplashHackHandlerCallback".equals(tag)) {
            return;
        }

        String format;
        Throwable throwable = null;
        Object[] params;
        if (args.length == 3) {
            format = (String) args[1];
            params = (Object[]) args[2];
        } else {
            throwable = (Throwable) args[1];
            format = (String) args[2];
            params = (Object[]) args[3];
        }

        String log;
        try {
            log = tag + ": " + (params == null ? format : String.format(format, params));
            if (throwable != null) {
                log += "  " + Log.getStackTraceString(throwable);
            }
        } catch (Exception e) {
            log = tag + ": " + args[1] + ", error to format wechat exception: " + e.toString();
        }

        switch (level) {
            case Log.VERBOSE:
                KLog.v(log);
                break;
            case Log.DEBUG:
                KLog.d(log);
                break;
            case Log.INFO:
                KLog.i(log);
                break;
            case Log.WARN:
                KLog.w(log);
                break;
            case Log.ERROR:
                KLog.e(log);
                break;
        }
    }
}
