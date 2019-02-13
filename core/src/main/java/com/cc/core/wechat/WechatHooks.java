package com.cc.core.wechat;

public class WechatHooks {
	public static String[] HOOKS = new String[] {
		"com.cc.core.wechat.hook.ActivityHooks",
		"com.cc.core.wechat.hook.AvoidRiskHooks",
		"com.cc.core.wechat.hook.DbHooks",
		"com.cc.core.wechat.hook.LauchUIHooks",
		"com.cc.core.wechat.hook.MessageHooks",
		"com.cc.core.wechat.hook.NetsceneQueueHooks",
		"com.cc.core.wechat.hook.TestHooks",
		"com.cc.core.wechat.hook.UploadCrashLogHooks",
		"com.cc.core.wechat.hook.XLogHooks",
	};
}