package com.cc.core.wechat.invoke

import com.cc.core.actions.Action
import com.cc.core.actions.ActionResult
import com.cc.core.wechat.model.user.Friend
import com.cc.core.log.KLog
import com.cc.core.wechat.HookUtils
import com.cc.core.wechat.Wechat
import java.util.ArrayList


class GetContactsAction : Action {
    companion object {
        //private const val QUERY_CONTACT_SQL = "select username ,nickname ,alias,conRemark,verifyFlag,showHead,weiboFlag,rowid ,deleteFlag,lvbuff,descWordingId,openImAppid from rcontact  where (type & 1!=0) and type & 32=0  and type & 8 =0  and verifyFlag & 8 =0  and ( username not like '%@%' or (( (type & 1!=0) and type & 8=0 and username like '%@talkroom'))) and username != 'tmessage' and username != 'officialaccounts' and username != 'helper_entry' and username != 'blogapp' or username = 'weixin' order by showHead asc,  case when length(conRemarkPYFull) > 0 then upper(conRemarkPYFull)  else upper(quanPin) end asc,  case when length(conRemark) > 0 then upper(conRemark)  else upper(quanPin) end asc,  upper(quanPin) asc,  upper(nickname) asc,  upper(username) asc"
        private const val QUERY_CONTACT_SQL = "select * ,rowid from rcontact  where (type & 1!=0) and type & 32=0  and type & 8 =0  and verifyFlag & 8 =0  and ( username not like '%@%' or (( (type & 1!=0) and type & 8=0 and username like '%@talkroom'))) and username != 'appbrandcustomerservicemsg' and username != 'notifymessage' and username != 'weibo' and username != 'pc_share' and username != 'officialaccounts' and username != 'voicevoipapp' and username != 'cardpackage' and username != 'qqfriend' and username != 'helper_entry' and username != 'medianote' and username != 'shakeapp' and username != 'topstoryapp' and username != 'qmessage' and username != 'voipapp' and username != 'qqmail' and username != 'qqsync' and username != 'blogapp' and username != 'lbsapp' and username != 'feedsapp' and username != 'readerapp' and username != 'newsapp' and username != 'floatbottle' and username != 'fmessage' and username != 'tmessage' and username != 'weixin' and username != 'facebookapp' and username != 'meishiapp' and username != 'masssendapp' and username != 'voiceinputapp' and username != 'filehelper' and username != 'linkedinplugin' order by showHead asc,  case when length(conRemarkPYFull) > 0 then upper(conRemarkPYFull)  else upper(quanPin) end asc,  case when length(conRemark) > 0 then upper(conRemark)  else upper(quanPin) end asc,  upper(quanPin) asc,  upper(nickname) asc,  upper(username) asc"
        // private val QUERY_CONTACT_SQL = "SELECT rowid, username, alias, conRemark, nickname, verifyFlag, type " + "FROM rcontact WHERE type & 1 != 0 AND type & 8 = 0 AND type & 32 = 0 AND (verifyFlag & 8 = 0 AND username NOT LIKE '%@%') ORDER BY rowid"
        private const val QUERY_AVATAR_SQL = "select reserved1 from img_flag where username = ? ;"
    }

    override fun execute(actionId : String, vararg args: Any?): ActionResult? {
        val wechatIds = ArrayList<String>()
        val friends = ArrayList<com.cc.core.wechat.model.user.Friend>()
        HookUtils.executeRawQuery(QUERY_CONTACT_SQL).use { contactCursor ->
            KLog.e("----->>>>> finidh get contacts")
            while (contactCursor.moveToNext()) {
                val wechatId = contactCursor.getString(0)
                KLog.e("----->>>>> $wechatId")

                // Robot self should not be considered as a friend
                if (Wechat.LoginWechatId == wechatId) {
                    continue
                }

                wechatIds.add(wechatId)
            }

            KLog.e("----->>>>> ${wechatIds}")
            for (w in wechatIds) {
                val f = HookUtils.getContactByWechatId(w)

                if (f != null) {
                    f.avatar = getAvatarBy(w)
                    friends.add(f)
                }
            }
        }

        KLog.e("----->>>>> ${wechatIds}")
        return ActionResult.successResult(actionId, friends)
    }

    private fun getAvatarBy(wechatId: String): String {
        HookUtils.executeRawQuery(QUERY_AVATAR_SQL, wechatId).use { contactCursor ->
            if (contactCursor.moveToFirst()) {
                return contactCursor.getString(0)

            }
        }
        return ""
    }

    override fun key(): String? {
        return "wechat:getContacts"
    }
}