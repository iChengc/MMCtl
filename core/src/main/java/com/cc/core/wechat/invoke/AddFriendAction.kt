package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.log.KLog
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.NetsceneQueueHooks
import de.robv.android.xposed.XposedHelpers

class AddFriendAction : Action {
    override fun execute(vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult("No mobile phone ")
        }

//        2
//        ["xnhjcc"]
//        [15]
//        null
//        "我是娇娇JoJo"
//        ""
//        {"xnhjcc":0}
//        null
//        ""
//
        searchFriend("15921123483")
        val wechatId = getWechatId(args[0].toString())
        val sayHi = if (args.size > 1){args[1].toString()} else {"你好啊！"}
        sendRequest(wechatId, sayHi)

        return ActionResult.successResult()
    }

    private fun searchFriend(phone:String) {
        val request = XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.SearchFriendNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(String::class.java, Long::class.javaPrimitiveType, Int::class.javaPrimitiveType,Int::class.javaPrimitiveType, String::class.java)
                .newInstance(phone, 1, 0, 16, "")

        HookUtils.enqueueNetScene(request, 0)
        NetsceneQueueHooks.registerSceneListener(request) { type, code, message ->
            KLog.e("----->>> Type:" + type + " Code:" + code + "  Message:" + message)
        }
    }

    private fun getWechatId(phone:String) : String {
        return "xnhjcc"
    }

    private fun sendRequest(wechatId: String, sayHi: String) {
        val args = ArrayList<String>()
        args.add(wechatId)
        val args2 = ArrayList<Int>()
        args2.add(15)
        val mapArgs = HashMap<String, Int>()
        mapArgs[wechatId] = 0


        val request = XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(Int::class.javaPrimitiveType, List::class.java, List::class.java, List::class.java, String::class.java,
                        String::class.java, Map::class.java, String::class.java, String::class.java)
                .newInstance(2, args, args2, null, sayHi, "", mapArgs, null, "")

        HookUtils.enqueueNetScene(request, 0)
        NetsceneQueueHooks.registerSceneListener(request) { type, code, message ->
            KLog.e("----->>> Type:" + type + " Code:" + code + "  Message:" + message)
        }
    }

    override fun key(): String? {
        return "wechat:addFriend"
    }
}