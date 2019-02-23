package com.cc.core.wechat.hook.tool;

import com.cc.core.command.Callback;
import com.cc.core.data.db.DbService;
import com.cc.core.data.db.model.User;
import com.cc.core.log.KLog;
import com.cc.core.wechat.HookUtils;
import com.cc.core.wechat.Wechat;
import com.cc.core.xposed.BaseXposedHook;

import de.robv.android.xposed.XC_MethodHook;

import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_Alias;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_Nickname;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_Phone;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_Sex;
import static com.cc.core.wechat.Wechat.HookMethodFunctions.Account.UserInfoId_Signature;

public class LauchUIHooks extends BaseXposedHook {
    @Override
    public void hook(ClassLoader classLoader) {
        hookMethod(Wechat.Resources.LAUNCHER_UI_CLASS, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                /*User userInfo = new User();
                userInfo.setWechatId(HookUtils.Companion.getLoginUserWechatId());
                userInfo.setAlias((String) HookUtils.Companion.getLoginUserInfo(UserInfoId_Alias));
                userInfo.setNickname((String) HookUtils.Companion.getLoginUserInfo(UserInfoId_Nickname));
                userInfo.setSignature((String) HookUtils.Companion.getLoginUserInfo(UserInfoId_Signature));
                userInfo.setRegionCode(HookUtils.Companion.getLoginUserRegionCode());
                userInfo.setSex((Integer) HookUtils.Companion.getLoginUserInfo(UserInfoId_Sex, 0));
                userInfo.setPhone((String) HookUtils.Companion.getLoginUserInfo(UserInfoId_Phone));
                userInfo.setAvatar(HookUtils.Companion.getSelfBigAvatarUrl());
                DbService.getInstance().insertLoginUser(userInfo, new Callback() {
                    @Override
                    public void onResult(String result) {
                        KLog.e(">>>>" + result);
                    }
                });*/

            }
        });
    }
}
