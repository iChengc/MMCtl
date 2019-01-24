package com.cc.wechatmanager.shell;

/**
 * Created by Chengc on 2018/12/11.
 */

public class ShellCommands {
    //adb shell am start -n "com.cc.autoclockin/com.cc.autoclockin.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
    public static final String START_APP_CMD = "am start -n %s/%s  -a android.intent.action.MAIN -c android.intent.category.LAUNCHER";

    public static final String STOP_APP_CMD = "am force-stop %s";

    public static final String SCREENSHOT_CMD = "screencap -p %s";

    public static final String INPUT_KEY_CMD = "input keyevent %s";

    public static String genCmd(String rawCmd, Object... args) {
        return String.format(rawCmd, args);
    }
}
