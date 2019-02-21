package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType

open class TextMessage : BaseMessage(WeChatMessageType.TEXT) {
    private var atUsers : Array<String>? = null
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