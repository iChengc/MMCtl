package com.cc.core.wechat.model.sns

import com.google.gson.annotations.SerializedName

class SnsCommentRequest {
    @SerializedName("snsId")
    var snsId : String? = null
    @SerializedName("content")
    var content : String? = null
    @SerializedName("comment")
    var comment : SnsComment? = null
}