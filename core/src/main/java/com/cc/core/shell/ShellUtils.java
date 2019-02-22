package com.cc.core.shell;

import com.cc.core.WorkerHandler;
import com.cc.core.shell.core.CommandResult;
import com.cc.core.shell.core.Shell;
import com.cc.core.shell.core.ShellNotFoundException;

/**
 * Created by Chengc on 2018/12/11.
 */

public class ShellUtils {
    private ShellUtils(){

    }
    private static Shell.Console console;
    private static Shell.Console rootConsole;

    static {
        try {
            console = new Shell.Console.Builder().useSH().build();
            rootConsole = new Shell.Console.Builder().useSU().build();
        } catch (ShellNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static CommandResult runShell(String... commands) {
        return console.run(commands);
    }

    public static CommandResult runShell(boolean isSu, String... commands) {
        if (!isSu) {
            return runShell(commands);
        }

        return rootConsole.run(commands);
    }

    public static void runShellAsync(final ResultCallback callback, final String...cmds) {
        WorkerHandler.getInstance().postOnWorkThread(new Runnable() {
            @Override
            public void run() {
                CommandResult result = runShell(cmds);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        });
    }

    public static void runSuShellAsync(final ResultCallback callback, final String...cmds) {
        WorkerHandler.getInstance().postOnWorkThread(new Runnable() {
            @Override
            public void run() {
                CommandResult result = runShell(true, cmds);
                if (callback != null) {
                    callback.onResult(result);
                }
            }
        });
    }

    public interface ResultCallback {
        void onResult(CommandResult result);
    }
}
