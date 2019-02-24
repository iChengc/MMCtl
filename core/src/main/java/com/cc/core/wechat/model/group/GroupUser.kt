package com.cc.core.wechat.model.group

import com.google.gson.annotations.SerializedName
import java.util.*

class GroupUser {
    @SerializedName("groupWechatId")
    private var groupWechatId: String? = null
    @SerializedName("wechatId")
    private var wechatId: String? = null
    @SerializedName("groupNickname")
    private var groupNickname: String? = null
    @SerializedName("groupDisplayName")
    private var groupDisplayName: String? = null
    @SerializedName("invitedBy")
    private var invitedBy: String? = null
    @SerializedName("flag")
    private var flag: Int = 0
    @SerializedName("enteredTime")
    private var enteredTime: Long = 0L
    @SerializedName("sex")
    private var sex: Int = 0
    @SerializedName("avatar")
    private var avatar : String? = null
}