package com.cc.core.wechat.model.group

import com.google.gson.annotations.SerializedName

class GroupInfo {
    @SerializedName("ServerId")
    private var ServerId: String? = null
    private var groupName : String? = null
    private var groupWechatId :String? = null
    private var groupNotice:String?= null
    private var roomownerId: String? = null
    private var memberList:List<GroupUser>?=null
}