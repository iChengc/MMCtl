package com.cc.core.wechat

import android.database.Cursor
import android.text.TextUtils
import com.cc.core.wechat.model.user.Friend
import com.cc.core.utils.FileUtil
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.Wechat.HookMethodFunctions.*

import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.ConfigStorageGetFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.AccountStorage
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetContactManagerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetConversationManagerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetConfigManagerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetGroupManagerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.GetMsgInfoManagerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.RegionCodeDecoderClass
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_CityCode
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_CountryCode
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_ProvinceCode
import com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_WechatId
import com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DBExecSqlFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DBRawQueryFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.DbHelperField
import com.cc.core.wechat.Wechat.HookMethodFunctions.Sqlite.GetDBHelerFunc
import com.cc.core.wechat.Wechat.HookMethodFunctions.NetScene.*
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.callStaticMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.getObjectField
import java.util.HashMap

class HookUtils {
    companion object {
        fun getContactManager(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetContactManagerFunc)
        }

        fun getGroupManager(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetGroupManagerFunc)
        }

        fun getMsgInfoStorage(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetMsgInfoManagerFunc)
        }

        fun getConversationStorage(): Any {
            return callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetConversationManagerFunc)
        }

        fun getNetscenQueue(): Any {
            return callStaticMethod(findClass(GetNetSceneQueueClass, Wechat.WECHAT_CLASSLOADER), GetNetSceneQueueFunc)
        }

        fun getLoginUserInfo(id: Int): Any {
            return getLoginUserInfo(id, "")
        }

        fun getLoginUserInfo(id: Int, defaultValue: Any): Any {
            val coreStorageObject = callStaticMethod(findClass(AccountStorage, Wechat.WECHAT_CLASSLOADER), GetConfigManagerFunc)

            return callMethod(coreStorageObject, ConfigStorageGetFunc, id, defaultValue)
        }

        fun getLoginUserWechatId(): String {
            return getLoginUserInfo(UserInfoId_WechatId) as String
        }

        fun getLoginUserRegionCode(): String {
            val countryCode = getLoginUserInfo(UserInfoId_CountryCode) as String
            val provinceCode = getLoginUserInfo(UserInfoId_ProvinceCode) as String
            val cityCode = getLoginUserInfo(UserInfoId_CityCode)

            return encodeRegionCode(countryCode, provinceCode, cityCode.toString())
        }

        private fun encodeRegionCode(countryCode: String, provinceCode: String, cityCode: String): String {
            return callStaticMethod(findClass(RegionCodeDecoderClass, Wechat.WECHAT_CLASSLOADER), Wechat.HookMethodFunctions.Account.RegionCodeDecoderEncodeFunc, countryCode, provinceCode, cityCode) as String
        }


        fun getDbHelper(): Any {
            val obj = callStaticMethod(findClass(KernelClass, Wechat.WECHAT_CLASSLOADER), GetDBHelerFunc)
            return getObjectField(obj, DbHelperField)
        }

        fun executeRawQuery(rawQuery: String, vararg params: String): Cursor {
            return callMethod(getDbHelper(), DBRawQueryFunc, rawQuery, params) as Cursor
        }

        fun execWritableSql(sql: String): Boolean {
            return callMethod(getDbHelper(), DBExecSqlFunc, "", sql) as Boolean
        }

        fun getSelfBigAvatarUrl(): String? {
            var avatar = ""
            try {
                executeRawQuery("SELECT value FROM userinfo2 WHERE sid = 'USERINFO_LAST_LOGIN_AVATAR_PATH_STRING'").use { cursor ->
                    if (cursor.moveToNext()) {
                        avatar = cursor.getString(0)
                        if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("user_hd")) {
                            val file = FileUtil.copyFile(avatar, FileUtil.getImageCacheDirectory().absolutePath)
                            if (file != null) {
                                return file.absolutePath
                            }
                        }
                    }
                }
            } catch (e: Exception) {
            }

            return avatar
        }

        fun getContactByWechatId(wechatId: String): com.cc.core.wechat.model.user.Friend? {
            val j = callMethod(getContactManager(), Wechat.HookMethodFunctions.Account.GetContactInfoFunc, wechatId)
            return if (j == null) {
                null
            } else StrUtils.fromJson(StrUtils.toJson(j), com.cc.core.wechat.model.user.Friend::class.java)
        }

        fun enqueueNetScene(request: Any, type: Int) {
            callMethod(getNetscenQueue(), NetSceneEnqueueFunc, request, type)
        }


        fun xmlToMap(xml: String, rootNodeName: String): Map<String, String> {
            val rawMap = callStaticMethod(
                    findClass(commonWechatSdkXmlParserClass, Wechat.WECHAT_CLASSLOADER),
                    commonWechatSdkXmlParserToMapFunc,
                    xml,
                    rootNodeName
            )

            return if (rawMap == null) HashMap() else rawMap as Map<String, String>
        }
    }
}