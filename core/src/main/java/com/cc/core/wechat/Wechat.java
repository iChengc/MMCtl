package com.cc.core.wechat;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

import com.cc.core.MyApplication;
import com.cc.core.rpc.Rpc;
import com.cc.core.xposed.BaseXposedHook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;

public class Wechat {
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";
    private static List<BaseXposedHook> hooks = new ArrayList<>();
    private void Wechat(){}
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

    public static void start(ClassLoader classLoader) {
        XposedBridge.log(">>开始hook微信主进程");
        MyApplication.init(AndroidAppHelper.currentApplication());
        for (BaseXposedHook h : hooks) {
            h.hook(classLoader);
        }
        Rpc.asRpcServer();
        XposedBridge.log("---->>结束hook微信主进程");
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

        public static String AEDHLPClass = "";
        public static String AEDHLPGetAccessibilityClickCountFunc = "";
        public static String NormsgSourceImplClass = "";
        public static String NormsgSourceImplGetEnabledAccessibilityServicesFunc = "";
    }

    public static class Resources {
        // Webview activity's name
        public static final String LAUNCHER_UI_CLASS = "com.tencent.mm.ui.LauncherUI";
        public final static String WEBVIEW_ACTIVITY_CLASS_NAME = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
        public static String PROGRESS_DIALOG_CLASS_NAME = "";

        // 首页tab文字-我
        public static final String NODE_TAB_TEXT_ME = "我";

        // 系统权限弹框节点
        public final static String NODE_SYSTEM_PERMISSION_MESSAGE = "com.android.packageinstaller:id/permission_message";
        public final static String NODE_SYSTEM_PERMISSION_DO_NOT_ASK = "com.android.packageinstaller:id/do_not_ask_checkbox";
        public final static String NODE_SYSTEM_PERMISSION_ALLOW = "com.android.packageinstaller:id/permission_allow_button";
        public final static String NODE_SYSTEM_PERMISSION_DENY = "com.android.packageinstaller:id/permission_deny_button";


        // 对话框内容
        public final static String NODE_DIALOG_CONTENT_FORBIDDEN = "操作太频繁，请稍后再试。";
        // 对话框内容
        public final static String NODE_DIALOG_CONTENT_FRIEND_FULL = "你的朋友数量已达到上限，可删除部分朋友后重新添加。";
        // 邀请内容
        public final static String NODE_DIALOG_CONFIRM_INVITE = "邀请";
        // 发送邀请确认
        public final static String NODE_DIALOG_CONFIRM_SEND = "发送";
        // 邀请内容
        public final static String NODE_DIALOG_CONFIRM_PUBLISH = "发布";
        // 邀请内容
        public final static String NODE_DIALOG_CONFIRM_CLEAR = "清空";
        // 确定
        public final static String NODE_DIALOG_CONFIRM_CONFIRM = "确定";
        // 取消
        public final static String NODE_DIALOG_CONFIRM_CANCEL = "取消";
        // 去验证
        public final static String NODE_DIALOG_CONFIRM_VALIDATION = "去验证";
        // android手机anr等待按钮
        public final static String NODE_DIALOG_ANR_WAITING = "android:id/button2";

        public final static String NODE_ANDROID_LIST = "android:id/list";
        public final static String NODE_ANDROID_TITLE = "android:id/title";
        public final static String NODE_ANDROID_SUMMARY = "android:id/summary";

        // 对话框内容
        public final static String NODE_DIALOG_CONTENT_CANCEL_INSTALL = "是否取消安装？";
        public final static String NODE_DIALOG_CONTENT_CERTIFICATION = "你需要实名验证后才能接受邀请，可在“我”->“钱包”中绑定银行卡进行验证。";
        public final static String NODE_DIALOG_CONTENT_QUIT_MALL = "你要关闭购物页面?";
        public final static String NODE_DIALOG_CONTENT_QUIT_TRANSACTION = "是否要放弃本次交易？";// 对话框内容

        public final static String NODE_DIALOG_CONTENT_INVITATION_ERROR = "接受群邀请被禁止";
        public final static String NODE_DIALOG_CONTENT_SCAN_JOIN_GROUP_ERROR = "扫码进群被禁止";

        public final static String NODE_DIALOG_TITLE_LOGIN_FAIL = "登录失败";

        // 完成
        public final static String NODE_GO_BUTTON_TEXT_FINISH = "完成";

        public static class HomePage {
            // common
            // 主页tab的节点
            public static String NODE_TAB = "";
            // 主页tab里面的文字节点
            public static String NODE_TAB_TEXT = "";

            public static void  init(String version) {
                switch (version) {
                    case "6.7.2":
                        NODE_TAB = "com.tencent.mm:id/chn";
                        NODE_TAB_TEXT = "com.tencent.mm:id/chp";
                        break;
                }
            }
        }

        public static class ChatPage {}

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
        }
    }
}