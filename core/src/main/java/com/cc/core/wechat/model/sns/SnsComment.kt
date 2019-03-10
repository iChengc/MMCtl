package com.cc.core.wechat.model.sns

import com.google.gson.annotations.SerializedName

class SnsComment : SnsLike() {
    @SerializedName("reply2")
    internal var reply2: String? = null
    @SerializedName("content")
    internal var content: String? = null
    @SerializedName("replayId")
    internal var replayId = 0
}