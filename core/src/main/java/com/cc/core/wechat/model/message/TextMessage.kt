package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

open class TextMessage : BaseMessage(WeChatMessageType.TEXT) {
    @SerializedName("atUsers")
    private var atUsers : Array<String>? = null
    @SerializedName("content")
    private var content: String? = null

    fun getAtUsers(): Array<String>? {
        return atUsers
    }

    fun setContent(atUsers: Array<String>?) {
        this.atUsers = atUsers
    }

    fun getContent(): String? {
        return content
    }

    fun setContent(content: String?) {
        this.content = content
    }
}