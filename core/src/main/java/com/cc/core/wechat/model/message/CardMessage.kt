package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType

class CardMessage : BaseMessage(WeChatMessageType.CARD) {
  private var title: String? = null
  private var description: String? = null
  private var thumbUrl: String? = null
  private var url: String? = null
  private var type: String? = null

  fun getTitle(): String? {
    return title
  }

  fun setTitle(title: String?) {
    this.title = title
  }

  fun getDescription(): String? {
    return description
  }

  fun setDescription(description: String?) {
    this.description = description
  }

  fun getThumbUrl(): String? {
    return thumbUrl
  }

  fun setThumbUrl(thumbUrl: String?) {
    this.thumbUrl = thumbUrl
  }

  fun getUrl(): String? {
    return url
  }

  fun setUrl(url: String?) {
    this.url = url
  }

  fun geType(): String? {
    return type
  }

  fun setType(type: String?) {
    this.type = type
  }
}