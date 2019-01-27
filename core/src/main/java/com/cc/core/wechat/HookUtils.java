package com.cc.core.wechat;

import android.database.Cursor;
import android.text.TextUtils;

import com.cc.core.log.KLog;
import com.cc.core.utils.FileUtil;

import java.io.File;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.ConfigStorageGetFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.Account;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetContactManagerFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetConversationManagerFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetConfigManagerFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetGroupManagerFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetMsgInfoManagerFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.RegionCodeDecoderClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_CityCode;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_CountryCode;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_ProvinceCode;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_WechatId;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.KernelClass;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DBExecSqlFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DBRawQueryFunc;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DbHelperField;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.GetDBHelerFunc;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class HookUtils {
    private HookUtils(){}

    public static Object getContactManager() {
        Object contactManager = callStaticMethod(findClass(Account, Wechat.WECHAT_CLASSLOADER), GetContactManagerFunc);
        return contactManager;
    }

    public static Object getGroupManager() {
        Object groupManager = callStaticMethod(findClass(Account, Wechat.WECHAT_CLASSLOADER), GetGroupManagerFunc);
        return groupManager;
    }

    public static Object getMsgInfoStorage() {
        Object msgStorage = callStaticMethod(findClass(Account, Wechat.WECHAT_CLASSLOADER), GetMsgInfoManagerFunc);
        return msgStorage;
    }

    public static Object getConversationStorage() {
        Object ww = callStaticMethod(findClass(Account, Wechat.WECHAT_CLASSLOADER), GetConversationManagerFunc);
        return ww;
    }

    public static Object getLoginUserInfo(int id) {
        return getLoginUserInfo(id, null);
    }

    public static Object getLoginUserInfo(int id, Object defaultValue) {
         Object coreStorageObject = callStaticMethod(findClass(Account, Wechat.WECHAT_CLASSLOADER), GetConfigManagerFunc);


        return callMethod(coreStorageObject, ConfigStorageGetFunc, id, defaultValue);
    }

    public static String getLoginUserWechatId() {
        return (String) getLoginUserInfo(UserInfoId_WechatId);
    }

    public static String getLoginUserRegionCode() {
        String countryCode = (String) getLoginUserInfo(UserInfoId_CountryCode);
        String provinceCode = (String) getLoginUserInfo(UserInfoId_ProvinceCode);
        String cityCode = (String) getLoginUserInfo(UserInfoId_CityCode);

        return encodeRegionCode(countryCode, provinceCode, cityCode);
    }

    private static String encodeRegionCode(String countryCode, String provinceCode, String cityCode) {
        return (String) callStaticMethod( findClass(RegionCodeDecoderClass, Wechat.WECHAT_CLASSLOADER), Wechat.HookMethodFunctions.Account.RegionCodeDecoderEncodeFunc, countryCode, provinceCode, cityCode);
    }


    public static Object getDbHelper() {
        Object obj = callStaticMethod(findClass(KernelClass, Wechat.WECHAT_CLASSLOADER), GetDBHelerFunc);
        return getObjectField(obj, DbHelperField);
    }

    public static Cursor executeRawQuery(String rawQuery, String... params) {
        return (Cursor) callMethod(getDbHelper(), DBRawQueryFunc, rawQuery, params);
    }

    public static boolean execWritableSql(String sql) {
        return (boolean) callMethod(getDbHelper(), DBExecSqlFunc, "", sql);
    }

    public static String getSelfBigAvatarUrl() {
        String avatar = "";
        try (Cursor cursor = HookUtils.executeRawQuery("SELECT value FROM userinfo2 WHERE sid = 'USERINFO_LAST_LOGIN_AVATAR_PATH_STRING'")) {
            if (cursor.moveToNext()) {
                avatar = cursor.getString(0);
                KLog.e(">>>>> avatar:" + avatar);
                if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("user_hd")) {
                    File file = FileUtil.copyFile(avatar, FileUtil.getImageCacheDirectory().getAbsolutePath());
                    if (file != null) {
                        return file.getAbsolutePath();
                    }
                }
            }
        } catch (Exception e) {
        }

        //copy avatar to sdcard
        /*if (!TextUtils.isEmpty(thumbAvatarUrl)) {
            String filename = MD5.getMD5(thumbAvatarUrl);
            String extension = MimeTypeMap.getFileExtensionFromUrl(thumbAvatarUrl);
            if (TextUtils.isEmpty(extension)) {
                filename = filename + ".png";
            } else {
                filename = filename + "." + extension;
            }

            String path = PathUtil.getCachedImagePath(filename);

            FileUtil.fileCpy(thumbAvatarUrl, path, true);

            return path;
        }*/

        return avatar;
    }
/*
    public static Map<String, String> xmlToMap(String xml, String rootNodeName) {
        Object rawMap = callStaticMethod(
                findClass(VersionParam.commonWechatSdkXmlParserClass, Wechat.WECHAT_CLASSLOADER),
                VersionParam.commonWechatSdkXmlParserToMapFunc,
                xml,
                rootNodeName
        );

        return rawMap == null ? new HashMap<String, String>() : (Map<String, String>) rawMap;
    }

   public static Map<String, String> semixmlToMap(String semixml) {
        Object rawMap = callStaticMethod(
                findClass(VersionParam.commonWechatSdkSemiXmlParserClass, classLoader()),
                VersionParam.commonWechatSdkSemiXmlParserToMapFunc,
                semixml
        );

        return rawMap == null ? new HashMap<String, String>() : (Map<String, String>) rawMap;
    }

    public static XposedContactInfo getXposedContactInfo(String username) {
        try {
            Object ae = WechatContactUtil.getContactInfo(username);
            if (ae != null) {
                XposedContactInfo xposedContactInfo = copyProperties(XposedContactInfo.class, ae);
                return xposedContactInfo == null || StringUtil.isEmpty(xposedContactInfo.field_username) ? null : xposedContactInfo;
            }
        } catch (Throwable th) {
            LogUtil.w(TAG, "Error when getting xposed contact info: " + username);
        }

        return null;
    }

    public static XposedContactInfo getXposedContactInfoBySQL(String username) {
        try (Cursor contactCursor = WechatCoreUtil.executeRawQuery(String.format(StringUtil.isGroupWechatId(username) ? XposedContactInfo.SELECT_USER_GROUP_CONTACT_BY_USER_NAME_SQL : XposedContactInfo.SELECT_USER_CONTACT_BY_USER_NAME_SQL, username))) {
            if (contactCursor.moveToFirst()) {
                XposedContactInfo xposedContactInfo = new XposedContactInfo();
                xposedContactInfo.field_username = contactCursor.getString(0);
                xposedContactInfo.field_nickname = contactCursor.getString(1);

                return xposedContactInfo;
            }
        }

        return null;
    }

    public static String getImageMessagePath(String imgPath) {
        try {
            Class<?> mb = findClass(VersionParam.commonGetGroupMessageImageClass, classLoader());
            String imgPrefix = (String) callStaticMethod(mb, VersionParam.commonGetGroupMessageImagePathFunc);

            imgPath = imgPath.substring(20);
            String folder1 = imgPath.substring(3, 5) + "/";
            String folder2 = imgPath.substring(5, 7) + "/";
            String fullPath = imgPrefix + folder1 + folder2 + imgPath;
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }*/
}
