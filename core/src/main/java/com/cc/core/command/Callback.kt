package com.cc.core.command

import com.cc.core.log.KLog
import com.cc.core.utils.StrUtils
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

abstract class Callback<T> {

  // inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)
  fun result(result: String) {
    val type: Type = ResultType<CommandResult<T>>().type

    val cmdResult = StrUtils.fromJson<CommandResult<T>>(result, type)

    KLog.e("===>>>>> cmdResult:" + cmdResult.javaClass.simpleName, StrUtils.toJson(cmdResult))
    onResult(cmdResult.isSuccess(), cmdResult.getMessage(), cmdResult.getData())
  }

  abstract fun onResult(
    isSuccess: Boolean,
    message: String?,
    result: T?
  )

  class ResultType<T> : TypeToken<T>()
}