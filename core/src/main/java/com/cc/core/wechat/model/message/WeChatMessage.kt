package com.cc.core.wechat.model.message

interface WeChatMessage {
    fun setFrom(from: String?)
    fun getFrom(): String?
    fun setTarget(target: String?)
    fun getTarget(): String?
    fun getType(): Int
    fun setType(type: Int)
    fun getCreateTime(): Long
    fun setCreateTime(createTime: Long)
    fun setMsgServId(servId: String?)
    fun getMsgServId(): String?
}