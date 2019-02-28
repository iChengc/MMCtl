package com.cc.core.command.impl

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.actions.Actions
import com.cc.core.utils.StrUtils
import com.cc.core.utils.Utils
import com.cc.core.wechat.invoke.UploadSnsAction
import com.cc.core.wechat.model.sns.SnsInfo
import java.util.ArrayList

class UploadSns : Action {
    override fun execute(actionId: String, vararg args: Any?): ActionResult? {
        if (args.isEmpty()) {
            return ActionResult.failedResult(actionId, "No sns was inputted!")
        }
        var sns : SnsInfo = if (args[0] is SnsInfo) {
            args[0] as SnsInfo
        } else {
            StrUtils.fromJson(args[0].toString(), SnsInfo::class.java)
        }

        if (sns.getType() != SnsInfo.TEXT_TYPE) {
            downloadMedia(sns)
        }
        return Actions.execute(UploadSnsAction::class.java, actionId, StrUtils.toJson(sns))
    }

    private fun downloadMedia(sns:SnsInfo) {
        if (sns.getMedias() == null) {
            return
        }

        var localPath = ArrayList<String>()
        for (url in sns.getMedias()!!) {
            val path = Utils.downloadFile(url, sns.getType() == SnsInfo.VIDEO_TYPE)
            localPath.add(path)
        }

        sns.setMedias(localPath)
    }

    override fun key(): String? {
        return "uploadSns"
    }
}