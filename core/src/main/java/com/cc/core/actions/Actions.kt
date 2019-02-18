package com.cc.core.actions

import android.text.TextUtils
import com.cc.core.log.KLog
import com.cc.core.rpc.Rpc
import com.cc.core.rpc.RpcArgs
import com.cc.core.utils.StrUtils
import com.google.gson.annotations.SerializedName

class Actions {
    companion object {

        private val actions : HashMap<String, Action> = HashMap()

        init {
            loadActions()
        }

        private fun loadActions(){
            try {
                for (a in ActionNames.ACTION_NAMES) {
                    addAction(a)
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        @Throws(Exception::class)
        private fun addAction(className: String) {
            val action = Class.forName(className)
            if (isAction(action)) {
                put(action.newInstance() as Action)
            }
        }

        private fun isAction(actionCandidate: Class<*>): Boolean {
            val isImplementation = !actionCandidate.isInterface
            return isImplementation && Action::class.java.isAssignableFrom(actionCandidate)
        }

        private fun put(action: Action) {
            if (actions.containsKey(action.key())) {
                val duplicate = actions[action.key()]
                throw RuntimeException("Found duplicate action key:'" + action.key() + "'. [" + (duplicate as Action).javaClass.name + "," + action.javaClass.name + "]")
            }

            if (TextUtils.isEmpty(action.key())) {
                KLog.i("Skipping " + action.javaClass.name + ". Key is null.")
            } else {
                actions[action.key().toString()] = action
            }
        }

        fun lookup(key: String?): Action {
            var action: Action? = getActions()[key]
            if (action == null) {
                action = NullAction()
                action.setMissingKey(key)
            }
            return action
        }

        fun lookup(clazz: Class<out Action>): Action {
            actions.entries.forEach {
                if (it.value.javaClass === clazz) {
                    return it.value
                }
            }

            return NullAction(clazz.name)
        }

        fun getActions(): MutableMap<String, Action> {
            return actions
        }

        fun execute(clazz: Class<out Action>, vararg args: Any): ActionResult? {
            val action = lookup(clazz)
            return if (isWechatAction(action)) {
                try {
                    Rpc.call(RpcArgs.newMessage(RawAction.fromAction(action, *args)))
                } catch (e: Exception) {
                    e.printStackTrace()
                    ActionResult.failedResult(e)
                }

            } else {
                action.execute(*args)
            }
        }

        fun executeCommand(key: String, vararg args: Any?): String {
            val action = lookup(key)
            return if (isWechatAction(action)) {
                try {
                    StrUtils.toJson(Rpc.call(RpcArgs.newMessage(RawAction.fromAction(action, *args))))
                } catch (e: Exception) {
                    e.printStackTrace()
                    StrUtils.toJson(ActionResult.failedResult(e))
                }

            } else {
                StrUtils.toJson(action.execute(*args))
            }
        }

        fun receivedAction(rawAction: String): ActionResult? {
            val raw = StrUtils.fromJson(rawAction, RawAction::class.java)
            val action = lookup(raw.actionName)
            return action.execute(*raw.args!!.toTypedArray())
        }

        private fun isWechatAction(action: Action): Boolean {
            return StrUtils.stringNotNull(action.key()).startsWith("wechat:")
        }
    }

    class RawAction {
        @SerializedName("actionName")
        var actionName: String? = null

        @SerializedName("args")
        var args: MutableList<Any?>? = null

        override fun toString(): String {
            return """
                RawAction{actionName='$actionName', args=${args.toString()}
            """
        }

        companion object {

            fun fromAction(action: Action, vararg args: Any?): RawAction {
                val a = RawAction()
                a.actionName = action.key()
                a.args = mutableListOf()

                for (arg in args) {
                    (a.args as MutableList).add(arg)
                }
                return a
            }
        }
    }
}