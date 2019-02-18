package com.cc.core.wechat.model

import com.cc.core.wechat.WechatMessageType

class ImageMessage : BaseMessage(WechatMessageType.IMAGE) {
    private var imageUrl: String? = null


    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getImageUrl(): String? {
        return imageUrl
    }
}