package com.cc.core.accessibility.impl

import com.cc.core.actions.ActionResult
import com.cc.core.accessibility.AccessibilityAction

class BackAction : AccessibilityAction() {

    override fun doAction(actionId: String, vararg args: Any?): ActionResult {
        val result = systemBack()
        return if (result) ActionResult.successResult(actionId) else ActionResult.failedResult(actionId, "can not execute back")
    }

    override fun key(): String {
        return "back"
    }
}