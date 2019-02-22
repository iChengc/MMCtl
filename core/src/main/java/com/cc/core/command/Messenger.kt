package com.cc.core.command

import com.cc.core.WorkerHandler
import com.cc.core.actions.Actions
import com.cc.core.utils.Utils

class Messenger {
  companion object {
    fun <T> sendCommand(cmd: Command?, callback: Callback<T>) {
      if (cmd == null) {
        callback.onResult(false, "no command was found!", null)
        return
      }

      WorkerHandler.postOnWorkThread {
        val result: String
        if (Utils.isEmpty(cmd.getArgs())) {

          result = Actions.executeCommand(cmd.getKey())
        } else {
          result = Actions.executeCommand(cmd.getKey(), *cmd.getArgs()!!.toTypedArray())
        }

        callback.result(result)
      }

    }
  }
}