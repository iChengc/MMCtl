package com.cc.core.accessibility.impl

import android.text.TextUtils
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.accessibility.AccessibilityAction
import com.cc.core.command.impl.OpenWechatAction
import com.cc.core.utils.Utils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.invoke.StartActivityAction

class AddFriendActionByAcc : AccessibilityAction() {
    override fun doAction(actionId: String, vararg args: Any?): ActionResult {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No phone was found!")
        }
        
        val hi = if (args.size > 1) {
            args[1].toString()
        } else {
            ""
        }
        val result = try2PerformAction(actionId, args[0].toString(), hi)

        backToHomePage()
        if (!result.isSuccess()) {
            return ActionResult.failedResult(actionId, "failed to add friend!")
        }
        return ActionResult.successResult(actionId)
    }

    private fun try2PerformAction(actionId: String, wechatId: String, hi: String?): ActionResult {
        var tryTimes = 0
        var result: ActionResult
        do {
            result = performAction(actionId, wechatId, hi)
        } while (++tryTimes < 5 && !result.isSuccess())

        return result
    }

    private fun performAction(actionId: String, wechatId: String, hi: String?): ActionResult {
        var result = Actions.execute(OpenWechatAction::class.java, actionId)
        if (!result!!.isSuccess()) {
            return result
        }
        waitForWindowChange(Wechat.Resources.LAUNCHER_UI_CLASS)
        
        var success = backToHomePage()
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to back home page")
        }
        result = Actions.execute(StartActivityAction::class.java, "open_add_activity", Wechat.Resources.Search.FTS_ADD_FRIEND_UI)
        if (!result!!.isSuccess()) {
            return result
        }
        success = inputText(Wechat.Resources.Search.FTS_ADD_FRIEND_SEARCH_INPUT, wechatId)
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to input search text:$wechatId")
        }
        Utils.sleep(500)
        success = performClickListItem(Wechat.Resources.Search.FTS_ADD_FRIEND_LIST, 0)
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to back home page")
        }
        waitForWindowChange(Wechat.Resources.ContactInfo.CONTACT_INFO_UI)
        success = performClickListItem(Wechat.Resources.ContactInfo.CONTACT_INFO_LIST, Wechat.Resources.ContactInfo.ADD_FRIEND_BTN, "添加到通讯录")
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to click add friend btn")
        }
        waitForWindowChange(Wechat.Resources.SayHiUI.SAY_HI_UI)

        if (!TextUtils.isEmpty(hi)) {
            inputText(Wechat.Resources.SayHiUI.SAY_HI_INPUT, hi)
        }

        success = performClick(Wechat.Resources.SayHiUI.SEND_BTN)
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to click send button")
        }

        waitForWindowChange(Wechat.Resources.ContactInfo.CONTACT_INFO_UI)
        if (!success) {
            return ActionResult.failedResult(actionId, "failed to waite for loading")
        }
        success = backToHomePage()
        return if (!success) {
            ActionResult.failedResult(actionId, "failed to back home page")
        } else ActionResult.successResult(actionId)
    }

    override fun key(): String {
        return "accessibility:addFriend"
    }
}