package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import de.robv.android.xposed.XposedHelpers

class CreateGroupAction : Action {
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val members = ArrayList<Any?>()
        members.addAll(args.asList())
        var request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.HookMethodFunctions.Group.CreateGroupRequest, Wechat.WECHAT_CLASSLOADER), members)
        HookUtils.enqueueNetScene(request, 0)
        // RemoteRespHooks.registerOnResponseListener(119, {response->})
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:createGroup"
    }
}