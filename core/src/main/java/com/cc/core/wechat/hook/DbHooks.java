package com.cc.core.wechat.hook;

import com.cc.core.log.KLog;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class DbHooks extends BaseXposedHook {

    @Override
    public void hook(ClassLoader classLoader) {
        Class classSQLiteDatabase = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", classLoader);
        Class classSQLiteDatabaseConfiguration = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabaseConfiguration", classLoader);
        Class classSQLiteCipherSpec = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteCipherSpec", classLoader);
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteConnectionPool",
                classLoader,
                "open",
                classSQLiteDatabase,
                classSQLiteDatabaseConfiguration,
                byte[].class,
                classSQLiteCipherSpec,
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        byte[] passwordBuffer = (byte[]) param.args[2];
                        if (passwordBuffer == null) {
                            return;
                        }

                        String path = (String) XposedHelpers.getObjectField(param.args[1], "path");
                        if (!path.endsWith("EnMicroMsg.db")) {
                            return;
                        }

                        Wechat.DB_PATH = path;
                        Wechat.DB_PASSWORD = new String(passwordBuffer);

                        boolean hmacEnabled = XposedHelpers.getBooleanField(param.args[3], "hmacEnabled");
                        int kdfIteration = XposedHelpers.getIntField(param.args[3], "kdfIteration");
                        int pageSize = XposedHelpers.getIntField(param.args[3], "pageSize");

                        String sql = String.format(
                                "PRAGMA key = '%s';" +
                                        "PRAGMA cipher_use_hmac = %s;" +
                                        "PRAGMA cipher_page_size = %d;" +
                                        "PRAGMA kdf_iter = %d;",  Wechat.DB_PASSWORD, hmacEnabled ? "ON" : "OFF", pageSize, kdfIteration);
                        /*DbService.getInstance().insertDbPassword(password, new Callback() {
                            @Override
                            public void onResult(String result) {
                                KLog.d("password has saved");
                            }
                        });*/
                        KLog.e("XPosed", "----- path: " + path + ", decrypt sql: " + sql);
                    }
                });
    }
}
