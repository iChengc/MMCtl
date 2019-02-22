package com.cc.core.actions.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ListView;

import com.cc.core.actions.Action;
import com.cc.core.actions.Actions;
import com.cc.core.command.impl.OpenWechatAction;
import com.cc.core.log.KLog;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.Wechat.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class AccessibilityAction implements Action {
    private static final String TAG = AccessibilityAction.class.getSimpleName();

    private static final String GOOGLE_PINYIN_PACKAGE_NAME = "com.google.android.inputmethod.pinyin";
    private static final String GOOGLE_PINYIN_IME_ID = GOOGLE_PINYIN_PACKAGE_NAME + "/.PinyinIME";
    private static final String GOOGLE_PINYIN_PREFERENCE_FILE = "/data/data/com.google.android.inputmethod.pinyin/shared_prefs/com.google.android.inputmethod.pinyin_preferences.xml";

    private static final Set<String> SYS_DESKTOP_PACKAGE_NAMES = new HashSet<String>() {{
        add("com.google.android.googlequicksearchbox");
    }};

    protected static final int FIND_NODE_INTERVAL = 100;
    protected static final int FIND_NODE_DEFAULT_TIMEOUT = 10 * 1000;
    protected static final int FIND_LOADING_NODE_TIMEOUT = 1 * 1000;
    protected static final int WAIT_ACTION_DEFAULT_TIMEOUT = 5 * 1000;
    protected static final int NORMAL_ACTION_DEFAULT_TIMEOUT = 3 * 1000;

    private final static int TAB_INDEX_HOMEPAGE = 0;
    private final static int TAB_INDEX_CONTACTS = 1;
    private final static int TAB_INDEX_DISCOVERY = 2;
    private final static int TAB_INDEX_ME = 3;

    private boolean isDismissingSystemHangDialog;

    public AccessibilityAction() {

    }

    protected boolean performClickByRidWithRetry(String id) {
        int tryCount = 0;
        boolean result = false;
        while (!result && tryCount < 10) {
            AccessibilityNodeInfo node = getNodeById(id, null);
            if (node != null) {
                result = performClick(node);
            }
            tryCount++;
        }
        return result;
    }

    protected boolean performClick(String id) {
        AccessibilityNodeInfo node = getNodeById(id, null);
        if (node == null) {
            return false;
        }
        return performClick(node);
    }

    protected boolean performClickListItem(String id, int index) {
        AccessibilityNodeInfo node = getNodeById(id, null);
        if (node == null) {
            return false;
        }
        if (isView(node, ListView.class) || isView(node, RecyclerView.class)) {
            if (node.getChildCount() > index) {
                node = node.getChild(index);
            }
        }
        return performClick(node);
    }

    protected boolean performClick(AccessibilityNodeInfo item) {
        return click(item) && Utils.sleep(1000);
    }

    protected boolean performLongClick(String id) {
        AccessibilityNodeInfo node = getNodeById(id, null);
        if (node == null) {
            return false;
        }
        return performLongClick(node);
    }

    protected boolean performLongClick(AccessibilityNodeInfo item) {
        AccessibilityNodeInfo clickItem = getClickableParent(item);
        if (clickItem != null) {
            boolean result = clickItem.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            Utils.sleep(1000);
            return result;
        } else {
            return false;
        }
    }

    protected boolean performDoubleClick(AccessibilityNodeInfo item) {
        KLog.d(this.getClass().getSimpleName(), ">>>>> perform double click start <<<<<");
        AccessibilityNodeInfo clickItem = getClickableParent(item);
        if (clickItem != null) {
            boolean result = clickItem.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Utils.sleep(100);
            result &= clickItem.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Utils.sleep(1000);
            return result;
        } else {
            return false;
        }
    }

    protected boolean goToHomePage() {
        return backToHomePage() && clickMainPageTab(TAB_INDEX_HOMEPAGE) && Utils.sleep(100);
    }

    protected boolean goToContacts() {
        return backToHomePage() && clickMainPageTab(TAB_INDEX_CONTACTS) && Utils.sleep(100);
    }

    protected boolean goToMe() {
        return backToHomePage() && clickMainPageTab(TAB_INDEX_ME) && Utils.sleep(100);
    }

    protected boolean goToDiscovery() {
        return backToHomePage() && clickMainPageTab(TAB_INDEX_DISCOVERY) && Utils.sleep(100);
    }

    protected void startWeChat() {
        //Wechat.startApp(WechatAccessibilityService.getInstance().getApplicationContext());
        Actions.Companion.execute(OpenWechatAction.class, "startWechat");
    }

    private boolean clickMainPageTab(int tabIndex) {
        List<AccessibilityNodeInfo> tabList = getNodesById(Wechat.Resources.HomePage.NODE_TAB, null);
        if (tabList == null || tabList.size() == 0 || tabList.size() <= tabIndex) {
            KLog.i(TAG, "clickMainPageTab: IndexOutOfBoundsException");

            return false;
        }

        return this.click(tabList.get(tabIndex));
    }

    protected boolean backFromNormalPage() {
        // 普通窗口
        AccessibilityNodeInfo backItem = getNodeById(Resources.NODE_OTHER_BACK, null);
        if (backItem != null) {
            performClick(backItem);
            return true;
        } else {
            return false;
        }
    }

    protected boolean cancleDialog() {
        // 普通窗口
        AccessibilityNodeInfo backItem = getNodeByText(Wechat.Resources.NODE_DIALOG_CONFIRM_CANCEL, null);
        if (backItem != null) {
            performClick(backItem);
            return true;
        } else {
            return false;
        }
    }

    protected boolean backFromSearchPage() {
        // 搜索窗口
        AccessibilityNodeInfo backItem = getNodeById(Resources.NODE_SEARCH_BACK, null);
        if (backItem != null) {
            performClick(backItem);
            return true;
        } else {
            return false;
        }
    }

    private boolean isView(AccessibilityNodeInfo node, Class<? extends View> type) {
        boolean result = false;
        if (node != null && node.getClassName().toString().equals(type.getName())) {
            result = true;
        }

        return result;
    }

    private AccessibilityNodeInfo getClickableParent(AccessibilityNodeInfo item) {
        if (item == null) {
            return null;
        }

        do {
            if (item.isClickable()) {
                break;
            }
            item = item.getParent();
        } while (item != null);

        return item;
    }

    // region New logic

    // region Query methods

    protected AccessibilityNodeInfo getNodeById(String id, AccessibilityNodeInfo rootNode) {
        return getNodeById(id, rootNode, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected AccessibilityNodeInfo getNodeById(String id, AccessibilityNodeInfo rootNode, int timeout) {
        List<AccessibilityNodeInfo> results = getNodesById(id, rootNode, timeout);

        return results == null || results.size() == 0 ? null : results.get(0);
    }

    /**
     * 获取匹配参数中任意一个id的节点
     */
    protected AccessibilityNodeInfo getNodeByIds(String[] ids, AccessibilityNodeInfo rootNode, int timeout) {
        List<AccessibilityNodeInfo> results = getNodesByIds(ids, rootNode, timeout);

        return results == null || results.size() == 0 ? null : results.get(0);
    }

    protected AccessibilityNodeInfo getNodeByText(String text, AccessibilityNodeInfo rootNode) {
        return getNodeByText(text, rootNode, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected AccessibilityNodeInfo getNodeByText(String text, AccessibilityNodeInfo rootNode, int timeout) {
        List<AccessibilityNodeInfo> results = getNodesByText(text, rootNode, timeout);

        return results == null || results.size() == 0 ? null : results.get(0);
    }

    protected AccessibilityNodeInfo getNodeByIdWithText(String id, String text, AccessibilityNodeInfo rootNode) {
        return getNodeByIdWithText(id, text, rootNode, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected AccessibilityNodeInfo getNodeByIdWithText(String id, String text, AccessibilityNodeInfo rootNode, int timeout) {
        List<AccessibilityNodeInfo> results = getNodesByIdWithText(id, text, rootNode, timeout);

        return results == null || results.size() == 0 ? null : results.get(0);
    }

    protected List<AccessibilityNodeInfo> getNodesById(String id, AccessibilityNodeInfo rootNode) {
        return getNodesById(id, rootNode, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected List<AccessibilityNodeInfo> getNodesById(final String id, AccessibilityNodeInfo rootNode, int timeout) {
        return getNodes(rootNode, new NodesFinder() {
            @Override
            public List<AccessibilityNodeInfo> find(AccessibilityNodeInfo rootNode) {
                return rootNode.findAccessibilityNodeInfosByViewId(id);
            }
        }, timeout);
    }

    protected List<AccessibilityNodeInfo> getNodesByIds(final String[] ids, AccessibilityNodeInfo rootNode, int timeout) {
        return getNodes(rootNode, new NodesFinder() {
            @Override
            public List<AccessibilityNodeInfo> find(AccessibilityNodeInfo rootNode) {
                return findNodesByIds(rootNode, ids);
            }
        }, timeout);
    }

    protected List<AccessibilityNodeInfo> getNodesByText(final String text, AccessibilityNodeInfo rootNode, int timeout) {
        return getNodes(rootNode, new NodesFinder() {
            @Override
            public List<AccessibilityNodeInfo> find(AccessibilityNodeInfo rootNode) {
                return rootNode.findAccessibilityNodeInfosByText(text);
            }
        }, timeout);
    }

    protected List<AccessibilityNodeInfo> getNodesByIdWithText(final String id, final String text, final AccessibilityNodeInfo rootNode, final int timeout) {
        return getNodes(rootNode, new NodesFinder() {
            @Override
            public List<AccessibilityNodeInfo> find(AccessibilityNodeInfo rootNode) {
                return findNodesByIdWithText(rootNode, id, text);
            }
        }, timeout);
    }

    // endregion

    // region Action methods

    /***
     * 在返回true之前不断retry， callable必须返回boolean
     */
    protected boolean tryUntilSuccess(Callable callable, int timeout) {
        boolean result;
        long startTime = System.currentTimeMillis();
        try {
            while (!(result = (boolean) callable.call()) && System.currentTimeMillis() - startTime < timeout) {
                SystemClock.sleep(FIND_NODE_INTERVAL);
            }
        } catch (Exception e) {
            KLog.i(TAG, "Error when tryUntilSuccess", e);

            result = false;
        }

        return result;
    }

    /***
     * 在返回true之前不断retry， callable必须返回boolean
     */
    protected boolean tryUntillSuccessByTimes(Callable callable, int maxTimes) {
        boolean result;
        int times = 1;
        try {
            while (!(result = (boolean) callable.call()) && times < maxTimes) {
                times++;

                SystemClock.sleep(FIND_NODE_INTERVAL);
            }
        } catch (Exception e) {
            KLog.i(TAG, "Error when tryUntilSuccess", e);

            result = false;
        }

        return result;
    }

    // region Basic actions

    /**
     * 点击node或node的parent中距离node最近的可点击项
     *
     * @param nodeInfo
     * @return
     */
    protected boolean click(AccessibilityNodeInfo nodeInfo) {
        return click(nodeInfo, NORMAL_ACTION_DEFAULT_TIMEOUT);
    }

    protected boolean click(final AccessibilityNodeInfo nodeInfo, int timeout) {
        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                AccessibilityNodeInfo clickItem = getClickableParent(nodeInfo);

                return clickItem != null && clickItem.isEnabled() && clickItem.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }, timeout);
    }

    /**
     * 点击node或node的parent中距离node最近的可点击项
     *
     * @param nodeInfo
     * @return
     */
    protected boolean longClick(AccessibilityNodeInfo nodeInfo) {
        return longClick(nodeInfo, NORMAL_ACTION_DEFAULT_TIMEOUT);
    }

    protected boolean longClick(final AccessibilityNodeInfo nodeInfo, int timeout) {
        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                AccessibilityNodeInfo clickItem = getClickableParent(nodeInfo);

                return clickItem != null && clickItem.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            }
        }, timeout);
    }

    protected boolean scroll(AccessibilityNodeInfo nodeInfo) {
        return scroll(nodeInfo, 0);
    }

    protected boolean scroll(final AccessibilityNodeInfo nodeInfo, int timeout) {
        if (nodeInfo == null) {
            return false;
        }

        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return nodeInfo.isScrollable() && nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) && AccessibilityUtil.waitScroll();
            }
        }, timeout);
    }

    protected boolean scrollBack(AccessibilityNodeInfo nodeInfo) {
        return scrollBack(nodeInfo, 0);
    }

    protected boolean scrollBack(final AccessibilityNodeInfo nodeInfo, int timeout) {
        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return nodeInfo.isScrollable() && nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) && AccessibilityUtil.waitScroll();
            }
        }, timeout);
    }

    protected boolean inputText(String id, String text) {
        AccessibilityNodeInfo node = getNodeById(id, null);
        if (node == null) {
            return false;
        }

        inputText(node, text);
        return true;
    }

    protected boolean inputText(AccessibilityNodeInfo nodeInfo, String text) {
        return inputText(nodeInfo, text, NORMAL_ACTION_DEFAULT_TIMEOUT);
    }

    protected boolean inputText(final AccessibilityNodeInfo nodeInfo, String text, int timeout) {
        if (nodeInfo == null) {
            return false;
        }

        final Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);

        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        }, timeout);
    }

    protected boolean check(AccessibilityNodeInfo nodeInfo) {
        return check(nodeInfo, NORMAL_ACTION_DEFAULT_TIMEOUT);
    }

    protected boolean check(final AccessibilityNodeInfo nodeInfo, int timeout) {
        if (nodeInfo == null) {
            return false;
        }

        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return nodeInfo.isCheckable() && (nodeInfo.isChecked() || nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK));
            }
        }, timeout);
    }

    protected boolean uncheck(AccessibilityNodeInfo nodeInfo) {
        return uncheck(nodeInfo, NORMAL_ACTION_DEFAULT_TIMEOUT);
    }

    protected boolean uncheck(final AccessibilityNodeInfo nodeInfo, int timeout) {
        if (nodeInfo == null) {
            return false;
        }

        return tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return nodeInfo.isCheckable() && (!nodeInfo.isChecked() || nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK));
            }
        }, timeout);
    }

    protected boolean systemBack() {
        return accessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    protected AccessibilityService accessibilityService() {
        return WechatAccessibilityService.getInstance();
    }

    // 用于屏幕键盘弹出时的后退
    protected boolean doubleSystemBack() {
        return accessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
                && Utils.sleep(150)
                && accessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    protected boolean systemHome() {
        return accessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

//    protected boolean sendEnterKey() {
//        return new SendEnterKeyEventShell().run();
//    }

    // endregion Basic actions

    // region Advanced actions

    protected boolean clickPreferenceLine(String title) {
        return clickPreferenceLine(title, null, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected boolean clickPreferenceLine(String title, AccessibilityNodeInfo rootNode) {
        return clickPreferenceLine(title, rootNode, FIND_NODE_DEFAULT_TIMEOUT);
    }

    protected boolean clickPreferenceLine(String title, AccessibilityNodeInfo rootNode, int timeout) {
        long startTime = System.currentTimeMillis();
        AccessibilityNodeInfo titleNode = getNodeByIdWithText(Resources.NODE_ANDROID_TITLE, title, rootNode, timeout);
        if (titleNode == null) {
            KLog.i(TAG, "Cannot find " + title + " line node");

            return false;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (!click(titleNode, (timeout - (int) elapsedTime))) {
            KLog.i(TAG, "Failed to click " + title + " line node");

            return false;
        }

        return true;
    }

    protected boolean waitForLoading() {
        int times = 0;
        while (getNodeById(Resources.LOADING_DIALOG, null) == null || ++times <= 10) {
            Utils.sleep(1000);
        }
        return times < 10;
    }

    protected boolean waitForLoading(String loadingNodeId) {
        return waitForLoading(loadingNodeId, WAIT_ACTION_DEFAULT_TIMEOUT, true);
    }

    protected boolean waitForLoading(final String loadingNodeId, int timeout) {
        return waitForLoading(loadingNodeId, timeout, true);
    }

    protected boolean waitForLoading(final String loadingNodeId, int timeout, boolean needBackSystemHome) {
        boolean result = tryUntilSuccess(new Callable() {
            @Override
            public Object call() throws Exception {
                return getNodeById(loadingNodeId, null, FIND_LOADING_NODE_TIMEOUT) == null;
            }
        }, timeout);

        if (!result && needBackSystemHome) {
            KLog.i(TAG, "Still loading after " + timeout + " millis");

            systemHome();
        }
        return result;
    }

    /**
     * Wait for the progress dialog to dismiss
     *
     * @param timeout timeout in millisecond
     * @return true if the progress dialog is dismissed.
     */
    protected boolean waitForProgressDialogFinish(int timeout) {
        AccessibilityUtil.waitWindowStateChange(Wechat.Resources.PROGRESS_DIALOG_CLASS_NAME, 5000);
        return waitForLoading(Resources.NODE_LOADING_BACK, timeout, true);
    }

    /**
     * Waiting for web view load finish
     *
     * @return true if url is load finished
     */
    protected boolean waitForWebViewLoadFinish() {
        AccessibilityUtil.waitWindowStateChange(Resources.WEBVIEW_ACTIVITY_CLASS_NAME, 5000);
        // 微信一般等待10秒就 timeout. 我们这里等待 15s 如果, 如果还不行就返回 true
        return waitForLoading(Resources.NODE_WEBVIEW_PROGRESSBAR, 15000, false);
    }

    protected void waitForWindowChange(String windowName) {
        AccessibilityUtil.waitWindowStateChange(windowName, 5000);
    }

    protected Boolean dismissSystemPermissionDialog() {
        AccessibilityNodeInfo permissionMessageNode = getNodeById(Resources.NODE_SYSTEM_PERMISSION_MESSAGE, null, 0);
        if (permissionMessageNode == null) {
            return null;
        }

        AccessibilityNodeInfo doNotAskNode = getNodeById(Resources.NODE_SYSTEM_PERMISSION_DO_NOT_ASK, null, 0);
        if (doNotAskNode != null && !check(doNotAskNode)) {
            KLog.i(TAG, "Failed to click do not ask checkbox");

            return false;
        }

        String permissionMessage = permissionMessageNode.getText().toString();
        // 除位置信息外，需要给微信其他权限
        if ("要允许微信使用此设备的位置信息吗？".equals(permissionMessage)) {
            AccessibilityNodeInfo denyPermissionNode = getNodeById(Resources.NODE_SYSTEM_PERMISSION_DENY, null, 0);
            if (!click(denyPermissionNode)) {
                KLog.i(TAG, "Failed to click deny permission button");

                return false;
            }

            return true;
        } else {
            AccessibilityNodeInfo allowPermissionNode = getNodeById(Resources.NODE_SYSTEM_PERMISSION_ALLOW, null, 0);
            if (!click(allowPermissionNode)) {
                KLog.i(TAG, "Failed to click allow permission button");

                return false;
            }

            return true;
        }
    }

    protected Boolean dismissSystemHangDialog() {
        isDismissingSystemHangDialog = true;

        AccessibilityNodeInfo dialogContentNode = getNodeById("android:id/message", null, 0);
        if (dialogContentNode == null) {
            isDismissingSystemHangDialog = false;

            return null;
        }

        String dialogContent = dialogContentNode.getText().toString();
        if (!dialogContent.contains("无响应。")) {
            isDismissingSystemHangDialog = false;

            return null;
        }

        KLog.i(TAG, "Detect app no responding: " + dialogContent);

        AccessibilityNodeInfo waitButtonNode = getNodeById("android:id/button1", null, 0);
        if (!click(waitButtonNode)) {
            KLog.i(TAG, "Failed to click close button for system hang dialog");
            isDismissingSystemHangDialog = false;

            return false;
        }

        AccessibilityUtil.waitWindowStateChange(1000);

        isDismissingSystemHangDialog = false;

        return true;
    }

    protected Boolean dismissWechatStandardDialog() {
        AccessibilityNodeInfo dialogConfirmNode = getNodeById(Resources.NODE_DIALOG_CONFIRM, null, 0);
        AccessibilityNodeInfo dialogCancelNode = getNodeById(Resources.NODE_DIALOG_CANCEL, null, 0);
        if (dialogConfirmNode == null && dialogCancelNode == null) {
            return null;
        }

        // region 如果只有一个按钮那么直接点击
        if (dialogConfirmNode == null) {
            if (!click(dialogCancelNode)) {
                KLog.i(TAG, "Failed to click dialog cancel button");

                return false;
            }

            return true;
        }

        if (dialogCancelNode == null) {
            if (!click(dialogConfirmNode)) {
                KLog.i(TAG, "Failed to click dialog confirm button");

                return false;
            }

            return true;
        }

        // endregion

        // 两个都有的时候要考虑弹框内容
        AccessibilityNodeInfo titleItem = getNodeById(Resources.NODE_DIALOG_TITLE, null, 0);
        AccessibilityNodeInfo contentItem = getNodeById(Resources.NODE_DIALOG_CONTENT, null, 0);

        String title = titleItem == null || titleItem.getText() == null ? "" : titleItem.getText().toString();
        String content = contentItem == null || contentItem.getText() == null ? "" : contentItem.getText().toString();

        // 处理微信升级提示 或 编辑退出确认 或 退出 京东商城
        if (Resources.NODE_DIALOG_CONTENT_CANCEL_INSTALL.equals(content)
                || Resources.NODE_DIALOG_CONTENT_QUIT_TRANSACTION.equals(content)
                || Resources.NODE_DIALOG_CONTENT_QUIT_MALL.equals(title)) {
            if (!click(dialogConfirmNode)) {
                KLog.i(TAG, "Failed to click confirm dialog button, dialogTitle: " + title + ", dialogContent: " + content);

                return false;
            }

            return true;
        } else {
            if (!click(dialogCancelNode)) {
                KLog.i(TAG, "Failed to click cancel dialog button, dialogTitle: " + title + ", dialogContent: " + content);

                return false;
            }

            return true;
        }
    }

    protected Boolean dismissWechatNonStandardDialog() {
        AccessibilityNodeInfo backNode = getNodeByIds(new String[]{
                Resources.NODE_SHAKE_ATTENTION_ACKNOWLEDGE_BUTTON, // 第一次打开摇一摇时的"请注意"弹框
                Resources.NODE_DIALOG_ANR_WAITING // 不知道是什么按钮
        }, null, 0);

        if (backNode == null) {
            return null;
        }

        if (!click(backNode)) {
            KLog.i(TAG, "Failed to click back button");

            return false;
        }

        return true;
    }

    protected boolean backToHomePage() {
        int triedTimes = 0;
        while (triedTimes < 10) {
            triedTimes++;

            AccessibilityNodeInfo rootNode = accessibilityService().getRootInActiveWindow();
            String rootNodePackageName = "";
            if (rootNode == null) {
                KLog.i(TAG, "Cannot find root node when trying to go to wechat main page");
            } else {
                rootNodePackageName = rootNode.getPackageName().toString();
            }

            if ("android".equals(rootNodePackageName)) {
                dismissSystemHangDialog();

                continue;
            }

            Boolean systemPermissionResult = dismissSystemPermissionDialog();
            if (systemPermissionResult != null) {
                if (systemPermissionResult && AccessibilityUtil.waitWindowStateChange()) {
                    continue;
                }

                return false;
            }

            if (rootNode == null || !Wechat.WECHAT_PACKAGE_NAME.equals(rootNodePackageName)) {
                if (!SYS_DESKTOP_PACKAGE_NAMES.contains(rootNodePackageName)) {
                    KLog.i(TAG, "The front most window is not wechat or desktop: " + rootNodePackageName);
                    if (!systemHome()) {
                        return false;
                    }

                    SystemClock.sleep(100);
                }

                startWeChat();

                AccessibilityUtil.waitWindowStateChange(Resources.LAUNCHER_UI_CLASS, 15 * 1000);

                continue;
            }

            KLog.e("------>>>>>");
            if (getNodeByIdWithText(Resources.HomePage.NODE_TAB_TEXT, Resources.HomePage.TAB_WECHAT_TEXT, null, 100) != null) {
//                if (getNodeById(ConversationConstant.NODE_CONVERSATION_PLUS_BUTTON, null, 0) == null) {
//                    return true;
//                }

               /* if (!systemBack()) {
                    return false;
                }

                return tryUntilSuccess(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return null;
//                        return getNodeById(ConversationConstant.NODE_CONVERSATION_PLUS_BUTTON, null, 0) == null;
                    }
                }, FIND_NODE_DEFAULT_TIMEOUT);*/
                return true;
            }

            Boolean wechatStandardDialogResult = dismissWechatStandardDialog();
            if (wechatStandardDialogResult != null) {
                if (wechatStandardDialogResult && AccessibilityUtil.waitWindowStateChange()) {
                    continue;
                }

                return false;
            }

            Boolean wechatNonStandardDialogResult = dismissWechatNonStandardDialog();
            if (wechatNonStandardDialogResult != null) {
                if (wechatNonStandardDialogResult && AccessibilityUtil.waitWindowStateChange()) {
                    continue;
                }

                return false;
            }

            if (!systemBack() || !AccessibilityUtil.waitWindowStateChange()) {
                rootNode = accessibilityService().getRootInActiveWindow();
                if (rootNode == null) {
                    return false;
                }

                rootNodePackageName = rootNode.getPackageName().toString();
                if (!Wechat.WECHAT_PACKAGE_NAME.equals(rootNodePackageName)) {
                    continue;
                }
                return false;
            }
        }

        return false;
    }

//    protected boolean switchToGooglePinyinIme() {
//        AnyShell readGooglePinyinSettingShell = new AnyShell("cat " + GOOGLE_PINYIN_PREFERENCE_FILE);
//        if (!readGooglePinyinSettingShell.run()) {
//            KLog.i(TAG, "Failed to read google pinyin preferences: " + readGooglePinyinSettingShell.toString());
//
//            return false;
//        }
//
//        List<String> googlePinyinSettings = readGooglePinyinSettingShell.getStdOut();
//        boolean settingsChanged = false;
//        List<String> newSettings = new ArrayList<>();
//        for (String setting : googlePinyinSettings) {
//            if (setting != null && setting.contains("ACTIVE_LANGUAGE_113646356") && !setting.contains("zh-CN")) {
//                newSettings.add("    <string name=\"ACTIVE_LANGUAGE_113646356\">zh-CN</string>");
//                settingsChanged = true;
//            } else {
//                newSettings.add(setting);
//            }
//        }
//
//        if (settingsChanged) {
//            AnyShell stopGooglePinyinShell = new AnyShell("am force-stop " + GOOGLE_PINYIN_PACKAGE_NAME);
//            if (!stopGooglePinyinShell.run()) {
//                KLog.i(TAG, "Failed to stop google pinyin: " + stopGooglePinyinShell.toString());
//
//                return false;
//            }
//
//            if (!FileUtil.write2File(TextUtils.join(newSettings, System.lineSeparator()), PathUtil.QUN_EXTERNAL_CACHE_DIR, "tmp_google_pinyin_preferences.xml")) {
//                KLog.i(TAG, "Failed to write tmp google pinyin preferences file");
//
//                return false;
//            }
//
//            String tmpSettingsFile = PathUtil.combine(PathUtil.QUN_EXTERNAL_CACHE_DIR, "tmp_google_pinyin_preferences.xml");
//            AnyShell writeSettingsShell = new AnyShell("cat " + tmpSettingsFile + " > " + GOOGLE_PINYIN_PREFERENCE_FILE);
//            if (!writeSettingsShell.run()) {
//                KLog.i(TAG, "Failed to overwrite google pinyin preferences file: " + writeSettingsShell.toString());
//
//                return false;
//            }
//        }
//
//        String currentIme = Settings.Secure.getString(QunUtil.getCurrentApplication().getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
//        if (!GOOGLE_PINYIN_IME_ID.equals(currentIme)) {
//            PutSettingsShell setImeShell = new PutSettingsShell(PutSettingsShell.NAMESPACE_SECURE, Settings.Secure.DEFAULT_INPUT_METHOD, GOOGLE_PINYIN_IME_ID);
//            if (!setImeShell.run()) {
//                KLog.i(TAG, "Failed to run set ime shell: " + setImeShell.toString());
//
//                return false;
//            }
//        }
//
//        return true;
//    }

    // endregion Advanced actions

    // endregion

    // region Util methods

    private AccessibilityNodeInfo ensureRootNode(AccessibilityNodeInfo rootNode) {
        if (rootNode != null) {
            return rootNode;
        }

        return this.accessibilityService().getRootInActiveWindow();
    }

    private List<AccessibilityNodeInfo> getNodes(AccessibilityNodeInfo rootNode, NodesFinder nodesFinder, int timeout) {
        long startTime = System.currentTimeMillis();
        while (true) {
            AccessibilityNodeInfo latestRootNode = ensureRootNode(rootNode);
            if (latestRootNode != null) {
                if (!isDismissingSystemHangDialog) {
                    if ("android".equals(latestRootNode.getPackageName().toString())) {
                        dismissSystemHangDialog();
                    }
                }

                List<AccessibilityNodeInfo> resultNodes;
                if ((resultNodes = nodesFinder.find(latestRootNode)).size() > 0) {
                    KLog.d(TAG, "findNode duration: " + (System.currentTimeMillis() - startTime));
                    return resultNodes;
                }

                // 是在可以滚动的view里面并且第一次没找到，则从view头部重新滚动到底来查找
                while (scrollBack(latestRootNode))
                    ;

                while (scroll(latestRootNode)) {
                    if ((resultNodes = nodesFinder.find(latestRootNode)).size() > 0) {
                        KLog.d(TAG, "findNode duration: " + (System.currentTimeMillis() - startTime));
                        return resultNodes;
                    }
                }
            }

            if (System.currentTimeMillis() - startTime >= timeout) {
                KLog.d(TAG, "findNode fail duration: " + (System.currentTimeMillis() - startTime));
                return Collections.emptyList();
            }

            SystemClock.sleep(FIND_NODE_INTERVAL);
        }
    }

    private List<AccessibilityNodeInfo> findNodesByIds(AccessibilityNodeInfo rootNode, String[] ids) {
        for (String id : ids) {
            List<AccessibilityNodeInfo> resultNodes = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (resultNodes.size() > 0) {
                return resultNodes;
            }
        }

        return Collections.emptyList();
    }

    private List<AccessibilityNodeInfo> findNodesByIdWithText(AccessibilityNodeInfo rootNode, String id, String text) {
        List<AccessibilityNodeInfo> resultNodes;

        resultNodes = rootNode.findAccessibilityNodeInfosByViewId(id);
        if (!TextUtils.isEmpty(text)) {
            List<AccessibilityNodeInfo> tmpResultNodes = new ArrayList<>();
            for (AccessibilityNodeInfo node : resultNodes) {
                if (node.getText() != null && text.equals(node.getText().toString())) {
                    tmpResultNodes.add(node);
                }
            }

            resultNodes = tmpResultNodes;
        }

        return resultNodes;
    }

    // endregion

    // endregion

    private interface NodesFinder {
        List<AccessibilityNodeInfo> find(AccessibilityNodeInfo rootNode);
    }
}
