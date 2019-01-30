package com.cc.core.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import com.cc.core.ApplicationContext;
import com.cc.core.shell.ShellCommands;
import com.cc.core.shell.ShellUtils;

public class DeviceUtils {
    private DeviceUtils() {
    }

    public static void unlockScreen() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) ApplicationContext.application()
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "AutoClockIn:LockScreen");
            wl.acquire(5000); // 点亮屏幕
            wl.release(); // 释放
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) ApplicationContext.application()
                .getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("AutoClockIn:LockScreen");
        // 屏幕锁定
        keyguardLock.disableKeyguard(); // 解锁
    }

    public static void lockScreen() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) ApplicationContext.application()
                .getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (screenOn) {
            ShellUtils.runShell(true, ShellCommands.genCmd(ShellCommands.INPUT_KEY_CMD, "26"));
        }
        /*Class c = Class.forName("android.os.PowerManager");
        PowerManager mPowerManager = (PowerManager) MyApplication.application.getSystemService(Context.POWER_SERVICE);
        for (Method m : c.getDeclaredMethods()) {
            if (m.getName().equals("goToSleep")) {
                m.setAccessible(true);
                if (m.getParameterTypes().length == 1) {
                    m.invoke(mPowerManager, SystemClock.uptimeMillis() - 2);
                }
            }
        }*/

        /*//获取电源管理器对象
        PowerManager pm=(PowerManager) MyApplication.application.getSystemService(Context.POWER_SERVICE);
        if (pm == null) {
            return;
        }
        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "AutoClockIn:LockScreen");

        //得到键盘锁管理器对象
        KeyguardManager km= (KeyguardManager)MyApplication.application.getSystemService(Context.KEYGUARD_SERVICE);
        if (km == null) {
            return;
        }

        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("AutoClockIn:LockScreen");

        //锁屏
        kl.reenableKeyguard();

        //释放wakeLock，关灯
        try{
            wl.acquire(1000);
            wl.release();//always release before acquiring for safety just in case
        }
        catch(Exception e){
            //probably already released
            e.printStackTrace();
        }*/
    }
}
