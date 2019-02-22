package com.cc.core.actions.accessibility.impl

import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.actions.accessibility.AccessibilityAction
import com.cc.core.command.impl.OpenWechatAction
import com.cc.core.utils.Utils
import com.cc.core.wechat.Wechat

class CreateGroupAction : AccessibilityAction() {
    override fun execute(actiongId : String, vararg args: Any?): ActionResult? {
        /*backToHomePage();

        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_INPUT);
        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_MAIN_INPUT);*/
        if (args.size <= 2) {
            return ActionResult.failedResult(actiongId, "Not enough wechat to create a group")
        }

        val result = Actions.execute(OpenWechatAction::class.java, actiongId)
        if (!result!!.isSuccess()) {
            return result
        }
        waitForWindowChange(Wechat.Resources.LAUNCHER_UI_CLASS)

        performClick(Wechat.Resources.HomePage.PLUS_BTN)
        val nodes = getNodesByIdWithText(Wechat.Resources.HomePage.POP_PLUS_ITEM, "发起群聊", null, 500)
        if (Utils.isEmpty(nodes)) {
            return ActionResult.failedResult(actiongId, "Can not find create group btn")
        }
        performClick(nodes[0])
        waitForWindowChange(Wechat.Resources.ContactInfo.SELECT_CONTACT_UI)
        if (!result.isSuccess()) {
            return result
        }

        var success: Boolean
        val sb = StringBuilder("failed wechat {\n")
        for (s in args) {
            inputText(Wechat.Resources.ContactInfo.SELECT_CONTACT_INPUT, s.toString())
            success = performClickListItem(Wechat.Resources.ContactInfo.SELECT_CONTACT_INPUT, 1)
            if (!success) {
                sb.append(s.toString())
                        .append(";")
            }
        }
        sb.append("}\n")
        performClick(Wechat.Resources.ContactInfo.SELECT_CONTACT_CONFIRM_BTN)
        //backToHomePage();
        return ActionResult.successResult(sb.toString())
    }

    override fun key(): String? {
        return "accessibility:createGroup"
    }
}