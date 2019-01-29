package com.cc.core.wechat;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

import com.cc.core.MyApplication;
import com.cc.core.log.KLog;
import com.cc.core.rpc.Rpc;
import com.cc.core.xposed.BaseXposedHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Wechat {
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static List<BaseXposedHook> hooks = new ArrayList<>();

    public static String LoginWechatId;

    private void Wechat() {
    }

    static {
        loadHooks();
    }

    public static ClassLoader WECHAT_CLASSLOADER;
    public static String DB_PATH;
    public static String DB_PASSWORD;

    private static void loadHooks() {
        try {
            for (String name : WechatHooks.HOOKS) {
                addHook(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void start(XC_LoadPackage.LoadPackageParam lpparam) {
        if (MyApplication.application() != null) {
            KLog.e(">>>>已经Hook微信，无需再hook");
            return;
        }
        XposedBridge.log(">>开始hook微信主进程");
        WECHAT_CLASSLOADER = lpparam.classLoader;
        MyApplication.init(AndroidAppHelper.currentApplication());
        for (BaseXposedHook h : hooks) {
            h.hook(lpparam.classLoader);
        }
        Rpc.asRpcServer();
        XposedBridge.log("---->>结束hook微信主进程");
    }

    public static void initEnvironment(String packageName) {
        try {
            Context context = MyApplication.application();
            if (context == null) {
                context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            }
            String versionName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;

            HookMethodFunctions.init(versionName);
            Resources.init(versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addHook(String className) throws Exception {
        Class hook = Class.forName(className);
        hooks.add((BaseXposedHook) hook.newInstance());
    }

    public static void startApp(Context context) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .setClassName(WECHAT_PACKAGE_NAME, Resources.LAUNCHER_UI_CLASS);
        context.startActivity(intent);
    }

    public static class HookMethodFunctions {

        public static String KernelClass ="";
        public static String LOGGER ="";
        public static String UploadCrashLogClass = "com.tencent.mm.sandbox.monitor.a";
        public static String UploadCrashLogFunc = "fV";
        public static String UploadCrashWXRecoveryUploadServiceClass = "com.tencent.recovery.wx.service.WXRecoveryUploadService";
        public static String UploadCrashWXRecoveryUploadServicePushDataFunc = "pushData";
        public static String UploadCrashLogEnumClass = "com.tencent.mm.plugin.report.service.h";
        public static String UploadCrashLogEnumFunc = "a";
        public static String UploadCrashCrashUploaderServiceClass = "com.tencent.mm.crash.CrashUploaderService";
        public static String UploadCrashCrashUploaderServiceOnHandleIntentFunc = "onHandleIntent";
        public static String UploadCrashTraceRouteClass = "com.tencent.mm.plugin.traceroute.b.a.f";
        public static String UploadCrashTraceRouteFunc = "a";
        public static String UploadCrashStackReportUploaderClass = "com.tencent.mm.platformtools.ae";
        public static String UploadCrashStackReportUploaderFunc = "a";
        public static String SaveAnrWatchDogClass = "com.tencent.mm.sdk.a.b";
        public static String SaveAnrWatchDogSetHandlerFunc = "a";
        public static String  SaveAnrWatchDogHandlerClass = "com.tencent.mm.sdk.a.c";
        public static String MMCrashReporterClass = "com.tencent.mm.app.k";
        public static String WechatStorageClass = "com.tencent.mm.storage.ac";
        public static String WechatStorageCrashPath = "dFK";
        public static void init(String version) {
            Sqlite.init(version);
            Account.init(version);switch (version) {
                case "7.0.0":
                    break;
                case "6.7.2":
                    KernelClass = "com.tencent.mm.kernel.g";
                    LOGGER = "com.tencent.mm.sdk.platformtools.y";
                    UploadCrashLogClass = "com.tencent.mm.sandbox.monitor.a";
                    UploadCrashLogFunc = "fV";
                    UploadCrashWXRecoveryUploadServiceClass = "com.tencent.recovery.wx.service.WXRecoveryUploadService";
                    UploadCrashWXRecoveryUploadServicePushDataFunc = "pushData";
                    UploadCrashLogEnumClass = "com.tencent.mm.plugin.report.service.h";
                    UploadCrashLogEnumFunc = "a";
                    UploadCrashCrashUploaderServiceClass = "com.tencent.mm.crash.CrashUploaderService";
                    UploadCrashCrashUploaderServiceOnHandleIntentFunc = "onHandleIntent";
                    UploadCrashTraceRouteClass = "com.tencent.mm.plugin.traceroute.b.a.f";
                    UploadCrashTraceRouteFunc = "a";
                    UploadCrashStackReportUploaderClass = "com.tencent.mm.platformtools.ae";
                    UploadCrashStackReportUploaderFunc = "a";
                    SaveAnrWatchDogClass = "com.tencent.mm.sdk.a.b";
                    SaveAnrWatchDogSetHandlerFunc = "a";
                    SaveAnrWatchDogHandlerClass = "com.tencent.mm.sdk.a.c";
                    MMCrashReporterClass = "com.tencent.mm.app.k";
                    WechatStorageClass = "com.tencent.mm.storage.ac";
                    WechatStorageCrashPath = "dFK";
                    break;
            }
        }

        public static class Account {
            public static String Account = "";
            public static String GetContactManagerFunc = "";
            public static String GetGroupManagerFunc = "";
            public static String GetConfigManagerFunc = "";
            public static String GetMsgInfoManagerFunc = "";
            public static String GetConversationManagerFunc = "";
            public static String ConfigStorageGetFunc = "";

            public static String GetContactInfoFunc = "";

            public static String RegionCodeDecoderClass = "";
            public static String RegionCodeDecoderEncodeFunc = "";

            public static int UserInfoId_WechatId = 0x0002;
            public static int UserInfoId_Alias = 0x002A;
            public static int UserInfoId_Nickname = 0x0004;
            public static int UserInfoId_Phone = 0x0006;
            public static int UserInfoId_Sex = 0x3002;
            public static int UserInfoId_Signature = 0x3003;
            public static int UserInfoId_CountryCode = 0x3024;
            public static int UserInfoId_ProvinceCode = 0x3025;
            public static int UserInfoId_CityCode = 0x3026;

            public static void init(String version) {
                switch (version) {
                    case "7.0.0":
                        RegionCodeDecoderClass = "com.tencent.mm.storage.RegionCodeDecoder";
                        break;
                    case "6.7.2":
                        Account = "com.tencent.mm.model.c";
                        GetContactManagerFunc = "EO";
                        GetGroupManagerFunc = "EX";
                        GetConfigManagerFunc = "CQ";
                        GetMsgInfoManagerFunc = "EQ";
                        GetConversationManagerFunc = "ET";
                        ConfigStorageGetFunc = "get";
                        RegionCodeDecoderClass = "com.tencent.mm.storage.RegionCodeDecoder";
                        RegionCodeDecoderEncodeFunc = "an";
                        GetContactInfoFunc = "ZQ";
                        break;
                }
            }
        }

        public static class Sqlite {
            public static String GetDBHelerFunc = "";
            public static String DBRawQueryFunc = "";
            public static String DBExecSqlFunc = "";
            public static String DbHelperField = "";

            public static void init(String version) {
                switch (version) {
                    case "7.0.0":
                        DBRawQueryFunc = "rawQuery";
                        break;
                    case "6.7.2":
                        GetDBHelerFunc = "Dg";
                        DBRawQueryFunc = "rawQuery";
                        DBExecSqlFunc = "gf";
                        DbHelperField = "dBo";
                        break;
                }
            }
        }
    }

    public static class Resources {
        // Webview activity's name
        public static final String LAUNCHER_UI_CLASS = "com.tencent.mm.ui.LauncherUI";
        public final static String WEBVIEW_ACTIVITY_CLASS_NAME = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
        public static String PROGRESS_DIALOG_CLASS_NAME = "";


        // 系统权限弹框节点
        public final static String NODE_SYSTEM_PERMISSION_MESSAGE = "com.android.packageinstaller:id/permission_message";
        public final static String NODE_SYSTEM_PERMISSION_DO_NOT_ASK = "com.android.packageinstaller:id/do_not_ask_checkbox";
        public final static String NODE_SYSTEM_PERMISSION_ALLOW = "com.android.packageinstaller:id/permission_allow_button";
        public final static String NODE_SYSTEM_PERMISSION_DENY = "com.android.packageinstaller:id/permission_deny_button";

        // 取消
        public final static String NODE_DIALOG_CONFIRM_CANCEL = "取消";
        // android手机anr等待按钮
        public final static String NODE_DIALOG_ANR_WAITING = "android:id/button2";

        public final static String NODE_ANDROID_LIST = "android:id/list";
        public final static String NODE_ANDROID_TITLE = "android:id/title";
        public final static String NODE_ANDROID_SUMMARY = "android:id/summary";

        // 对话框内容
        public final static String NODE_DIALOG_CONTENT_CANCEL_INSTALL = "是否取消安装？";
        public final static String NODE_DIALOG_CONTENT_QUIT_MALL = "你要关闭购物页面?";
        public final static String NODE_DIALOG_CONTENT_QUIT_TRANSACTION = "是否要放弃本次交易？";// 对话框内容

        public static String LOADING_DIALOG = "";

        // 搜索窗口的返回节点
        public static String NODE_SEARCH_BACK = "";
        // 其他窗口的返回节点 - 比如 我 - 设置 界面
        public static String NODE_OTHER_BACK = "";
        // 加载的ProgressBar节点 - 界面中间弹出的loading，比如 断网，打开我的二维码页面
        public static String NODE_LOADING_BACK = "";
        // 对话框取消按钮
        public static String NODE_DIALOG_CANCEL = "";
        // 对话框确认按钮
        public static String NODE_DIALOG_CONFIRM = "";
        // 对话框内容
        public static String NODE_DIALOG_CONTENT = "";
        // 对话框标题 比如 发现-购物-点 x
        public static String NODE_DIALOG_TITLE = "";
        // 对话框另一种标题，比如登录密码错误的弹框
        public static String NODE_DIALOG_SUMMARY = "";
        // 微信打开网页的progress bar节点
        public static String NODE_WEBVIEW_PROGRESSBAR = "";
        // action bar go button
        public static String NODE_ACTION_BAR_GO_BUTTON = "";

        public static String NODE_WECHAT_ACTIONBAR = "";
        //ContentMenu 选项
        public static String NODE_CONTENT_MENU_ITEM_TEXT = "";
        // 第一次打开摇一摇时的"请注意"弹框，微信安装后第一次使用时会弹出该框，见 bzy-ai/octopus#1513
        public static String NODE_SHAKE_ATTENTION_ACKNOWLEDGE_BUTTON = "";

        public static String NODE_CLOSE_UPLOAD_ID_CARD_DIALOG_BUTTON = "";

        public static void init(String version) {
            switch (version) {
                case "6.7.2":
                    LOADING_DIALOG = "com.tencent.mm:id/a06";
                    NODE_SEARCH_BACK = "com.tencent.mm:id/ja";
                    NODE_OTHER_BACK = "com.tencent.mm:id/j7";
                    NODE_LOADING_BACK = "com.tencent.mm:id/a05";
                    NODE_DIALOG_CANCEL = "com.tencent.mm:id/api";
                    NODE_DIALOG_CONFIRM = "com.tencent.mm:id/apj";
                    NODE_DIALOG_CONTENT = "com.tencent.mm:id/chb";
                    NODE_DIALOG_TITLE = "com.tencent.mm:id/ch2";
                    NODE_DIALOG_SUMMARY = "com.tencent.mm:id/ch7";

                    NODE_ACTION_BAR_GO_BUTTON = "com.tencent.mm:id/iv";
                    NODE_WEBVIEW_PROGRESSBAR = "com.tencent.mm:id/b38";
                    NODE_WECHAT_ACTIONBAR = "com.tencent.mm:id/ie";
                    NODE_CONTENT_MENU_ITEM_TEXT = "com.tencent.mm:id/ci";
                    NODE_SHAKE_ATTENTION_ACKNOWLEDGE_BUTTON = "com.tencent.mm:id/dfg";
                    NODE_CLOSE_UPLOAD_ID_CARD_DIALOG_BUTTON = "com.tencent.mm:id/al6"; // 通过上个版本的key所在resource xml的特征搜到
                    PROGRESS_DIALOG_CLASS_NAME = "com.tencent.mm.ui.base.p";
                    break;
            }
            HomePage.init(version);
            Search.init(version);
            ChatRoom.init(version);
            ContactInfo.init(version);
            SayHiUI.init(version);
        }


        public static class HomePage {

            // 首页tab文字-我
            public static final String TAB_WECHAT_TEXT = "微信";

            // 主页tab的节点
            public static String NODE_TAB = "";
            // 主页tab里面的文字节点
            public static String NODE_TAB_TEXT = "";
            public static String PLUS_BTN = "";
            public static String POP_PLUS_ITEM = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        NODE_TAB = "com.tencent.mm:id/chn";
                        NODE_TAB_TEXT = "com.tencent.mm:id/chp";
                        PLUS_BTN = "com.tencent.mm:id/hp";
                        POP_PLUS_ITEM = "com.tencent.mm:id/ci";
                        break;
                }
            }
        }

        public static class ChatRoom {

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                }
            }
        }

        public static class Search {
            public static String ADD_FRIEND_SEARCH_INPUT = "";
            public static String ADD_FRIEND_SEARCH_MAIN_INPUT = "";

            public static String FTS_ADD_FRIEND_UI = "";
            public static String FTS_ADD_FRIEND_LIST = "";
            public static String FTS_ADD_FRIEND_SEARCH_INPUT = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        FTS_ADD_FRIEND_UI = "com.tencent.mm.plugin.fts.ui.FTSAddFriendUI";
                        ADD_FRIEND_SEARCH_INPUT = "com.tencent.mm:id/jd";
                        ADD_FRIEND_SEARCH_MAIN_INPUT = "com.tencent.mm:id/bhl";
                        FTS_ADD_FRIEND_SEARCH_INPUT = "com.tencent.mm:id/jd";
                        FTS_ADD_FRIEND_LIST = "com.tencent.mm:id/bh9";
                        break;
                }
            }
        }

        public static class ContactInfo {
            public static String CONTACT_INFO_UI = "";
            public static String ADD_FRIEND_BTN = "";
            public static String SELECT_CONTACT_UI = "";
            public static String SELECT_CONTACT_INPUT = "";
            public static String SELECT_CONTACT_LIST = "";
            public static String SELECT_CONTACT_CONFIRM_BTN = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        SELECT_CONTACT_UI = "com.tencent.mm.ui.contact.SelectContactUI";
                        CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

                        ADD_FRIEND_BTN = "com.tencent.mm:id/arl";
                        SELECT_CONTACT_INPUT = "com.tencent.mm:id/cmy";
                        SELECT_CONTACT_LIST = "com.tencent.mm:id/h8";
                        SELECT_CONTACT_CONFIRM_BTN = "com.tencent.mm:id/iv";
                        break;
                }
            }
        }

        public static class SayHiUI {
            public static String SAY_HI_UI = "";
            public static String SEND_BTN = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        SAY_HI_UI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
                        SEND_BTN = "com.tencent.mm:id/iv";
                        break;
                }
            }
        }
    }
}