package com.cc.core.actions.accessibility.impl

import com.cc.core.actions.ActionResult
import com.cc.core.actions.accessibility.AccessibilityAction

class BackAction : AccessibilityAction() {
    override fun execute(vararg args: Any?): ActionResult? {
        val result = systemBack()
        return if (result) ActionResult.successResult() else ActionResult.failedResult("can not execute back")
    }

    override fun key(): String {
        return "back"
    }
}