package com.cc.core.wechat.model

import com.cc.core.wechat.WechatMessageType

class VideoMessage : BaseMessage(WechatMessageType.VIDEO) {
    private var videoUrl: String? = null

    fun getVideoUrl(): String? {
        return videoUrl
    }

    fun setVideoUrl(videoUrl: String) {
        this.videoUrl = videoUrl
    }
}