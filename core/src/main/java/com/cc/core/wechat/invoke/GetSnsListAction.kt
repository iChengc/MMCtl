package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.Wechat.Hook.Sns.SnsGetRequest
import com.cc.core.wechat.hook.RemoteRespHooks
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.newInstance
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class GetSnsListAction : Action {
    private val lock = ArrayBlockingQueue<String>(1)
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        var snsId = if (args.isEmpty()){0L} else {(args[0] as Double).toLong()}
        val request = newInstance(findClass(SnsGetRequest, Wechat.WECHAT_CLASSLOADER), snsId)
        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(211){response ->
            if (response == null) {
                lock.offer("")
            }
            lock.offer("success")
        }

        val result = lock.poll(30, TimeUnit.SECONDS)
        if (TextUtils.isEmpty(result)) {
            return ActionResult.failedResult(actionId, "Failed to get sns list.")
        }

        return ActionResult.successResult(actionId, SnsUtils.getSnsList(snsId))
    }

    override fun key(): String? {
        return "wechat:getSnsList"
    }
}