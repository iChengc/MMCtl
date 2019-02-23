package com.cc.core.wechat.model.message

import com.google.gson.annotations.SerializedName

class UnsupportMessage : TextMessage() {
  @SerializedName("messageDetails")
  private var messageDetails: String? = null

  fun setMessageDetails(messageDetails: String?) {
    this.messageDetails = messageDetails
  }

  fun getMessageDetails(): String? {
    return messageDetails
  }
}