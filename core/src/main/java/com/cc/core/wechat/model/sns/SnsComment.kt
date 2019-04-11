package com.cc.core.wechat.model.sns

import com.google.gson.annotations.SerializedName

class SnsComment : SnsLike() {
    @SerializedName("reply2")
    var reply2: String? = null
    @SerializedName("content")
    var content: String? = null
    @SerializedName("replayId")
    var replayId = 0
}