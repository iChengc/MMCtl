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
                String result = Actions.executeCommand(cmd.getKey(),
                        Utils.isEmpty(cmd.getArgs()) ? null: cmd.getArgs().toArray());
                callback.onResult(result);
            }
        });

    }
}
