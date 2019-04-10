package com.cc.core.wechat

import android.database.Cursor
import android.text.TextUtils
import android.util.Log
import com.cc.core.log.KLog
import com.cc.core.utils.FileUtil
import com.cc.core.utils.ImageUtil
import com.cc.core.utils.MD5
import com.cc.core.utils.StrUtils
import com.cc.core.wechat.Wechat.Hook.ProtobufParseFromFunc
import com.cc.core.wechat.Wechat.Hook.Sns.*
import com.cc.core.wechat.Wechat.Hook.Sqlite.DBRawQueryFunc
import com.cc.core.wechat.hook.tool.CdnLogicHooks
import com.cc.core.wechat.model.sns.SnsComment
import com.cc.core.wechat.model.sns.SnsInfo
import com.cc.core.wechat.model.sns.SnsLike
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.*
import java.io.File
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class SnsUtils {
    companion object {
        private const val VideoPlaceholderName = "SNS_sns_table_"
        private const val Query_SnsId_Sql = "SELECT snsId FROM SnsInfo WHERE snsId < %s AND snsId != 0 ORDER BY createTime DESC ,snsId DESC LIMIT 10;"

        private const val Query_Latest_SnsId_Sql = "SELECT snsId FROM SnsInfo WHERE snsId != 0 ORDER BY createTime DESC ,snsId DESC LIMIT 10;"

        private val lock = ArrayBlockingQueue<String>(1)
        //region upload sns info
        fun generateSnsUploadPackHelper(snsInfo: SnsInfo): Any? {
            /*var sns: Any? = when (snsInfo.getSnsType()) {
                SnsInfo.TEXT_TYPE -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), SnsInfo.TEXT_TYPE)
                }
                SnsInfo.VIDEO_TYPE -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), SnsInfo.VIDEO_TYPE)
                }
                SnsInfo.CARD_TYPE -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER),  SnsInfo.CARD_TYPE)
                }
                else -> {
                    newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), SnsInfo.IMAGE_TYPE)
                }
            }*/
            val sns = newInstance(findClass(Wechat.Hook.Sns.SnsUploadPackHelper, Wechat.WECHAT_CLASSLOADER), snsInfo.getSnsType())
            setTextSns(sns, snsInfo)

            when (snsInfo.getSnsType()) {
                SnsInfo.IMAGE_TYPE -> {
                    setImageSns(sns, snsInfo)
                }
                SnsInfo.VIDEO_TYPE -> {
                    setVideoSns(sns, snsInfo)
                }
                SnsInfo.CARD_TYPE -> {
                    setCardSns(sns, snsInfo)
                }
                else -> {
                    return sns
                }
            }

            return sns
        }

        private fun setTextSns(sns: Any?, snsInfo: SnsInfo) {
            if (sns == null) {
                return
            }
            callMethod(sns, Wechat.Hook.Sns.SnsSetDescriptionFun, snsInfo.getDescription())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTypeFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetWatcherTypeFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetSyncQQZoneFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetIsPrivateFun, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetWatchersFun, ArrayList<String>())
            callMethod(sns, Wechat.Hook.Sns.SnsSetAtUsersFun, LinkedList<Any>())
            callMethod(sns, Wechat.Hook.Sns.SnsSetSessionIdFun, "")
            callMethod(sns, Wechat.Hook.Sns.SnsSetLocationFun, newInstance(findClass(Wechat.Hook.Sns.LocationClass, Wechat.WECHAT_CLASSLOADER)))
        }

        private fun setImageSns(sns: Any?, snsInfo: SnsInfo) {
            if (sns == null) {
                return
            }

            callMethod(sns, Wechat.Hook.Sns.SnsSetUrlFun, "", null, null, 0, 0)
            callMethod(sns, Wechat.Hook.Sns.SnsSetMediaInfoFun, genMediaList(snsInfo.getMedias(), snsInfo.getDescription()))
        }

        private fun setVideoSns(sns: Any?, snsInfo: SnsInfo): Boolean {
            if (snsInfo.getMedias() == null || snsInfo.getMedias()!!.isEmpty()) {
                return false
            }
            val videoPath = snsInfo.getMedias()!![0]
            val caverPath = ImageUtil.getVideoPathCover(videoPath) ?: return false

            val md5 = HookUtils.getFileMd5(videoPath) ?: return false

            callMethod(sns, Wechat.Hook.Sns.SnsSetVideoInfoFun, videoPath, caverPath, snsInfo.getDescription(), md5)

            /*callMethod(sns, Wechat.Hook.Sns.SnsSetVideoInfoFun, "/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/video/videoCompressTmp/video_send_preprocess_tmp_1551376714007.mp4",
                    "/storage/emulated/0/tencent/MicroMsg/f8db84cda2ef6f6aa5adde1594c88c36/video/videoCompressTmpThumb/video_send_preprocess_thumb_1551376762312.jpg",
                    snsInfo.getDescription(), "4f4de48085c93716a8b8d6742f3812da")*/
            return true
        }

        private fun setCardSns(sns: Any?, snsInfo: SnsInfo) {
            // SessionId@msg_-1049477600#66650946219
            callMethod(sns, Wechat.Hook.Sns.SnsSetSessionIdFun, callStaticMethod(findClass(SnsGetSessionIdUtil, Wechat.WECHAT_CLASSLOADER), SnsGetSessionIdFun,"sns_" + Math.abs(StrUtils.stringNotNull(snsInfo.getUrl()).hashCode())))

            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTypeFun, 1)
            callMethod(sns, Wechat.Hook.Sns.SnsSetUrlFun, snsInfo.getUrl(), snsInfo.getUrl(), "", 1, 0)
            if (!(snsInfo.getMedias() == null || snsInfo.getMedias()!!.isEmpty())) {
                val thumb = ImageUtil.fileToWxThumb(snsInfo.getMedias()!![0])
                if (thumb != null) {
                    callMethod(sns, Wechat.Hook.Sns.SnsSetShareThumbFun, thumb, "", "")
                }
            }
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareUrlFun, snsInfo.getUrl())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareUrl2Fun, snsInfo.getUrl())
            callMethod(sns, Wechat.Hook.Sns.SnsSetShareTitleFun, snsInfo.getShareTitle())
        }

        fun uploadSns() {
            val aw = newInstance(XposedHelpers.findClass(Wechat.Hook.Sns.UploadManager, Wechat.WECHAT_CLASSLOADER))
            callMethod(aw, Wechat.Hook.Sns.UploadFun)
        }

        private fun genMediaList(medias: ArrayList<String>?, desc: String?): Any {
            val mediasStorage = LinkedList<Any>()
            if (medias == null) return mediasStorage
            for (m in medias) {
                val data = newInstance(findClass("com.tencent.mm.plugin.sns.data.h", Wechat.WECHAT_CLASSLOADER), m, 2)
                setObjectField(data, "desc", desc)
                mediasStorage.add(data)
            }
            return mediasStorage
            /*val picWidget = newInstance(findClass("", Wechat.WECHAT_CLASSLOADER), null)
            val bundle = Bundle()
            bundle.putStringArrayList("sns_kemdia_path_list", medias)
            callMethod(picWidget, "F", bundle)
            return picWidget*/
        }

        //endregion

        //region get sns
        fun getSnsList(lastSnsId: Long): List<SnsInfo>? {
            val snsIds = ArrayList<Long>()
            executeSnsDbRawQuery(if (lastSnsId == 0L) {
                Query_Latest_SnsId_Sql
            } else {
                String.format(Query_SnsId_Sql, lastSnsId.toString())
            }).use { cursor ->
                while (cursor.moveToNext()) {
                    snsIds.add(cursor.getLong(0))
                }

                cursor.close()
            }

            val snsInfos = ArrayList<SnsInfo>()
            var index = 0
            for (id in snsIds) {
                val sns = getWechatRawSnsInfo(id) ?: continue
                val info = SnsInfo()
                //info.setSnsType()

                info.setSnsId(getObjectField(sns, "field_snsId").toString())
                info.setUserName(getObjectField(sns, "field_userName") as String)
                info.setCreateTime((getObjectField(sns, "field_createTime") as Int).toLong())

                parseComment(sns, info)

                val snsInfoTimeline = callMethod(sns, SnsStorage2Timeline)
                // KLog.e("+++++------>>>>>++++++", StrUtils.toJson(snsInfoTimeline))

                info.setSnsType(getObjectField(sns, "field_type") as Int)
                val sourceType = getObjectField(sns, "field_sourceType") as Int
                info.setIsDeleted(sourceType == 0)
                if (info.isDeleted()) {
                    snsInfos.add(info)
                    continue
                }

                info.setDescription(getObjectField(snsInfoTimeline, SnsTimeLineContentField) as String)
                val details = getObjectField(snsInfoTimeline, SnsTimeLineDetailsField)

                when (info.getSnsType()) {
                    SnsInfo.MUSIC,
                    SnsInfo.SHARE_VIDEO,
                    SnsInfo.CARD_TYPE -> {
                        parseCardSnsTimeLine(details, info)
                    }
                    SnsInfo.VIDEO_TYPE -> {
                        ++index
                        parseVideoSnsTimeLine(details, info, index)
                    }
                    SnsInfo.IMAGE_TYPE -> {
                        parseImageSnsTimeLine(details, info)
                    }
                }
                snsInfos.add(info)
            }
            return snsInfos
        }

        private fun parseComment(sns: Any, snsInfo: SnsInfo) {

            val commentBuf = getObjectField(sns, "field_attrBuf")
            val comment = newInstance(findClass(SnsTimelineCommentLikeProtobuf, Wechat.WECHAT_CLASSLOADER))

            callMethod(comment, ProtobufParseFromFunc, commentBuf)
            // KLog.e("+++++------>>>>>++++++", StrUtils.toJson(comment))

            // 获取点赞的记录
            val likeRaw = getObjectField(comment, SnsTimelineLikeListField)
            if (likeRaw != null) {
                val likeList = likeRaw as List<*>
                val likes = ArrayList<SnsLike>(likeList.size)
                for (l in likeList) {
                    val like = SnsLike()
                    like.createTime = getLongField(l, SnsTimelineCommentTimeField)
                    like.id = getIntField(l, SnsTimelineCommentIdField)
                    like.nickName = getObjectField(l, SnsTimelineCommenterNameField)!! as String
                    like.wechatId = getObjectField(l, SnsTimelineCommenterField)!! as String
                    likes.add(like)
                }
                snsInfo.setLikes(likes)
            }

            // 获取评论的记录
            val commentRaw = getObjectField(comment, SnsTimelineCommentListField)
            if (commentRaw != null) {
                val commentList = commentRaw as List<*>
                val comments = ArrayList<SnsComment>(commentList.size)
                for (l in commentList) {
                    val comment = SnsComment()
                    comment.createTime = getLongField(l, SnsTimelineCommentTimeField)
                    comment.nickName = getObjectField(l, SnsTimelineCommenterNameField)!! as String
                    comment.wechatId = getObjectField(l, SnsTimelineCommenterField)!! as String
                    comment.content = getObjectField(l, SnsTimelineCommentContentField)!! as String
                    comment.id = getIntField(l, SnsTimelineCommentIdField)
                    comment.replayId = getIntField(l, SnsTimelineCommentReplay2IdField)
                    val reply2 = getObjectField(l, SnsTimelineCommentReplay2Field)
                    if (reply2 != null) {
                        comment.reply2 = reply2 as String
                    }
                    comments.add(comment)
                }
                snsInfo.setComments(comments)
            }
        }

        fun getWechatRawSnsInfo(snsId: Long): Any? {
            return callMethod(getSnsInfoStorage(), SnsStorageGetBySnsId, snsId)
        }

        private fun executeSnsDbRawQuery(rawQuery: String): Cursor {
            return callMethod(getSnsDBManager(), DBRawQueryFunc, rawQuery, null) as Cursor
        }

        private fun getSnsDBManager(): Any? {
            return getObjectField(callStaticMethod(findClass(SnsCoreClass, Wechat.WECHAT_CLASSLOADER),
                    SnsCoreGetInstance), SnsCoreStorageField)
        }

        private fun getSnsInfoStorage(): Any {
            return callStaticMethod(findClass(SnsCoreClass, Wechat.WECHAT_CLASSLOADER), SnsCoreGetSnsInfoStorage)
        }

        private fun parseCardSnsTimeLine(snsTimeLine: Any?, snsInfo: SnsInfo): SnsInfo {
            if (snsTimeLine == null) {
                return snsInfo
            }

            val shareUrl = getObjectField(snsTimeLine, SnsTimeLineShareUrlField) as String
            snsInfo.setUrl(shareUrl)

            val shareTitle = getObjectField(snsTimeLine, SnsTimeLineShareTitleField)
            snsInfo.setShareTitle(if (shareTitle == null) {
                ""
            } else {
                shareTitle as String
            })

            val rawMedias = getObjectField(snsTimeLine, SnsTimeLineMediaField) ?: return snsInfo
            val medias = rawMedias as List<*>
            for (m in medias) {
                val thumb = getObjectField(m, SnsTimeLineMediaUrlField) ?: return snsInfo
                snsInfo.addMedia(thumb as String)
            }

            return snsInfo
        }

        private fun parseImageSnsTimeLine(snsTimeLine: Any?, snsInfo: SnsInfo): SnsInfo {
            if (snsTimeLine == null) {
                return snsInfo
            }

            val rawMedias = getObjectField(snsTimeLine, SnsTimeLineMediaField) ?: return snsInfo
            val medias = rawMedias as List<*>
            for (m in medias) {
                /*val thumb = getObjectField(m, SnsTimeLineMediaUrlField) ?: return snsInfo
                snsInfo.addMedia(thumb as String)*/
                downloadSnsImage(snsInfo, m)
            }
            return snsInfo
        }

        private fun parseVideoSnsTimeLine(snsTimeLineDetails: Any?, snsInfo: SnsInfo, index: Int): SnsInfo {
            if (snsTimeLineDetails == null) {
                return snsInfo
            }

            val rawMedias = getObjectField(snsTimeLineDetails, SnsTimeLineMediaField)
                    ?: return snsInfo
            val medias = rawMedias as List<*>
            for (m in medias) {
                val thumb = getObjectField(m, SnsTimeLineMediaUrlField) ?: return snsInfo
                snsInfo.addMedia(thumb as String)
            }
            downloadSnsVideo(snsInfo, index)
            return snsInfo
        }

        private fun getDownloadManager(): Any? {
            return callStaticMethod(findClass(SnsCoreClass, Wechat.WECHAT_CLASSLOADER), SnsCoreGetDownloadManager)
        }

        fun getLazyImageLoader(): Any {
            return callStaticMethod(findClass(SnsCoreClass, Wechat.WECHAT_CLASSLOADER), SnsCoreGetLazyImageLoaderFunc)
        }

        private fun downloadSnsImage(snsInfo: SnsInfo, media: Any?) {
            if (media == null) {
                return
            }
            val snsScene = callStaticMethod(findClass(SnsTimelineScene, Wechat.WECHAT_CLASSLOADER), SnsTimelineImageSceneGen)
            XposedHelpers.setIntField(snsScene, "time", snsInfo.getCreateTime().toInt())
            callMethod(getDownloadManager(), SnsTimelineStartImageDownload, media, 2, null, snsScene)
            val imagePath = callMethod(getLazyImageLoader(), SnsGetImageLocalPathFunc, media) as String

            waiteImageDownloadFinish(snsInfo, imagePath)
        }

        private fun waiteImageDownloadFinish(snsInfo: SnsInfo, imagePath: String) {
            if (File(imagePath).exists()) {
                imageDownloadFinished(snsInfo, imagePath)
                return
            }
            val lock = ArrayBlockingQueue<String>(1)
            XposedHelpers.findAndHookMethod(findClass(SnsDownloadManagerClass, Wechat.WECHAT_CLASSLOADER),
                    "a",
                    Int::class.javaPrimitiveType,
                    findClass(SnsMediaClass, Wechat.WECHAT_CLASSLOADER),
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Int::class.javaPrimitiveType, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    lock.offer("finish")
                }
            })

            val result = lock.poll(2, TimeUnit.MINUTES)
            if (!TextUtils.isEmpty(result)) {
                imageDownloadFinished(snsInfo, imagePath)
            }
        }

        private fun imageDownloadFinished(snsInfo: SnsInfo, imagePath: String) {
            val localPath = File(FileUtil.getImageCacheDirectory(), MD5.getMD5(imagePath) + ".jpg").absolutePath
            FileUtil.copyFile(File(imagePath), localPath)
            //KLog.e("====+++++ Image download finished +++++====", imagePath + " :: " + localPath)
            snsInfo.addMedia(localPath)
        }

        private fun downloadSnsVideo(snsInfo: SnsInfo, index: Int) {
            if (snsInfo.getMedias() == null) return
            insertSnsVideoPlaceholder(index)
            val videoPath = File(FileUtil.getVideoCacheDirectory(), "sight_${System.currentTimeMillis()}").absolutePath
            val snsScene = callStaticMethod(findClass(SnsTimelineScene, Wechat.WECHAT_CLASSLOADER), SnsTimelineVideoSceneGen)
            setIntField(snsScene, "time", snsInfo.getCreateTime().toInt())

            val cdnInfo = callStaticMethod(findClass(SnsTimelineOnlineVideoService, Wechat.WECHAT_CLASSLOADER), SnsTimelineGenVideoCdnInfo,
                    snsScene, snsInfo.getMedias()!![0],
                    videoPath,
                    "$VideoPlaceholderName-$index", 0)
            val fileKey = getObjectField(cdnInfo, "field_mediaId") as String
            val cdnRequest = callStaticMethod(findClass(SnsTimelineVideoCdnRequest, Wechat.WECHAT_CLASSLOADER), SnsTimelineVideoCdnRequestGen)
            callMethod(cdnRequest, SnsTimelineVideoCdnRequestSend, cdnInfo, false)
            val path = waitForDownloadFinish(fileKey, videoPath)
            snsInfo.getMedias()!!.clear()
            snsInfo.addMedia(path)
        }

        private fun insertSnsVideoPlaceholder(index: Int) {
            HookUtils.executeRawQuery("select * from videoinfo2 where filename = '$VideoPlaceholderName-$index'").use { cursor ->
                if (cursor.moveToNext()) {
                    HookUtils.execWritableSql("delete from videoinfo2 where filename = '$VideoPlaceholderName-$index'")
                }
                HookUtils.execWritableSql("insert into videoinfo2 (filename, clientid, msgsvrid," +
                        "netoffset,filenowsize, totallen, thumbnetoffset, thumblen,status,createtime," +
                        "lastmodifytime,downloadtime,videolength, msglocalid,nettimes,cameratype,'user'," +
                        "human,reserved1,reserved2,reserved3,reserved4,videofuncflag,masssendid,masssendlist,video" +
                        "md5,statextstr, downloadscene, preloadsize,videoformat) values ('$VideoPlaceholderName-$index'," +
                        " '', 0, 0, 0, 0, 0, 0, 0, ${System.currentTimeMillis() / 1000}, ${System.currentTimeMillis() / 1000}," +
                        " 0, 0, 0, 0, 0,'','', 0, 0, '', '', 0, 0, '', '', '', 30, 0, 0)")
                cursor.close()
            }
        }

        private fun waitForDownloadFinish(fileKey: String?, videoPath: String?): String? {
            CdnLogicHooks.registerDownloadListeners(fileKey) { lock.put(videoPath) }
            return lock.poll(2, TimeUnit.MINUTES)
        }

        //endregion
    }
}