package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.RemoteRespHooks
import de.robv.android.xposed.XposedHelpers
import java.util.concurrent.ArrayBlockingQueue

class CreateGroupAction : Action {
    private val lock = ArrayBlockingQueue<String>(1)
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val members = ArrayList<Any?>()
        members.addAll(args.asList())

        var request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.HookMethodFunctions.Group.CreateGroupRequest, Wechat.WECHAT_CLASSLOADER), "", members)
        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(119) { response->
            var groupWechatId = XposedHelpers.getObjectField(response, Wechat.HookMethodFunctions.Group.CreateGroupRequest) as String
            lock.offer(groupWechatId)
        }

        val groupWecharId = lock.poll()
        return if (TextUtils.isEmpty(groupWecharId)) {
            ActionResult.failedResult(actionId, "")
        } else if (!StrUtils.isGroupWechatId(groupWecharId)) {
            ActionResult.Companion.failedResult(actionId, groupWecharId)
        } else {
            ActionResult.successResult(actionId, groupWecharId)
        }
    }

    override fun key(): String? {
        return "wechat:createGroup"
    }
}