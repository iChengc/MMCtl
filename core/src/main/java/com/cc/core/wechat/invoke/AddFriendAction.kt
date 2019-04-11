package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.RemoteRespHooks
import de.robv.android.xposed.XposedHelpers
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class AddFriendAction : Action {
    private val lock = ArrayBlockingQueue<String>(2)
    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No mobile phone ")
        }

        val wechatId = getWechatId(args[0].toString())
        if (TextUtils.isEmpty(wechatId)) {
            return ActionResult.failedResult(actionId, "can not find user:" + args[0])
        }

        val sayHi = if (args.size > 1) {
            args[1].toString()
        } else {
            "你好啊！"
        }
        sendRequest(wechatId, sayHi)

        return ActionResult.successResult(actionId)
    }

    private fun getWechatId(phone: String): String? {
        val request = XposedHelpers.findClass(Wechat.Hook.NetScene.SearchFriendNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(String::class.java, Int::class.javaPrimitiveType)
                .newInstance(phone, 0)

        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(106) { response ->
            val friendDetails = XposedHelpers.getObjectField(response, Wechat.Hook.NetScene.NetSceneCmdResponseBodyKey)
            val antispamTicket = XposedHelpers.getObjectField(friendDetails, Wechat.Hook.AddFriend.AntispamTicket)

            var wechatId: String
            if (XposedHelpers.getIntField(friendDetails, Wechat.Hook.AddFriend.RelationType) == 1) {
                val wechatIdBody = XposedHelpers.getObjectField(friendDetails, Wechat.Hook.AddFriend.WechatId)
                wechatId = XposedHelpers.getObjectField(wechatIdBody, Wechat.Hook.NetScene.NetSceneResponseStringBooleanValueKey) as String
                if (wechatId.startsWith("wxid") && wechatId.length == 18 && wechatId.elementAt(4) != '_') {
                    val sb = StringBuilder(wechatId)
                    sb.insert(4, "_")
                    wechatId = sb.toString()
                }
            } else {
                val wechatIdBody = XposedHelpers.getObjectField(friendDetails, Wechat.Hook.AddFriend.DecriptyWechatId)
                wechatId = XposedHelpers.getObjectField(wechatIdBody, Wechat.Hook.NetScene.NetSceneResponseStringBooleanValueKey) as String
            }

            if (TextUtils.isEmpty(wechatId)) {
                lock.offer("")
            } else {
                verifyUser(wechatId, if (antispamTicket == null) {""} else {antispamTicket as String})
            }
        }
        return lock.poll(30, TimeUnit.SECONDS)
    }

    private fun verifyUser(wechatId: String?, antispamTicket: String) {
        val args = ArrayList<String?>()
        args.add(wechatId)
        val args2 = ArrayList<Int>()
        args2.add(15)
        val args3 = ArrayList<String>()
        args3.add(antispamTicket)

        val request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.Hook.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER),
                1, args, args2, args3, "", "", null, "", "")

        val isStranger = null == antispamTicket || "" == antispamTicket
        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(137) {

            lock.offer(wechatId)
        }
    }

    private fun sendRequest(wechatId: String?, sayHi: String) {
        val args = ArrayList<String?>()
        args.add(wechatId)
        val args2 = ArrayList<Int>()
        args2.add(15)
        val mapArgs = HashMap<String?, Int>()
        mapArgs[wechatId] = 0

        val request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.Hook.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER),
                2, args, args2, null, sayHi, "", mapArgs, null, "")

        HookUtils.enqueueNetScene(request, 0)
    }

    override fun key(): String? {
        return "wechat:addFriend"
    }
}