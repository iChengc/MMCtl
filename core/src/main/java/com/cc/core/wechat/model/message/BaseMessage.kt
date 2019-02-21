package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType

abstract class BaseMessage : WeChatMessage {
    private var target: String? = null
    private var from: String? = null
    private var type: Int = WeChatMessageType.UNKNOWN
    private var createTime: Long = System.currentTimeMillis()
    private var msgServId : String? = null

    constructor(type: Int) {
        this.type = type
    }

    override fun getType(): Int {
        return type
    }

    override fun setType(type: Int) {
        this.type = type
    }

    // private var from
    override fun setFrom(from: String?) {
        this.from = from
    }

    override fun getFrom(): String? {
        return from
    }

    override fun setTarget(target: String?) {
        this.target = target
    }

    override fun getTarget(): String? {
        return target
    }

    override fun getCreateTime(): Long {
        return createTime
    }

    override fun setCreateTime(createTime: Long) {
        this.createTime = createTime
    }
    override fun setMsgServId(servId: String?) {
        this.msgServId = servId
    }

    override fun getMsgServId(): String? {
        return msgServId
    }
}