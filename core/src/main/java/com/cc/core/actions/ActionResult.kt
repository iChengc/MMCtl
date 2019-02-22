package com.cc.core.actions

open class ActionResult {
    var success: Boolean = false
    var message: String? = null
    var data: Any? = null

    constructor()
    constructor(success: Boolean, message: String?) {
        this.success = success
        this.message = message
    }

    fun isSuccess() : Boolean {
        return success
    }

    companion object {
        fun successResult(): ActionResult {
            return successResult(null)
        }

        fun successResult(data: Any?): ActionResult {
            val result = ActionResult()
            result.success = true
            result.message = "ok"
            result.data = data
            return result
        }


        fun failedResult(thr: Throwable?): ActionResult {
            val result = ActionResult()
            result.success = false
            result.message = if (thr == null) "unknown error" else thr.javaClass.name + ":" + thr.message
            return result
        }

        fun failedResult(message: String): ActionResult {
            val result = ActionResult()
            result.success = false
            result.message = message
            return result
        }
    }
}