package com.cc.core.command

import com.google.gson.annotations.SerializedName

class Command {
  @SerializedName("id")
  private var id: String = ""
  @SerializedName("key")
  private var key: String = ""
  @SerializedName("args")
  private var args: List<Any>? = null

  fun getId(): String {
    return id
  }

  fun setId(id: String) {
    this.id = id
  }

  fun getKey(): String {
    return key
  }

  fun setKey(key: String) {
    this.key = key
  }

  fun getArgs(): List<Any>? {
    return args
  }

  fun setArgs(args: List<Any>) {
    this.args = args
  }
}