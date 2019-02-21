package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType

class ImageMessage : BaseMessage(WeChatMessageType.IMAGE) {
    private var imageUrl: String? = null


    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getImageUrl(): String? {
        return imageUrl
    }
}