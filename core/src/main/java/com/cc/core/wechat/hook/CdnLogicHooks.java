package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.utils.StrUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;
import de.robv.android.xposed.XC_MethodHook;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class CdnLogicHooks extends BaseXposedHook {
    private static Map<String, CdnDownloadFinishListener> mCdnDownloadListeners = new HashMap<>();
    @Override public void hook(@NotNull ClassLoader classLoader) {
        hookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "onDownloadToEnd", String.class, int.class, int.class,
            new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    CdnDownloadFinishListener listner = mCdnDownloadListeners.remove(param.args[0].toString());
                    if (listner != null) {
                        listner.onFinishDownload(param.args[0].toString());
                    }
                }
            });

        hookMethod("com.tencent.mars.cdn.CdnLogic", classLoader, "onC2CDownloadCompleted", String.class, findClass("com.tencent.mars.cdn.CdnLogic$C2CDownloadResult", Wechat.WECHAT_CLASSLOADER),
            new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    KLog.e("onC2CDownloadCompleted =====>>>>>>>", param.args[0] + "=====" + StrUtils.toJson(param.args[1]));
                    CdnDownloadFinishListener listner = mCdnDownloadListeners.remove(param.args[0].toString());
                    if (listner != null) {
                        listner.onFinishDownload(param.args[0].toString());
                    }
                }
            });
    }

    public static void registerDownloadListeners(String fileKey, CdnDownloadFinishListener listener) {
        mCdnDownloadListeners.put(fileKey, listener);
    }

    public interface CdnDownloadFinishListener {
        void onFinishDownload(String fileKey);
    }
}
