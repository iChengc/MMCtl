package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

class VideoMessage : BaseMessage(WeChatMessageType.VIDEO) {
    @SerializedName("videoUrl")
    private var videoUrl: String? = null

    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String) {
        this.videoUrl = videoUrl
    }
}