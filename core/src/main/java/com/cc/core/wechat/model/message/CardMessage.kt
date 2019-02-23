package com.cc.core.wechat.model.message

import com.cc.core.wechat.WeChatMessageType
import com.google.gson.annotations.SerializedName

class CardMessage : BaseMessage(WeChatMessageType.CARD) {
  @SerializedName("title")
  private var title: String? = null
  @SerializedName("description")
  private var description: String? = null
  @SerializedName("thumbUrl")
  private var thumbUrl: String? = null
  @SerializedName("url")
  private var url: String? = null
  @SerializedName("cardType")
  private var cardType: String? = null

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

  fun getCardType(): String? {
    return cardType
  }

  fun setCardType(type: String?) {
    this.cardType = type
  }
}