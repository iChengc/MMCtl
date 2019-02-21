package com.cc.core.wechat.model.message

class UnsupportMessage : TextMessage() {
  private var messageDetails: String? = null

  fun setMessageDetails(messageDetails: String?) {
    this.messageDetails = messageDetails
  }

  fun getMessageDetails(): String? {
    return messageDetails
  }
}