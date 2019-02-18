package com.cc.core.wechat.model

interface WeChatMessage {
    fun setFrom(from: String?)
    fun getFrom(): String?
    fun setTarget(target: String?)
    fun getTarget(): String?
    fun getType(): Int
    fun setType(type: Int)
    fun getCreateTime(): Long
    fun setCreateTime(createTime: Long)
}