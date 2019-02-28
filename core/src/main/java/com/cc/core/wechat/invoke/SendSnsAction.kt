package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.SnsUtils
import com.cc.core.wechat.model.sns.SnsInfo
import de.robv.android.xposed.XposedHelpers

class SendSnsAction : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        val uploadPackHelper = SnsUtils.generateSnsUploadPackHelper(SnsInfo())
        XposedHelpers.callMethod(uploadPackHelper, "commit")
        return ActionResult.successResult(actionId)
    }

    override fun key(): String? {
        return "wechat:sendSns"
    }
}