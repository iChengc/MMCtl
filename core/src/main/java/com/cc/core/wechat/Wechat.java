package com.cc.core.wechat;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

import com.cc.core.ApplicationContext;
import com.cc.core.WorkerHandler;
import com.cc.core.actions.Actions;
import com.cc.core.log.KLog;
import com.cc.core.rpc.Rpc;
import com.cc.core.utils.FileUtil;
import com.cc.core.wechat.invoke.InitDelayHooksAction;
import com.cc.core.xposed.BaseXposedHook;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static String[] SupportVersion = new String[]{
            "7.0.3"
    };

    private void Wechat() {
    }

    static {
        loadHooks();
    }

    private static void loadHooks() {
        try {
            for (String name : WechatHooks.HOOKS) {
                addHook(name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClassLoader WECHAT_CLASSLOADER;
    public static String DB_PATH;
    public static String DB_PASSWORD;

    public static void start(XC_LoadPackage.LoadPackageParam lpparam) {
        if (ApplicationContext.application() != null) {
            XposedBridge.log(">>>>已经Hook微信，无需再hook");
            return;
        }
        XposedBridge.log(">>开始hook微信主进程");

        WECHAT_CLASSLOADER = lpparam.classLoader;
        if (!ApplicationContext.init(AndroidAppHelper.currentApplication())) {
            // 删除热更新文件
            removePatchFile();
            return;
        }
        // 删除热更新文件
        removePatchFile();
        Rpc.asRpcServer();

        for (BaseXposedHook h : hooks) {
            h.hook(lpparam.classLoader);
        }
        XposedBridge.log("---->>结束hook微信主进程");
        WorkerHandler.postOnWorkThreadDelayed(new Runnable() {
            @Override
            public void run() {
                Actions.Companion.execute(InitDelayHooksAction.class, "");
            }
        }, 10000);
    }

    private static void removePatchFile() {
        File patchFile = new File("/data/data/com.tencent.mm/tinker");
        if (ApplicationContext.application() != null) {
            patchFile = new File(ApplicationContext.application().getFilesDir(), "tinker");
        }

        FileUtil.deleteDir(patchFile);

        WorkerHandler.removeCallbacks(removePathFileRunnable);
        // 每隔半小时尝试删除热更新文件
        WorkerHandler.postOnWorkThreadDelayed(removePathFileRunnable, 1800000);
    }

    private static Runnable removePathFileRunnable = new Runnable() {
        @Override
        public void run() {
            removePatchFile();
        }
    };

    public static boolean initEnvironment(String packageName) {

        try {
            Context context = ApplicationContext.application();
            if (context == null) {
                context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            }
            WechatVersion = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
            if (!Arrays.asList(Wechat.SupportVersion).contains(WechatVersion)) {
                KLog.e("not support wechat version:" + WechatVersion);
                return false;
            }

            Hook.init(WechatVersion);
            Resources.init(WechatVersion);
        } catch (Exception e) {
            KLog.e("init environment error", e);
        }
        return true;
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
        public static String UploadCrashLogClass = "";
        public static String UploadCrashLogFunc = "";
        public static String UploadCrashWXRecoveryUploadServiceClass = "";
        public static String UploadCrashWXRecoveryUploadServicePushDataFunc = "";
        public static String UploadCrashLogEnumClass = "";
        public static String UploadCrashLogEnumFunc = "a";
        public static String UploadCrashCrashUploaderServiceClass = "";
        public static String UploadCrashCrashUploaderServiceOnHandleIntentFunc = "";
        public static String UploadCrashTraceRouteClass = "";
        public static String UploadCrashTraceRouteFunc = "";
        public static String UploadCrashStackReportUploaderClass = "";
        public static String UploadCrashStackReportUploaderFunc = "";
        public static String SaveAnrWatchDogClass = "";
        public static String SaveAnrWatchDogSetHandlerFunc = "";
        public static String SaveAnrWatchDogHandlerClass = "";
        public static String MMCrashReporter = "";
        public static String ConstantsStorage = "";
        public static String WechatStorageCrashPath = "";

        public static String commonWechatSdkXmlParserClass = "";
        public static String commonWechatSdkXmlParserToMapFunc = "";

        public static String GetFileMd5Uitls = "";
        public static String GetFileMd5Func = "";

        public static String ProtobufParseFromFunc = "";

        public static void init(String version) {
            Sqlite.init(version);
            Account.init(version);
            NetScene.init(version);
            Message.init(version);
            AddFriend.init(version);
            Group.init(version);
            Sns.init(version);

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

                    GetFileMd5Uitls = "com.tencent.mm.vfs.e";
                    GetFileMd5Func = "arz";

                    ProtobufParseFromFunc = "parseFrom";
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
            public static String ContactStorageLogic = "";
            public static String ContactStorageLogicUpdateRemark = "";
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
                        ContactStorageLogic = "com.tencent.mm.model.s";
                        ContactStorageLogicUpdateRemark = "b";
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
                }
            }
        }

        public static class Sqlite {
            public static String GetDBHelerFunc = "";
            public static String DBRawQueryFunc = "";
            public static String DBExecSqlFunc = "";
            public static String DbHelperField = "";

            static void init(String version) {
                switch (version) {
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

            public static String MessageVoiceLogicClass = "";
            public static String MessageVoiceLogicGetVoiceFullPathFunc = "";

            public static String AppMsgLogic = "";
            public static String AppMsgLogicSendFunc = "";

            public static void init(String version) {

                switch (version) {
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

                        MessageVoiceLogicClass = "com.tencent.mm.modelvoice.q";
                        MessageVoiceLogicGetVoiceFullPathFunc = "T";

                        //MessageFromFieldId = "";
                        MessageServIdFieldId = "oPR";

                        AppMsgLogic = "com.tencent.mm.pluginsdk.model.app.l";
                        AppMsgLogicSendFunc = "a";
                        break;
                }
            }
        }

        public static class Group {
            public static String CreateGroupRequest = "";
            public static String CreateGroupWechatIdField = "";

            public static String GetGroupInfoFunc = "";
            public static String GroupParseChatroomDataFunc = "";
            public static String ChatroomMembersField = "";
            public static String ChatroomMemberGroupNicknameField = "";
            public static String ChatroomMemberInviterField = "";

            public static String RoomServiceFactoryClass = "";
            public static String RoomServiceFactoryGetRoomService = "";
            public static String RoomServiceGetRequest = "";
            public static String SendAddMemberRequest = "";

            public static void init(String version) {
                switch (version) {
                    case "7.0.3":
                        CreateGroupRequest = "com.tencent.mm.chatroom.c.g";
                        CreateGroupWechatIdField = "uUb";
                        GetGroupInfoFunc = "nB";
                        GroupParseChatroomDataFunc = "dmq";
                        ChatroomMembersField = "fhZ";
                        ChatroomMemberGroupNicknameField = "ebH";
                        ChatroomMemberInviterField = "ebJ";
                        RoomServiceFactoryClass = "com.tencent.mm.roomsdk.a.b";
                        RoomServiceFactoryGetRoomService = "akv";
                        RoomServiceGetRequest = "a";
                        SendAddMemberRequest = "dhu";
                        break;
                }
            }
        }

        public static class Sns {
            public static String SnsUploadPackHelper = "";
            public static String LocationClass = "";
            public static String PicWidget = "";
            public static String UploadManager = "";

            public static String UploadFun = "";

            public static String SnsSetDescriptionFun = "";
            public static String SnsSetLocationFun = "";
            public static String SnsSetAtUsersFun = "";
            public static String SnsSetIsPrivateFun = "";
            public static String SnsSetWatcherTypeFun = ""; // 1不给谁看
            public static String SnsSetSyncQQZoneFun = ""; // 4
            public static String SnsSetShareTypeFun = "";
            public static String SnsSetUrlFun = "";
            public static String SnsSetWatchersFun = "";
            public static String SnsSetSessionIdFun = "";
            public static String SnsSetMediaInfoFun = "";
            public static String SnsSetVideoInfoFun = "";
            public static String SnsSetShareThumbFun = "";
            public static String SnsSetAppIdFun = "";
            public static String SnsSetAppNameFun = "";
            public static String SnsGetSessionIdUtil = "";
            public static String SnsGetSessionIdFun = "";

            public static String SnsSetShareUrlFun = "";
            public static String SnsSetShareUrl2Fun = "";
            public static String SnsSetShareTitleFun = "";

            public static String SnsGetRequest = "";
            public static String SnsCoreClass = "";
            public static String SnsCoreGetInstance = "";
            public static String SnsCoreStorageField = "";
            public static String SnsCoreGetSnsCommentStorage = "";
            public static String SnsCoreGetDownloadManager= "";
            public static String SnsCoreGetSnsInfoStorage = "";
            public static String SnsStorageGetBySnsId = "";
            public static String SnsStorage2Timeline = "";

            public static String SnsTimeLineContentField = "";
            public static String SnsTimeLineDetailsField = "";
            public static String SnsTimeLineShareTitleField = "";
            public static String SnsTimeLineMediaField = "";
            public static String SnsTimeLineMediaUrlField = "";
            public static String SnsTimeLineShareUrlField = "";

            public static String SnsTimelineCommentProtobuf = "";
            public static String SnsTimelineCommentLikeProtobuf = "";
            public static String SnsTimelineCommentListField = "";
            public static String SnsTimelineLikeListField = "";
            public static String SnsTimelineCommenterField = "";
            public static String SnsTimelineCommenterNameField = "";
            public static String SnsTimelineCommentTimeField = "";
            public static String SnsTimelineCommentReplay2Field = "";
            public static String SnsTimelineCommentContentField = "";
            public static String SnsTimelineCommentIdField = "";
            public static String SnsTimelineCommentReplay2IdField = "";

            public static String SnsTimelineScene = "";
            public static String SnsTimelineOnlineVideoService = "";
            public static String SnsTimelineGenVideoCdnInfo = "";
            public static String SnsTimelineVideoCdnRequest = "";
            public static String SnsTimelineVideoCdnRequestGen = "";
            public static String SnsTimelineVideoSceneGen = "";
            public static String SnsTimelineVideoCdnRequestSend = "";
            public static String SnsTimelineImageSceneGen = "";
            // 在com.tencent.mm.plugin.sns.model.b中
            public static String SnsDownloadManagerClass = "";
            public static String SnsTimelineStartImageDownload = "";
            public static String SnsGetImageLocalPathFunc = "";
            public static String SnsCoreGetLazyImageLoaderFunc = "" ;
            public static String SnsMediaClass = "";

            public static String SnsTimelineCommentHelper = "";
            public static String SnsTimelineCommentSend = "";
            public static String SnsTimelineCancelLike= "";
            public static String SnsTimelineCancelCommentRequest= "";

            public static void init(String version) {
                switch (version) {
                    case "7.0.3":
                        SnsUploadPackHelper = "com.tencent.mm.plugin.sns.model.ax";
                        LocationClass = "com.tencent.mm.protocal.protobuf.axc";
                        PicWidget = "com.tencent.mm.plugin.sns.ui.ag";
                        UploadManager = "com.tencent.mm.plugin.sns.model.aw";

                        UploadFun = "cih";

                        SnsSetDescriptionFun = "WU";
                        SnsSetLocationFun = "a";
                        SnsSetAtUsersFun = "ar";
                        SnsSetIsPrivateFun = "Co";
                        SnsSetWatcherTypeFun = "Cr";
                        SnsSetSyncQQZoneFun = "Cp";
                        SnsSetShareTypeFun = "Cq";
                        SnsSetUrlFun = "f";
                        SnsSetWatchersFun = "de";
                        SnsSetSessionIdFun = "setSessionId";
                        SnsSetMediaInfoFun = "df";
                        SnsSetVideoInfoFun = "s";
                        SnsSetAppIdFun = "Xa";
                        SnsSetAppNameFun = "Xb";
                        SnsSetShareThumbFun = "b";
                        SnsGetSessionIdUtil = "com.tencent.mm.model.u";
                        SnsGetSessionIdFun = "nx";
                        SnsCoreGetDownloadManager = "cjp";

                        SnsSetShareUrlFun = "WX";
                        SnsSetShareUrl2Fun = "WY";
                        SnsSetShareTitleFun = "WZ";

                        SnsGetRequest = "com.tencent.mm.plugin.sns.model.y";
                        SnsCoreClass = "com.tencent.mm.plugin.sns.model.af";
                        SnsCoreGetInstance = "cjb";
                        SnsCoreStorageField = "evI";
                        SnsCoreGetSnsCommentStorage = "cjz";
                        SnsCoreGetSnsInfoStorage = "cju";
                        SnsStorageGetBySnsId = "jW";
                        SnsStorage2Timeline = "cmi";

                        SnsTimeLineContentField = "wsu";
                        SnsTimeLineDetailsField = "wsx";
                        SnsTimeLineShareTitleField = "Title";
                        SnsTimeLineMediaField = "vqu";
                        SnsTimeLineMediaUrlField = "Url";
                        SnsTimeLineShareUrlField = "Url";

                        SnsTimelineCommentLikeProtobuf = "com.tencent.mm.protocal.protobuf.bys";
                        SnsTimelineCommentProtobuf = "com.tencent.mm.protocal.protobuf.byg";
                        SnsTimelineCommentListField = "wnp";
                        SnsTimelineLikeListField = "wnm";
                        SnsTimelineCommenterField = "uWD";
                        SnsTimelineCommenterNameField = "vRo";
                        SnsTimelineCommentTimeField = "ozl";
                        SnsTimelineCommentReplay2Field = "wmV";
                        SnsTimelineCommentContentField = "mzy";
                        SnsTimelineCommentIdField = "wmB";
                        SnsTimelineCommentReplay2IdField = "wmA";

                        SnsTimelineScene = "com.tencent.mm.storage.az";
                        SnsTimelineVideoSceneGen = "dnK";
                        SnsTimelineImageSceneGen = "dnF";
                        SnsTimelineOnlineVideoService = "com.tencent.mm.ak.e";
                        SnsTimelineGenVideoCdnInfo = "a";
                        SnsTimelineVideoCdnRequestSend = "a";
                        SnsTimelineVideoCdnRequest = "com.tencent.mm.modelvideo.o";
                        SnsTimelineVideoCdnRequestGen = "ajm";

                        SnsDownloadManagerClass = "com.tencent.mm.plugin.sns.model.b";
                        SnsTimelineStartImageDownload = "a";
                        SnsGetImageLocalPathFunc = "C";
                        SnsCoreGetLazyImageLoaderFunc = "cjr" ;
                        SnsMediaClass = "com.tencent.mm.protocal.protobuf.azc";

                        SnsTimelineCommentHelper = "com.tencent.mm.plugin.sns.model.am$a";
                        SnsTimelineCommentSend = "b";
                        SnsTimelineCancelLike = "WL";
                        SnsTimelineCancelCommentRequest= "com.tencent.mm.plugin.sns.model.r";
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