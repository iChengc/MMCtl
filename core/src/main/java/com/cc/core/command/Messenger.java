package com.cc.core.command;

import com.cc.core.WorkerHandler;
import com.cc.core.actions.Actions;
import com.cc.core.utils.Utils;

public class Messenger {
    public static void sendCommand(final Command cmd, final Callback callback) {
        if (cmd == null) {
            callback.onResult(null);
            return;
        }

        WorkerHandler.postOnWorkThread(new Runnable() {
            @Override
            public void run() {
                String result;
                if (Utils.isEmpty(cmd.getArgs())) {

                    result = Actions.Companion.executeCommand(cmd.getKey());
                } else {
                    result = Actions.Companion.executeCommand(cmd.getKey(), cmd.getArgs().toArray());
                }
                callback.onResult(result);
            }
        });

    }
}
