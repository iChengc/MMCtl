package com.cc.core.wechat

import com.cc.core.wechat.model.sns.SnsInfo
import de.robv.android.xposed.XposedHelpers.callMethod
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.XposedHelpers.newInstance

class SnsUtils {
    companion object {
        //region image
        //02-28 16:25:12.263 7490-7490/? E/>>>> UploadPackHelper#WU <<<<<: [ (SnsUploadPackHelperHooks.java:209)#beforeHookedMethod ] 贯彻落实
        //02-28 16:25:12.263 7490-7490/? E/>>>> UploadPackHelper#a <<<<<: [ (SnsUploadPackHelperHooks.java:281)#beforeHookedMethod ] {"bsZ":0.0,"qFe":0,"qFg":0,"score":0,"vQW":0,"vQX":0,"vgc":-1000.0,"vgd":-1000.0}
        // {"eGp":"由度空间","gfk":"上海市","qFc":"上海市浦东新区宁桥路600号","vQU":"qqmap_3472697279383746309","bsZ":0.0,"qFe":1,"qFg":0,"score":0,"vQW":0,"vQX":0,"vgc":121.611015,"vgd":31.253334}
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#ar <<<<<: [ (SnsUploadPackHelperHooks.java:287)#beforeHookedMethod ] []
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#Co <<<<<: [ (SnsUploadPackHelperHooks.java:362)#beforeHookedMethod ] 0
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#Cp <<<<<: [ (SnsUploadPackHelperHooks.java:368)#beforeHookedMethod ] 0
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#Cr <<<<<: [ (SnsUploadPackHelperHooks.java:380)#beforeHookedMethod ] 0
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#Cq <<<<<: [ (SnsUploadPackHelperHooks.java:374)#beforeHookedMethod ] 0
        //02-28 16:25:12.273 7490-7490/? E/>>>> UploadPackHelper#f <<<<<: [ (SnsUploadPackHelperHooks.java:320)#beforeHookedMethod ] null : null : null : 1 : 0
        //02-28 16:25:12.293 7490-7490/? E/>>>> UploadPackHelper#de <<<<<: [ (SnsUploadPackHelperHooks.java:293)#beforeHookedMethod ] ["xnhjcc"]
        //02-28 16:25:12.293 7490-7490/? E/>>>> UploadPackHelper#setSessionId <<<<<: [ (SnsUploadPackHelperHooks.java:344)#beforeHookedMethod ]
        //02-28 16:25:12.303 7490-7490/? E/>>>> UploadPackHelper#df <<<<<: [ (SnsUploadPackHelperHooks.java:299)#beforeHookedMethod ] [{"desc":"贯彻落实","path":"/storage/emulated/0/DCIM/Camera/IMG_20181024_174216.jpg","qbf":"","qbg":"","qbh":"","thumbPath":"","cCt":-1,"fileSize":0,"filterId":0,"height":-1,"qbd":0,"qbe":0,"qbi":false,"type":2,"width":-1}]
        //endregion

        fun generateSnsUploadPackHelper(snsInfo : SnsInfo) : Any {
            var sns : Any? = null
            sns = when(snsInfo.getType()) {
                0->{
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER),  2)
                }
                else-> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER),  1)
                }
            }

            callMethod(sns, Wechat.Hook.Sns.SnsSetDescriptionFun, snsInfo.getDescription())

            return Any()
        }

        //region share
        // UploadPackHelper#WZ <<<<<: [ (SnsUploadPackHelperHooks.java:239)#beforeHookedMethod ] 胖子的尴尬！胖老鼠卡下水道洞口 惊动消防队来救援(视频)
        //02-28 17:49:03.193 7490-7490/? E/>>>> UploadPackHelper#WX <<<<<: [ (SnsUploadPackHelperHooks.java:227)#beforeHookedMethod ] view.inews.qq.com/w/WXN20190228007200020?refer=nwx&bat_id=1114029339&cur_pos=0&grp_type=region&rg=4&newsGid=20190228_r9501831&grp_index=2&grp_id=1314029345&rate=2&pushid=2019022802&bkt=0&openid=o04IBADwyLkEIMxeKWKVU1bdaHj4&tbkt=C0&ft=0&groupid=1551346179&msgid=0&isappinstalled=0&key=0e43a58611e7182a6eeb15e373a72592151e0be9047dd3b8680e06a7bbe212f20a8375b1fd4da03fa27c53f55bf003b786610f2ecf4953e1fce3b3fe619c6ecb05275d18043fa3f0cbe6e20accdb4612&version=27000334&devicetype=android-21&wuid=oDdoCt_EBUvS7z2-XG8PebsIA4Kg&cv=0x27000334&dt=2&lang=zh_CN&pass_ticket=Dx21GhuU%2BOlSSJVXo8dfVI0HRxBAiGSgcwfA2iaBCeP1G5mMo5jVREXC0joXuKzS
        //02-28 17:49:03.203 7490-7490/? E/>>>> UploadPackHelper#WY <<<<<: [ (SnsUploadPackHelperHooks.java:233)#beforeHookedMethod ] view.inews.qq.com/w/WXN20190228007200020?refer=nwx&bat_id=1114029339&cur_pos=0&grp_type=region&rg=4&newsGid=20190228_r9501831&grp_index=2&grp_id=1314029345&rate=2&pushid=2019022802&bkt=0&openid=o04IBADwyLkEIMxeKWKVU1bdaHj4&tbkt=C0&ft=0&groupid=1551346179&msgid=0&isappinstalled=0&key=0e43a58611e7182a6eeb15e373a72592151e0be9047dd3b8680e06a7bbe212f20a8375b1fd4da03fa27c53f55bf003b786610f2ecf4953e1fce3b3fe619c6ecb05275d18043fa3f0cbe6e20accdb4612&version=27000334&devicetype=android-21&wuid=oDdoCt_EBUvS7z2-XG8PebsIA4Kg&cv=0x27000334&dt=2&lang=zh_CN&pass_ticket=Dx21GhuU%2BOlSSJVXo8dfVI0HRxBAiGSgcwfA2iaBCeP1G5mMo5jVREXC0joXuKzS
        //02-28 17:49:03.203 7490-7490/? E/>>>> UploadPackHelper#WU <<<<<: [ (SnsUploadPackHelperHooks.java:209)#beforeHookedMethod ] 默默
        //02-28 17:49:03.213 7490-7490/? E/====++MicroMsg.snsMediaStorage++==>>>>>>: [ (TestHooks.java:133)#beforeHookedMethod ] {"desc":"","path":"/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/sns/temp/b997fbe5601fdfd78aab6d9899fb3383","qbf":"","qbg":"","qbh":"","thumbPath":"","cCt":-1,"fileSize":0,"filterId":0,"height":-1,"qbd":0,"qbe":0,"qbi":false,"type":2,"width":-1}
        //    java.lang.Exception
        //        at com.cc.core.wechat.hook.TestHooks$12.beforeHookedMethod(TestHooks.java:133)
        //        at com.tencent.mm.plugin.sns.storage.s.a(<Xposed>)
        //        at com.tencent.mm.plugin.sns.storage.s.dl(SourceFile:299)
        //        at com.tencent.mm.plugin.sns.model.ax.fU(SourceFile:460)
        //        at com.tencent.mm.plugin.sns.model.ax.A(SourceFile:414)
        //        at com.tencent.mm.plugin.sns.model.ax.a(SourceFile:689)
        //        at com.tencent.mm.plugin.sns.model.ax.b(SourceFile:685)
        //        at com.tencent.mm.plugin.sns.ui.z.a(SourceFile:362)
        //        at com.tencent.mm.plugin.sns.ui.SnsUploadUI$2$1.jK(SourceFile:564)
        //        at com.tencent.mm.ui.tools.b.c.dfA(SourceFile:160)
        //        at com.tencent.mm.ui.tools.b.c.a(SourceFile:112)
        //        at com.tencent.mm.plugin.sns.ui.SnsUploadUI$2.onMenuItemClick(SourceFile:535)
        //        at com.tencent.mm.ui.q.a(SourceFile:1231)
        //        at com.tencent.mm.ui.q.a(SourceFile:89)
        //        at com.tencent.mm.ui.q$13.onClick(SourceFile:1048)
        //        at android.view.View.performClick(View.java:4760)
        //        at android.view.View$PerformClick.run(View.java:19762)
        //        at android.os.Handler.handleCallback(Handler.java:739)
        //        at android.os.Handler.dispatchMessage(Handler.java:95)
        //        at android.os.Looper.loop(Looper.java:135)
        //        at android.app.ActivityThread.main(ActivityThread.java:5336)
        //        at java.lang.reflect.Method.invoke(Native Method)
        //        at java.lang.reflect.Method.invoke(Method.java:372)
        //        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:904)
        //        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:699)
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Cq <<<<<: [ (SnsUploadPackHelperHooks.java:374)#beforeHookedMethod ] 3
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Xc <<<<<: [ (SnsUploadPackHelperHooks.java:257)#beforeHookedMethod ]
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Xd <<<<<: [ (SnsUploadPackHelperHooks.java:263)#beforeHookedMethod ]
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Co <<<<<: [ (SnsUploadPackHelperHooks.java:362)#beforeHookedMethod ] 0
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#Cr <<<<<: [ (SnsUploadPackHelperHooks.java:380)#beforeHookedMethod ] 0
        //02-28 17:49:03.273 7490-7490/? E/>>>> UploadPackHelper#de <<<<<: [ (SnsUploadPackHelperHooks.java:293)#beforeHookedMethod ] null
        //02-28 17:49:03.283 7490-7490/? E/>>>> UploadPackHelper#ar <<<<<: [ (SnsUploadPackHelperHooks.java:287)#beforeHookedMethod ] []
        //02-28 17:49:03.283 7490-7490/? E/>>>> UploadPackHelper#a <<<<<: [ (SnsUploadPackHelperHooks.java:281)#beforeHookedMethod ] {"bsZ":0.0,"qFe":0,"qFg":0,"score":0,"vQW":0,"vQX":0,"vgc":-1000.0,"vgd":-1000.0}
        //endregion share
    }
}