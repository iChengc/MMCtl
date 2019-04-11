package com.cc.core.wechat;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

import com.cc.core.ApplicationContext;
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
    public static String WechatVersion;

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
        if (ApplicationContext.application() != null) {
            KLog.e(">>>>已经Hook微信，无需再hook");
            return;
        }
        XposedBridge.log(">>开始hook微信主进程");
        WECHAT_CLASSLOADER = lpparam.classLoader;
        ApplicationContext.init(AndroidAppHelper.currentApplication());
        for (BaseXposedHook h : hooks) {
            h.hook(lpparam.classLoader);
        }
        Rpc.asRpcServer();
        XposedBridge.log("---->>结束hook微信主进程");
    }

    public static void initEnvironment(String packageName) {
        try {
            Context context = ApplicationContext.application();
            if (context == null) {
                context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            }
            WechatVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;

            Hook.init(WechatVersion);
            Resources.init(WechatVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addHook(String className) throws Exception {
        Class hook = Class.forName(className);
        addHook((BaseXposedHook) hook.newInstance());
    }

    public static void addHook(BaseXposedHook hook) throws Exception {
        hooks.add(hook);
    }

    public static <T extends BaseXposedHook> BaseXposedHook lookup(Class<T> clazz) {
        for (BaseXposedHook h : hooks) {
            if (h.getClass() == clazz) {
                return h;
            }
        }

        return null;
    }

    public static void startApp(Context context) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .setClassName(WECHAT_PACKAGE_NAME, Resources.LAUNCHER_UI_CLASS);
        context.startActivity(intent);
    }

    public static class Hook {

        public static String KernelClass = "";
        public static String LOGGER = "";
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
        public static String SaveAnrWatchDogHandlerClass = "com.tencent.mm.sdk.a.c";
        public static String MMCrashReporter = "com.tencent.mm.app.k";
        public static String ConstantsStorage = "com.tencent.mm.storage.ac";
        public static String WechatStorageCrashPath = "dFK";



        public static String commonWechatSdkXmlParserClass = "";
        public static String commonWechatSdkXmlParserToMapFunc = "";

        public static void init(String version) {
            Sqlite.init(version);
            Account.init(version);
            NetScene.init(version);
            Message.init(version);
            AddFriend.init(version);

            switch (version) {
                case "7.0.3":
                    KernelClass = "com.tencent.mm.kernel.g";
                    LOGGER = "com.tencent.mm.sdk.platformtools.ab";
                    UploadCrashLogClass = "com.tencent.mm.sandbox.monitor.a";
                    UploadCrashLogFunc = "hD";
                    UploadCrashWXRecoveryUploadServiceClass = "com.tencent.recovery.wx.service.WXRecoveryUploadService";
                    UploadCrashWXRecoveryUploadServicePushDataFunc = "pushData";
                    UploadCrashLogEnumClass = "com.tencent.mm.plugin.report.service.h";
                    UploadCrashLogEnumFunc = "a";
                    UploadCrashCrashUploaderServiceClass = "com.tencent.mm.crash.CrashUploaderService";
                    UploadCrashCrashUploaderServiceOnHandleIntentFunc = "onHandleIntent";
                    UploadCrashTraceRouteClass = "com.tencent.mm.plugin.traceroute.b.a.f";
                    UploadCrashTraceRouteFunc = "a";
                    UploadCrashStackReportUploaderClass = "com.tencent.mm.platformtools.ad";
                    UploadCrashStackReportUploaderFunc = "a";
                    SaveAnrWatchDogClass = "com.tencent.mm.sdk.a.b";
                    SaveAnrWatchDogSetHandlerFunc = "a";
                    SaveAnrWatchDogHandlerClass = "com.tencent.mm.sdk.a.c";
                    MMCrashReporter = "com.tencent.mm.app.k";
                    ConstantsStorage = "com.tencent.mm.storage.ac";
                    WechatStorageCrashPath = "eEe";

                    commonWechatSdkXmlParserClass = "com.tencent.mm.sdk.platformtools.bs";
                    commonWechatSdkXmlParserToMapFunc = "z";
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
                    MMCrashReporter = "com.tencent.mm.app.k";
                    ConstantsStorage = "com.tencent.mm.storage.ac";
                    WechatStorageCrashPath = "dFK";

                    commonWechatSdkXmlParserClass = "com.tencent.mm.sdk.platformtools.bm";
                    commonWechatSdkXmlParserToMapFunc = "r";
                    break;
            }
        }

        public static class Account {
            /**
             * type说明：flag enum
             * 0 - Unknown
             * 1 - 是好友关系
             * 2 - 有聊天记录
             * 4 - 有相同群
             * 8 - 黑名单
             * 16 - 无参考数据，未知, 从代码看似乎是 android only的意思，缺乏数据验证，不能确定
             * 32 - plugin
             * 64 - 星标好友
             * 128 - 无参考数据，未知
             * 256 - 不让对方看朋友圈
             * 512 - Mute, 消息免打扰
             * 1024 - 无参考数据，未知
             * 2048 - 聊天置顶
             * 32768 - 隐藏可以匹配手机通讯录的好友手机号
             * 65536 - 不看对方朋友圈
             * 524288 - 微信运动不与对方排行
             * <p>
             * 微信中好友列表使用的规则是 type & 1 = 1 and type & 8 = 0 and type & 32 = 0 and verifyFlag & 8 = 0
             */

            /**
             * verifyFlag说明：flag enum
             * 0 - Unknown
             * 1 - 目前唯一含有1的是京东，并且京东的原始id为qqwanggou001，猜测1是合作方或自营电商等含义
             * 2 - 无参考数据，未知
             * 4 - 无参考数据，未知
             * 8 - 公众号
             * 16 - 已认证
             * 32 - 微信团队
             */
            public static String AccountStorage = "";
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
                    case "7.0.3":
                        AccountStorage = "com.tencent.mm.model.c";
                        GetContactManagerFunc = "VI";
                        GetGroupManagerFunc = "VR";
                        GetConfigManagerFunc = "PO";
                        GetMsgInfoManagerFunc = "VK";
                        GetConversationManagerFunc = "VN";
                        GetContactInfoFunc = "anm";
                        ConfigStorageGetFunc = "get";
                        RegionCodeDecoderClass = "com.tencent.mm.storage.RegionCodeDecoder";
                        RegionCodeDecoderEncodeFunc = "az";
                        break;
                    case "6.7.2":
                        AccountStorage = "com.tencent.mm.model.c";
                        GetContactManagerFunc = "EO";
                        GetGroupManagerFunc = "EX";
                        GetConfigManagerFunc = "CQ";
                        GetMsgInfoManagerFunc = "EQ";
                        GetConversationManagerFunc = "ET";
                        GetContactInfoFunc = "ZQ";
                        ConfigStorageGetFunc = "get";
                        RegionCodeDecoderClass = "com.tencent.mm.storage.RegionCodeDecoder";
                        RegionCodeDecoderEncodeFunc = "an";
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
                    case "6.7.2":
                        GetDBHelerFunc = "Dg";
                        DBRawQueryFunc = "rawQuery";
                        DBExecSqlFunc = "gf";
                        DbHelperField = "dBo";
                        break;
                    case "7.0.3":
                        GetDBHelerFunc = "Qe";
                        DBRawQueryFunc = "rawQuery";
                        DBExecSqlFunc = "hN";
                        DbHelperField = "evI";
                        break;
                }
            }
        }

        public static class NetScene {
            public static String GetNetSceneQueueClass;
            public static String GetNetSceneQueueFunc;

            public static String NetSceneRequestClass;
            public static String NetSceneEnqueueFunc;
            public static String NetSceneEndFunc;
            public static String NetSceneQueueClass;
            public static String NetSceneRemoteRespClass = "";

            public static String FriendRequestNetSceneClass;
            public static String SearchFriendNetSceneClass;

            public static String NetSceneSendMsgClass = "";
            public static String NetSceneUploadMsgImg = "";
            public static String NetSceneUploadMsgVideo = "";
            public static String UploadMsgVideoHandler = "";
            public static int NetSceneUploadMsgImgMaskResId;
            public static String ModelCdnUtil = "";
            public static String ModelCdnUtilGetFileKeyFunc = "";

            public static String NetSceneResponseBodyKey = ""; // 参照 7.0.3: com.tencent.mm.ah.v
            public static String NetSceneCmdResponseBodyKey = ""; // 参照 7.0.3: com.tencent.mm.ah.b$c
            public static String NetSceneResponseStringBooleanValueKey = ""; // // 参照 7.0.3: com.tencent.mm.protocal.protobuf.brj

            static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        GetNetSceneQueueClass = "com.tencent.mm.model.av";
                        GetNetSceneQueueFunc = "CB";
                        NetSceneEnqueueFunc = "a";
                        NetSceneRequestClass = "com.tencent.mm.af.m";
                        NetSceneQueueClass = "com.tencent.mm.af.p";
                        NetSceneEndFunc = "onSceneEnd";
                        FriendRequestNetSceneClass = "com.tencent.mm.pluginsdk.model.m";
                        SearchFriendNetSceneClass = "com.tencent.mm.plugin.messenger.a.f";
                        // SearchFriendNetSceneClass = "com.tencent.mm.plugin.brandservice.b.h";
                        NetSceneRemoteRespClass = "com.tencent.mm.af.v";

                        NetSceneSendMsgClass = "com.tencent.mm.modelmulti.h";
                        NetSceneUploadMsgImg = "com.tencent.mm.ap.l";
                        NetSceneUploadMsgImgMaskResId = 2130838032;
                        NetSceneUploadMsgVideo = "com.tencent.mm.pluginsdk.model.j";
                        UploadMsgVideoHandler = "com.tencent.mm.sdk.f.e";
                        ModelCdnUtil = "com.tencent.mm.modelcdntran.d";
                        ModelCdnUtilGetFileKeyFunc = "a";

                        NetSceneResponseBodyKey = "dVG";
                        NetSceneCmdResponseBodyKey = "dUj";
                        NetSceneResponseStringBooleanValueKey = "sVc";
                        break;
                    case "7.0.3":
                        GetNetSceneQueueClass = "com.tencent.mm.model.av";
                        GetNetSceneQueueFunc = "Pw";
                        NetSceneEnqueueFunc = "a";
                        NetSceneRequestClass = "com.tencent.mm.ah.m";
                        NetSceneQueueClass = "com.tencent.mm.ah.p";
                        NetSceneEndFunc = "onSceneEnd";
                        FriendRequestNetSceneClass = "com.tencent.mm.pluginsdk.model.m";
                        SearchFriendNetSceneClass = "com.tencent.mm.plugin.messenger.a.f";
                        NetSceneRemoteRespClass = "com.tencent.mm.ah.v";

                        NetSceneSendMsgClass = "com.tencent.mm.modelmulti.h";
                        NetSceneUploadMsgImg = "com.tencent.mm.as.l";
                        NetSceneUploadMsgImgMaskResId = 2130838200;
                        NetSceneUploadMsgVideo = "com.tencent.mm.pluginsdk.model.j";
                        UploadMsgVideoHandler = "com.tencent.mm.sdk.g.d";

                        ModelCdnUtil = "com.tencent.mm.ak.c";
                        ModelCdnUtilGetFileKeyFunc = "a";

                        NetSceneResponseBodyKey = "feW";
                        NetSceneCmdResponseBodyKey = "fdy";
                        NetSceneResponseStringBooleanValueKey = "wiP";
                        break;
                }
            }
        }

        public static class AddFriend {
            public static String RelationType = "";
            public static String WechatId = "";
            public static String DecriptyWechatId = "";
            public static String AntispamTicket = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        RelationType = "eWZ";
                        WechatId = "sgh";
                        DecriptyWechatId = "sgx";
                        AntispamTicket = "sqc";
                        break;
                    case "7.0.3":
                        RelationType = "gfi";
                        WechatId = "vqP";
                        DecriptyWechatId = "vrl";
                        AntispamTicket = "vAm";
                        break;
                }
            }
        }

        public static class Message {
            public static String MessageSyncExtensionClass = "";
            public static String MessageSyncExtensionProcessCommonMessageFunc = "";
            public static String SyncMessageNotifierClass = "";
            public static String ProtocolAddMsgInfoClass = "";

            public static String MessageInfoFieldId = "";
            public static String MessageContentFieldId = "";
            //public static String MessageFromFieldId = "";
            public static String MessageToFieldId = "";
            public static String MessageTypeFieldId = "";
            public static String MessageDatetimeFieldId = "";
            public static String MessageServIdFieldId = "";

            public static String AppMsgLogic = "";
            public static String AppMsgLogicSendFunc = "";
            public static void init(String version) {

                switch (version) {
                    case "6.7.2":
                        MessageSyncExtensionClass = "com.tencent.mm.plugin.messenger.foundation.c";
                        MessageSyncExtensionProcessCommonMessageFunc = "a";
                        SyncMessageNotifierClass = "com.tencent.mm.plugin.messenger.foundation.a.t";
                        ProtocolAddMsgInfoClass = "com.tencent.mm.af.e.a";

                        MessageInfoFieldId = "dsF";
                        MessageContentFieldId = "rMB";
                        MessageToFieldId = "rMz";
                        MessageTypeFieldId = "knu";
                        MessageDatetimeFieldId = "mkk";
                        MessageServIdFieldId = "rMG";

                        AppMsgLogic = "com.tencent.mm.pluginsdk.model.app.l";
                        AppMsgLogicSendFunc = "a";
                        break;
                    case "7.0.3":
                        MessageSyncExtensionClass = "com.tencent.mm.plugin.messenger.foundation.c";
                        MessageSyncExtensionProcessCommonMessageFunc = "a";
                        SyncMessageNotifierClass = "com.tencent.mm.plugin.messenger.foundation.a.t";
                        ProtocolAddMsgInfoClass = "com.tencent.mm.ah.e.a";

                        MessageInfoFieldId = "emv";
                        MessageContentFieldId = "uUw";
                        MessageToFieldId = "uUu";
                        MessageTypeFieldId = "mxa";
                        MessageDatetimeFieldId = "ozl";

                        //MessageFromFieldId = "";
                        MessageServIdFieldId = "oPR";

                        //TODO: support 7.0.3
                        AppMsgLogic = "com.tencent.mm.pluginsdk.model.app.l";
                        AppMsgLogicSendFunc = "a";
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

        // android手机anr等待按钮
        public final static String NODE_DIALOG_ANR_WAITING = "android:id/button2";

        public final static String NODE_ANDROID_TITLE = "android:id/title";

        // 对话框内容
        public final static String NODE_DIALOG_CONTENT_CANCEL_INSTALL = "是否取消安装？";
        public final static String NODE_DIALOG_CONTENT_QUIT_TRANSACTION = "是否要放弃本次交易？";// 对话框内容

        public static String LOADING_DIALOG = "";
        // 其他窗口的返回节点 - 比如 我 - 设置 界面
        public static String NODE_OTHER_BACK = "";
        // 其他窗口的返回节点 - 比如 我 - 设置 界面
        public static String NODE_OTHER_BACK1 = "";
        // 加载的ProgressBar节点 - 界面中间弹出的loading，比如 断网，打开我的二维码页面
        public static String NODE_LOADING_BACK = "";
        // 对话框取消按钮
        public static String NODE_DIALOG_CANCEL = "";
        // 对话框确认按钮
        public static String NODE_DIALOG_CONFIRM = "";
        // 对话框内容
        public static String NODE_DIALOG_CONTENT = "";
        // action bar go button
        public static String NODE_ACTION_BAR_GO_BUTTON = "";
        //ContentMenu 选项
        public static String NODE_CONTENT_MENU_ITEM_TEXT = "";

        public static void init(String version) {
            switch (version) {
                case "6.7.2":
                    LOADING_DIALOG = "com.tencent.mm:id/a06";
                    NODE_OTHER_BACK = "com.tencent.mm:id/j7";
                    NODE_LOADING_BACK = "com.tencent.mm:id/a05";
                    NODE_DIALOG_CANCEL = "com.tencent.mm:id/api";
                    NODE_DIALOG_CONFIRM = "com.tencent.mm:id/apj";
                    NODE_DIALOG_CONTENT = "com.tencent.mm:id/chb";

                    NODE_ACTION_BAR_GO_BUTTON = "com.tencent.mm:id/iv";
                    NODE_CONTENT_MENU_ITEM_TEXT = "com.tencent.mm:id/ci";
                    PROGRESS_DIALOG_CLASS_NAME = "com.tencent.mm.ui.base.p";
                    break;
                case "7.0.3":
                    LOADING_DIALOG = "com.tencent.mm:id/a5w";
                    NODE_OTHER_BACK = "com.tencent.mm:id/ka";
                    NODE_OTHER_BACK1 = "com.tencent.mm:id/ke";
                    NODE_LOADING_BACK = "com.tencent.mm:id/a05";
                    NODE_DIALOG_CANCEL = "com.tencent.mm:id/az9";
                    NODE_DIALOG_CONFIRM = "com.tencent.mm:id/az_";
                    NODE_DIALOG_CONTENT = "com.tencent.mm:id/d6y";

                    NODE_ACTION_BAR_GO_BUTTON = "com.tencent.mm:id/k1";
                    NODE_CONTENT_MENU_ITEM_TEXT = "com.tencent.mm:id/ci";
                    PROGRESS_DIALOG_CLASS_NAME = "com.tencent.mm.ui.base.p";
                    break;
            }
            HomePage.init(version);
            Search.init(version);
            ContactInfo.init(version);
            SayHiUI.init(version);
        }


        public static class HomePage {

            // 首页tab文字-我
            public static final String TAB_WECHAT_TEXT = "Chats";

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
                    case "7.0.3":
                        NODE_TAB = "com.tencent.mm:id/bq";
                        NODE_TAB_TEXT = "com.tencent.mm:id/d7b";
                        PLUS_BTN = "com.tencent.mm:id/iq";
                        POP_PLUS_ITEM = "com.tencent.mm:id/ci";
                        break;
                }
            }
        }

        public static class Search {

            public static String FTS_ADD_FRIEND_UI = "";
            public static String FTS_ADD_FRIEND_LIST = "";
            public static String FTS_ADD_FRIEND_SEARCH_INPUT = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        FTS_ADD_FRIEND_UI = "com.tencent.mm.plugin.fts.ui.FTSAddFriendUI";
                        FTS_ADD_FRIEND_SEARCH_INPUT = "com.tencent.mm:id/jd";
                        FTS_ADD_FRIEND_LIST = "com.tencent.mm:id/bh9";
                        break;
                    case "7.0.3":
                        FTS_ADD_FRIEND_UI = "com.tencent.mm.plugin.fts.ui.FTSAddFriendUI";
                        FTS_ADD_FRIEND_SEARCH_INPUT = "com.tencent.mm:id/kh";
                        FTS_ADD_FRIEND_LIST = "com.tencent.mm:id/bwh";
                        break;
                }
            }
        }

        public static class ContactInfo {
            public static String CONTACT_INFO_UI = "";
            public static String ADD_FRIEND_BTN = "";
            public static String SELECT_CONTACT_UI = "";
            public static String CONTACT_INFO_LIST = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        SELECT_CONTACT_UI = "com.tencent.mm.ui.contact.SelectContactUI";
                        CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

                        ADD_FRIEND_BTN = "com.tencent.mm:id/arl";
                        break;
                    case "7.0.3":
                        SELECT_CONTACT_UI = "com.tencent.mm.ui.contact.SelectContactUI";
                        CONTACT_INFO_UI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

                        ADD_FRIEND_BTN = "com.tencent.mm:id/cs";
                        CONTACT_INFO_LIST = "android:id/list";
                        break;
                }
            }
        }

        public static class SayHiUI {
            public static String SAY_HI_UI = "";
            public static String SEND_BTN = "";
            public static String SAY_HI_INPUT = "";

            public static void init(String version) {
                switch (version) {
                    case "6.7.2":
                        SAY_HI_UI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
                        SEND_BTN = "com.tencent.mm:id/iv";
                        break;
                    case "7.0.3":
                        SAY_HI_UI = "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI";
                        SEND_BTN = "com.tencent.mm:id/jx";
                        SAY_HI_INPUT = "com.tencent.mm:id/e0o";
                        break;
                }
            }
        }
    }
}