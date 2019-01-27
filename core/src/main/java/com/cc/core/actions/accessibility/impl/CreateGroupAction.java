package com.cc.core.actions.accessibility.impl;

import android.view.accessibility.AccessibilityNodeInfo;

import com.cc.core.actions.ActionResult;
import com.cc.core.actions.Actions;
import com.cc.core.actions.accessibility.AccessibilityAction;
import com.cc.core.command.impl.OpenWechatAction;
import com.cc.core.utils.Utils;
import com.cc.core.wechat.Wechat;
import com.cc.core.wechat.invoke.StartActivityAction;

import java.util.List;

public class CreateGroupAction extends AccessibilityAction {
    @Override
    public ActionResult execute(Object... args) {
        /*backToHomePage();

        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_INPUT);
        performClick(Wechat.Resources.Search.ADD_FRIEND_SEARCH_MAIN_INPUT);*/
        if (args == null || args.length <= 2) {
            return ActionResult.failedResult("Not enough wechat to create a group");
        }

        ActionResult result = Actions.execute(OpenWechatAction.class);
        if (!result.isSuccess()) {
            return result;
        }
        waitForWindowChange(Wechat.Resources.LAUNCHER_UI_CLASS);

        performClick(Wechat.Resources.HomePage.PLUS_BTN);
        List<AccessibilityNodeInfo> nodes = getNodesByIdWithText(Wechat.Resources.HomePage.POP_PLUS_ITEM, "发起群聊", null, 500);
        if (Utils.isEmpty(nodes)) {
            return ActionResult.failedResult("Can not find create group btn");
        }
        performClick(nodes.get(0));
        waitForWindowChange(Wechat.Resources.ContactInfo.SELECT_CONTACT_UI);
        if (!result.isSuccess()) {
            return result;
        }

        boolean success;
        StringBuilder sb = new StringBuilder("failed wechat {\n");
        for (Object s : args) {
            inputText(Wechat.Resources.ContactInfo.SELECT_CONTACT_INPUT, s.toString());
            success = performClickListItem(Wechat.Resources.ContactInfo.SELECT_CONTACT_INPUT, 1);
            if (!success) {
                sb.append(s.toString())
                        .append(";");
            }
        }
        sb.append("}\n");
        performClick(Wechat.Resources.ContactInfo.SELECT_CONTACT_CONFIRM_BTN);
        //backToHomePage();
        return ActionResult.successResult(sb.toString());
    }

    @Override
    public String key() {
        return "createGroup";
    }
}
