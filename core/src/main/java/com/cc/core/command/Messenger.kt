package com.cc.core.command

import com.cc.core.WorkerHandler
import com.cc.core.actions.Actions
import com.cc.core.utils.Utils

class Messenger {
    companion object {
        fun sendCommand(cmd: Command?, callback: Callback?) {
            if (cmd == null) {
                callback!!.onResult(null)
                return
            }

            WorkerHandler.postOnWorkThread {
                val result: String = if (Utils.isEmpty(cmd.getArgs())) {

                    Actions.executeCommand(cmd.getId(), cmd.getKey())
                } else {
                    Actions.executeCommand(cmd.getId(), cmd.getKey(), *cmd.getArgs()!!.toTypedArray())
                }
                callback!!.onResult(result)

            }

        }
    }
}