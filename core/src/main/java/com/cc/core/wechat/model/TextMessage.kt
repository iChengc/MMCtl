package com.cc.core.wechat.model

import com.cc.core.wechat.WechatMessageType

class TextMessage : BaseMessage(WechatMessageType.TEXT) {
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