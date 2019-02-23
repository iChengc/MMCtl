package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

class ImageMessage : BaseMessage(WeChatMessageType.IMAGE) {
    @SerializedName("imageUrl")
    private var imageUrl: String? = null

    fun setImageUrl(imageUrl: String) {
        this.imageUrl = imageUrl
    }

    fun getImageUrl(): String? {
        return imageUrl
    }
}