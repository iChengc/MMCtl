package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import java.util.ArrayList

import de.robv.android.xposed.XposedHelpers.callMethod

class GetContactsAction : Action {
    private val QUERY_CONTACT_SQL = "SELECT rowid, username, alias, conRemark, nickname, verifyFlag, type " + "FROM rcontact WHERE type & 1 != 0 AND type & 8 = 0 AND type & 32 = 0 AND (verifyFlag & 8 = 0 AND username NOT LIKE '%@%') ORDER BY rowid"
    override fun execute(vararg args: Any?): ActionResult? {
        val wechatIds = ArrayList<String>()
        HookUtils.executeRawQuery(QUERY_CONTACT_SQL).use { contactCursor ->
            while (contactCursor.moveToNext()) {
                val wechatId = contactCursor.getString(1)

                // Robot self should not be considered as a friend
                if (Wechat.LoginWechatId == wechatId) {
                    continue
                }

                callMethod(HookUtils.getContactManager(), Wechat.HookMethodFunctions.Account.GetContactInfoFunc, wechatId)
                wechatIds.add(wechatId)
            }
        }
        return null
    }

    override fun key(): String? {
        return "wechat:getContacts"
    }
}