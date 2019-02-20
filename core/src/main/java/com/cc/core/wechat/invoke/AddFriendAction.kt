package com.cc.core.wechat.invoke

import android.text.TextUtils
import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.log.KLog
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import com.cc.core.wechat.hook.RemoteRespHooks
import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

class AddFriendAction : Action {
    private val lock = ArrayBlockingQueue<String>(2)
    override fun execute(vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult("No mobile phone ")
        }

        val wechatId = getWechatId(args[0].toString())
        if (TextUtils.isEmpty(wechatId)) {
            return ActionResult.failedResult("can not find user:" + args[0])
        }

        val sayHi = if (args.size > 1) {
            args[1].toString()
        } else {
            "你好啊！"
        }
        sendRequest(wechatId, sayHi)

        return ActionResult.successResult()
    }

    private fun getWechatId(phone: String): String? {
        val request = XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.SearchFriendNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(String::class.java, Int::class.javaPrimitiveType)
                .newInstance(phone, 0)

        HookUtils.enqueueNetScene(request, 0)
        RemoteRespHooks.registerOnResponseListener(106) { response ->
            if ("6.7.2".equals(Wechat.WechatVersion)) {
                val jsonObject = JSONObject(response).optJSONObject("dVG").optJSONObject("dUj")
                var wechatId: String
                if (jsonObject.optInt("eWZ") == 1) {
                    wechatId = jsonObject.optJSONObject("sgh").optString("sVc")
                    if (wechatId.startsWith("wxid") && wechatId.length == 18 && wechatId.elementAt(4) != '_') {
                        val sb = StringBuilder(wechatId)
                        sb.insert(4, "_")
                        wechatId = sb.toString()
                    }
                } else {
                    wechatId = jsonObject.optJSONObject("sgx").optString("sVc")
                }

                if (TextUtils.isEmpty(wechatId)) {
                    lock.offer(wechatId)
                } else {
                    verifyUser(wechatId, jsonObject.optString("sqc"))
                }
            } else if ("7.0.3".equals(Wechat.WechatVersion)) {
                val jsonObject = JSONObject(response).optJSONObject("feW").optJSONObject("fdy")
                var wechatId: String
                if (jsonObject.optInt("gfi") == 1) {
                    wechatId = jsonObject.optJSONObject("vqP").optString("wiP")
                    if (wechatId.startsWith("wxid") && wechatId.length == 18 && wechatId.elementAt(4) != '_') {
                        val sb = StringBuilder(wechatId)
                        sb.insert(4, "_")
                        wechatId = sb.toString()
                    }
                } else {
                    wechatId = jsonObject.optJSONObject("vrl").optString("wiP")
                }

                if (TextUtils.isEmpty(wechatId)) {
                    lock.offer(wechatId)
                } else {
                    verifyUser(wechatId, jsonObject.optString("vAm"))
                }
            }
        }
        return lock.poll(30, TimeUnit.SECONDS)
    }

    private fun verifyUser(wechatId: String?, antispamTicket: String) {
        /* =====>>>>>> 1
          ["xnhjcc"]
          [15]
          ["v2_936b8a81d0373f569028ea01e15bbb5e76f73c98c7fbfb8778f6d8998d224132dddc0b8d62f6709256f2c0bff29b022d@stranger"]
          ""
          ""
          null
          ""
          ""*/
        val args = ArrayList<String?>()
        args.add(wechatId)
        val args2 = ArrayList<Int>()
        args2.add(15)
        val args3 = ArrayList<String>()
        args3.add(antispamTicket)

        val request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER),
                1, args, args2, args3, "", "", null, "", "")
        /*val request = XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(Int::class.javaPrimitiveType, List::class.java, List::class.java, List::class.java, String::class.java,
                        String::class.java, Map::class.java, String::class.java, String::class.java)
                .newInstance(1, args, args2, args3, "", "", null, "", "")*/

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

        val request = XposedHelpers.newInstance(XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER),
                2, args, args2, null, sayHi, "", mapArgs, null, "")
        /*val request = XposedHelpers.findClass(Wechat.HookMethodFunctions.NetScene.FriendRequestNetSceneClass, Wechat.WECHAT_CLASSLOADER)
                .getConstructor(Int::class.javaPrimitiveType, List::class.java, List::class.java, List::class.java, String::class.java,
                        String::class.java, Map::class.java, String::class.java, String::class.java)
                .newInstance(2, args, args2, null, sayHi, "", mapArgs, null, "")*/

        HookUtils.enqueueNetScene(request, 0)
    }

    override fun key(): String? {
        return "wechat:addFriend"
    }
}