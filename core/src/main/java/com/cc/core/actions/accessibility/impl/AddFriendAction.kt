package com.cc.core.actions.accessibility.impl

import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.actions.accessibility.AccessibilityAction
import com.cc.core.command.impl.OpenWechatAction
import com.cc.core.utils.Utils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.invoke.StartActivityAction

class AddFriendAction : AccessibilityAction() {
    override fun execute(actiongId : String, vararg args: Any?): ActionResult? {
        /*backToHomePage();
        performClick(Wechat.Resources.HomePage.PLUS_BTN);
        List<AccessibilityNodeInfo> nodes = getNodesByIdWithText(Wechat.Resources.HomePage.POP_PLUS_ITEM, "添加朋友", null, 500);
        if (Utils.isEmpty(nodes)) {
            return ActionResult.failedResult("Can not find add friend btn");
        }
        performClick(nodes.get(0));
        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_INPUT);
        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_MAIN_INPUT);*/
        var result = Actions.execute(OpenWechatAction::class.java, actiongId)
        if (!result!!.isSuccess()) {
            return result
        }
        waitForWindowChange(Wechat.Resources.LAUNCHER_UI_CLASS)
        val sb = StringBuilder("failed wechat {\n")
        for (o in args) {
            result = try2PerformAction(actiongId, o.toString())
            if (!result.isSuccess()) {
                sb.append(o.toString())
                sb.append(":").append(result.message).append("\n")
            }
        }
        sb.append("}\n")
        backToHomePage()
        return ActionResult.successResult(sb.toString())
    }

    private fun try2PerformAction(actiongId: String, wechatId: String): ActionResult {
        var tryTimes = 0
        var result: ActionResult
        do {
            result = performAction(actiongId, wechatId)
        } while (++tryTimes < 5 && !result.isSuccess())

        return result
    }

    private fun performAction(actiongId: String, wechatId: String): ActionResult {
        var success = backToHomePage()
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to back home page")
        }
        val result = Actions.execute(StartActivityAction::class.java, Wechat.Resources.Search.FTS_ADD_FRIEND_UI)
        if (!result!!.isSuccess()) {
            return result
        }
        success = inputText(Wechat.Resources.Search.FTS_ADD_FRIEND_SEARCH_INPUT, wechatId)
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to input search text:$wechatId")
        }
        Utils.sleep(1000)
        success = performClickListItem(Wechat.Resources.Search.FTS_ADD_FRIEND_LIST, 0)
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to back home page")
        }
        waitForWindowChange(Wechat.Resources.ContactInfo.CONTACT_INFO_UI)
        success = performClick(Wechat.Resources.ContactInfo.ADD_FRIEND_BTN)
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to click add friend btn")
        }
        waitForWindowChange(Wechat.Resources.SayHiUI.SAY_HI_UI)
        success = performClick(Wechat.Resources.SayHiUI.SEND_BTN)
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to click send button")
        }
        success = waitForLoading()
        if (!success) {
            return ActionResult.failedResult(actiongId, "failed to waite for loading")
        }
        success = backToHomePage()
        return if (!success) {
            ActionResult.failedResult(actiongId, "failed to back home page")
        } else ActionResult.successResult(actiongId)
    }

    override fun key(): String {
        return "accessibility:addFriend"
    }
}