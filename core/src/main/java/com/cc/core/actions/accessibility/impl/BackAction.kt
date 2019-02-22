package com.cc.core.actions.accessibility.impl

import com.cc.core.actions.ActionResult
import com.cc.core.actions.accessibility.AccessibilityAction

class BackAction : AccessibilityAction() {
    override fun execute(actiongId : String, vararg args: Any?): ActionResult? {
        val result = systemBack()
        return if (result) ActionResult.successResult(actiongId) else ActionResult.failedResult(actiongId, "can not execute back")
    }

    override fun key(): String {
        return "back"
    }
}