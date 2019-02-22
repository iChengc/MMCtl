package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import de.robv.android.xposed.XposedHelpers

class TestAction : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        //XposedHelpers.callMethod(HookUtils.getNetscenQueue(), "", )
        return null
    }

    override fun key(): String? {
        return "wechat:test"
    }
}