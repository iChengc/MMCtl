package com.cc.core.wechat.model.sns

import com.google.gson.annotations.SerializedName

open class SnsLike {
    @SerializedName("nickName")
    var nickName: String? = null
    @SerializedName("wechatId")
    var wechatId: String? = null
    @SerializedName("createTime")
    var createTime: Long = 0
    @SerializedName("id")
    var id = 0
}