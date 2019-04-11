package com.cc.core.wechat.hook;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.VersionedPackage;
import android.os.Build;

import com.cc.core.ApplicationContext;
import com.cc.core.log.KLog;
import com.cc.core.xposed.BaseXposedHook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class AvoidRiskHooks extends BaseXposedHook {
    public static final ThreadLocal<Boolean> shouldShowStackTrace = new ThreadLocal<>();
    public static boolean isPrintingStackTrace;

    private static List<String> riskPackages;
    private static List<String> classPrefixHiddenList;
    private static List<String> threadPrefixHiddenList;
    private static List<String> libHiddenFiles;

    static {
        riskPackages = new ArrayList<>();
        riskPackages.add("eu.chainfire.supersu");
        riskPackages.add("com.joeykrim.rootcheck");
        riskPackages.add("de.robv.android.xposed.installer");
        riskPackages.add(ApplicationContext.PACKAGE_NAME);

        classPrefixHiddenList = new ArrayList<>();
        classPrefixHiddenList.add("de.robv.android.xposed");
        classPrefixHiddenList.add(ApplicationContext.PACKAGE_NAME);

        threadPrefixHiddenList = new ArrayList<>();
        classPrefixHiddenList.add("de.robv.android.xposed");
        threadPrefixHiddenList.add(ApplicationContext.PACKAGE_NAME);

        libHiddenFiles = new ArrayList<>();
        libHiddenFiles.add("/data/dalvik-cache/arm/data@app@com.cc.wechatmanager-0@base.apk@classes.dex");
        libHiddenFiles.add("/data/dalvik-cache/arm/data@app@com.cc.wechatmanager-1@base.apk@classes.dex");
        libHiddenFiles.add("/data/dalvik-cache/arm/data@app@com.cc.wechatmanager-2@base.apk@classes.dex");
        libHiddenFiles.add("/data/dalvik-cache/arm/system@framework@XposedBridge.jar@classes.dex".toLowerCase());
        libHiddenFiles.add("/system/bin/app_process32_xposed");
        libHiddenFiles.add("/system/lib/libxposed_art.so");
        libHiddenFiles.add("/system/bin/app_process32_xposed");


    }

    private void hideRiskPackagesFromPackageManager(ClassLoader classLoader) {
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getInstalledPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<PackageInfo> packageInfos = (List<PackageInfo>) param.getResult();
                    for (PackageInfo packageInfo : packageInfos.toArray(new PackageInfo[0])) {
                        if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                            packageInfos.remove(packageInfo);
                        }
                    }

                    param.setResult(packageInfos);
                }
            }
        });
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getPreferredPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<PackageInfo> packageInfos = (List<PackageInfo>) param.getResult();
                    for (PackageInfo packageInfo : packageInfos.toArray(new PackageInfo[0])) {
                        if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                            packageInfos.remove(packageInfo);
                        }
                    }

                    param.setResult(packageInfos);
                }
            }
        });
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getInstalledApplications", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                /*List<ApplicationInfo> packageInfos = (List<ApplicationInfo>) param.getResult();
                for (PackageInfo packageInfo : packageInfos.toArray(new PackageInfo[0])) {
                    if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                        packageInfos.remove(packageInfo);
                    }
                }*/
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<ApplicationInfo> applicationInfos = (List<ApplicationInfo>) param.getResult();
                    for (ApplicationInfo applicationInfo : applicationInfos.toArray(new ApplicationInfo[0])) {
                        if (riskPackages.contains(applicationInfo.packageName.toLowerCase())) {
                            applicationInfos.remove(applicationInfo);
                        }
                    }

                    param.setResult(applicationInfos);
                }
            }
        });
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getPackagesForUid", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<String> newPackageNames = new ArrayList<>();
                    String[] packageNames = (String[]) param.getResult();
                    for (String packageName : packageNames) {
                        if (!riskPackages.contains(packageName.toLowerCase())) {
                            newPackageNames.add(packageName);
                        }
                    }

                    param.setResult(newPackageNames.toArray(new String[0]));
                }
            }
        });
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getPackageInfo", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    PackageInfo packageInfo = (PackageInfo) param.getResult();
                    if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                        param.setThrowable(new PackageManager.NameNotFoundException(packageInfo.packageName));
                    }
                }
            }
        });
        hookMethod("android.app.ApplicationPackageManager", classLoader, "getApplicationInfo", String.class, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    ApplicationInfo applicationInfo = (ApplicationInfo) param.getResult();
                    if (riskPackages.contains(applicationInfo.packageName.toLowerCase())) {
                        param.setResult(null);
                    }
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hookMethod("android.app.ApplicationPackageManager", classLoader, "getPackageInfoAsUser",  String.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable() && param.getResult() != null) {
                        PackageInfo packageInfo = (PackageInfo) param.getResult();
                        if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                            param.setThrowable(new PackageManager.NameNotFoundException(packageInfo.packageName));
                        }
                    }
                }
            });
            hookMethod("android.app.ApplicationPackageManager", classLoader, "getApplicationInfoAsUser", String.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable() && param.getResult() != null) {
                        ApplicationInfo applicationInfo = (ApplicationInfo) param.getResult();
                        if (riskPackages.contains(applicationInfo.packageName.toLowerCase())) {
                            param.setResult(null);
                        }
                    }
                }
            });
            hookMethod("android.app.ApplicationPackageManager", classLoader, "getPackageInfo", VersionedPackage.class, int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable() && param.getResult() != null) {
                        PackageInfo packageInfo = (PackageInfo) param.getResult();
                        if (riskPackages.contains(packageInfo.packageName.toLowerCase())) {
                            param.setThrowable(new PackageManager.NameNotFoundException(packageInfo.packageName));
                        }
                    }
                }
            });
            hookMethod("android.app.ApplicationPackageManager", classLoader, "getChangedPackages", int.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable() && param.getResult() != null) {
                        ChangedPackages changedPackages = (ChangedPackages) param.getResult();
                        for (String packageName : changedPackages.getPackageNames().toArray(new String[0])) {
                            if (riskPackages.contains(packageName.toLowerCase())) {
                                changedPackages.getPackageNames().remove(packageName);
                            }
                        }

                        param.setResult(changedPackages);
                    }
                }
            });
        }
    }

    private void hideRiskPackagesFromActivityManager(ClassLoader classLoader) {
        hookMethod("android.app.ActivityManager", classLoader, "getRunningServices", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<ActivityManager.RunningServiceInfo> runningServiceInfos = (List<ActivityManager.RunningServiceInfo>) param.getResult();
                    for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfos.toArray(new ActivityManager.RunningServiceInfo[0])) {
                        if (runningServiceInfo.service != null && runningServiceInfo.service.getPackageName() != null && riskPackages.contains(runningServiceInfo.service.getPackageName().toLowerCase())) {
                            runningServiceInfos.remove(runningServiceInfo);
                        }
                    }

                    param.setResult(runningServiceInfos);
                }
            }
        });
        hookMethod("android.app.ActivityManager", classLoader, "getRunningAppProcesses", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = (List<ActivityManager.RunningAppProcessInfo>) param.getResult();
                    outerLoop:
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos.toArray(new ActivityManager.RunningAppProcessInfo[0])) {
                        String lowerCaseProcessName = runningAppProcessInfo.processName.toLowerCase();
                        for (String packageNameHidden : riskPackages) {
                            if (lowerCaseProcessName.contains(packageNameHidden)) {
                                runningAppProcessInfos.remove(runningAppProcessInfo);

                                continue outerLoop;
                            }
                        }
                    }

                    param.setResult(runningAppProcessInfos);
                }
            }
        });

        hookMethod("android.app.ActivityManager", classLoader, "getRunningTasks", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!param.hasThrowable() && param.getResult() != null) {
                    List<ActivityManager.RunningTaskInfo> runningTaskInfos = (List<ActivityManager.RunningTaskInfo>) param.getResult();
                    for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos.toArray(new ActivityManager.RunningTaskInfo[0])) {
                        if (riskPackages.contains(runningTaskInfo.baseActivity.getPackageName().toLowerCase())) {
                            runningTaskInfos.remove(runningTaskInfo);
                        }
                    }

                    param.setResult(runningTaskInfos);
                }
            }
        });
    }

    private void hideExceptionStackTrace(ClassLoader classLoader) {
        hookMethod("java.lang.Throwable", classLoader, Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? "getOurStackTrace" : "getInternalStackTrace", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.getResult() != null) {
                    param.setResult(removeHiddenClasses((StackTraceElement[]) param.getResult()));
                }
            }
        });
        hookMethod("java.lang.Throwable", classLoader, "getStackTrace", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.getResult() != null) {
                    param.setResult(removeHiddenClasses((StackTraceElement[]) param.getResult()));
                }
            }
        });
        hookMethod("java.lang.Thread", classLoader, "getAllStackTraces", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.getResult() != null) {
                    Map<Thread, StackTraceElement[]> allThreads = (Map<Thread, StackTraceElement[]>) param.getResult();
                    for (Thread thread : allThreads.keySet().toArray(new Thread[0])) {
                        if (shouldHideThread(thread.getName())) {
                            allThreads.remove(thread);
                        }
                    }

                    param.setResult(allThreads);
                }
            }
        });

    }

    private void hideFiles(ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("java.io.File", classLoader, "exists", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                final String path = (String) XposedHelpers.getObjectField(param.thisObject, "path");
                if (path != null) {
                    String lowerCasePath = path.toLowerCase();
                    if (lowerCasePath.endsWith("/su") || (lowerCasePath.contains("xposed")
                            && !lowerCasePath.contains("exposed"))) {
                        param.setResult(false);
                    }
                }
            }
        });
    }

    private static boolean shouldHideThread(String threadName) {
        if (threadName != null) {
            for (String threadPrefix : threadPrefixHiddenList) {
                if (threadName.startsWith(threadPrefix)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static StackTraceElement[] removeHiddenClasses(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return stackTraceElements;
        }

        if (isPrintingStackTrace) {
            return stackTraceElements;
        }

        Boolean shouldShowStackTrace = AvoidRiskHooks.shouldShowStackTrace.get();
        if (shouldShowStackTrace != null && shouldShowStackTrace) {
            return stackTraceElements;
        }

        List<StackTraceElement> newStackTraceElements = new ArrayList<>();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement != null && stackTraceElement.getClassName() != null) {
                boolean shouldHide = false;

                if ("<XposedCaller>".equals(stackTraceElement.getFileName())) {
                    shouldHide = true;
                } else {
                    String className = stackTraceElement.getClassName();
                    if (className != null) {
                        for (String classPrefix : classPrefixHiddenList) {
                            if (className.startsWith(classPrefix)) {
                                shouldHide = true;

                                break;
                            }
                        }
                    }
                }

                if (shouldHide) {
                    continue;
                }
            }

            newStackTraceElements.add(stackTraceElement);
        }

        return newStackTraceElements.toArray(new StackTraceElement[0]);
    }

    @Override
    public void hook(ClassLoader classLoader) {
        hideRiskPackagesFromPackageManager(classLoader);
        hideRiskPackagesFromActivityManager(classLoader);
        hideExceptionStackTrace(classLoader);
        hideFiles(classLoader);
    }
}
