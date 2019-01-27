package com.cc.core.wechat.invoke;

import android.database.Cursor;

import com.cc.core.actions.Action;
import com.cc.core.actions.ActionResult;
import com.cc.core.wechat.HookUtils;
import com.cc.core.wechat.Wechat;

import java.util.ArrayList;
import java.util.List;

public class GetContactsAction implements Action {
    private static final String QUERY_CONTACT_SQL = "SELECT rowid, username, alias, conRemark, nickname, verifyFlag, type " +
            "FROM rcontact WHERE type & 1 != 0 AND type & 8 = 0 AND type & 32 = 0 AND (verifyFlag & 8 = 0 AND username NOT LIKE '%@%') ORDER BY rowid";
    @Override
    public ActionResult execute(Object... args) {
        List<String> wechatIds = new ArrayList<>();
        try (Cursor contactCursor = HookUtils.executeRawQuery(QUERY_CONTACT_SQL)) {
            while (contactCursor.moveToNext()) {
                String wechatId = contactCursor.getString(1);

                // Robot self should not be considered as a friend
                if (Wechat.LoginWechatId.equals(wechatId)) {
                    continue;
                }

                wechatIds.add(wechatId);
            }
        }
        return null;
    }

    @Override
    public String key() {
        return "wechat:getContacts";
    }
}
