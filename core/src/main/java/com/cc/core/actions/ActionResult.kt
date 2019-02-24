package com.cc.core.actions

import com.google.gson.annotations.SerializedName

class ActionResult {
	@SerializedName("success")
    var success: Boolean = false
	@SerializedName("message")
    var message: String? = null
	@SerializedName("id")
    var actionId: String = ""
	@SerializedName("data")
    var data: Any? = null

    private constructor()
    constructor(actionId : String, success: Boolean, message: String?) {
        this.success = success
        this.message = message
        this.actionId = actionId
    }

    fun isSuccess() : Boolean {
        return success
    }

    companion object {
        fun successResult(actionId : String): ActionResult {
            return successResult(actionId, null)
        }

        fun successResult(actionId : String, data: Any?): ActionResult {
            val result = ActionResult()
            result.actionId = actionId
            result.success = true
            result.message = "ok"
            result.data = data
            return result
        }


        fun failedResult(actionId : String, thr: Throwable?): ActionResult {
            val result = ActionResult()
            result.actionId = actionId
            result.success = false
            result.message = if (thr == null) "unknown error" else thr.javaClass.name + ":" + thr.message
            return result
        }

        fun failedResult(actionId : String, message: String): ActionResult {
            val result = ActionResult()
            result.success = false
            result.message = message
            result.actionId = actionId
            return result
        }
    }
}