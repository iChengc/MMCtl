package com.cc.core.wechat.model

interface WeChatMessage {
    fun setFrom(from:String)
    fun getFrom():String
    fun setTarget(target:String)
    fun getTarget():String
}