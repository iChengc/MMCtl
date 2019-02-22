package com.cc.core.command

class CommandResult<T> {
  private var success: Boolean = false
  private var message: String? = null
  private var data: T? = null

  fun isSuccess(): Boolean {
    return success
  }

  fun setSuccess(success: Boolean) {
    this.success = success
  }

  fun getMessage(): String? {
    return message
  }

  fun setMessage(message: String?) {
    this.message = message
  }

  fun getData(): T? {
    return data
  }

  fun setData(data: T?) {
    this.data = data
  }
}