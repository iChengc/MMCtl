package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType

class VideoMessage : BaseMessage(WeChatMessageType.VIDEO) {
    private var videoUrl: String? = null

    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String) {
        this.videoUrl = videoUrl
    }
}