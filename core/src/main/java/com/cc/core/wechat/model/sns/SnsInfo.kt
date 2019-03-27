package com.cc.core.wechat.model.sns

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * 分享方式发的与普通方式发的type一样
 * field_type == 1, 朋友圈图片加文字
 * tML（解码content，通过bfq.tML拿），文字内容
 * 图片（解码content，通过mVar.bfq().tMO.tcl.iterator() mOo拿id，再根据id生成地址前缀，再拼装）, 获取见逻辑
 * <p>
 * field_type == 2, 纯文字
 * tML（解码content，通过bfq.tML拿）, 文字内容
 * <p>
 * field_type == 3, 卡片
 * th1, th2, 缩略图
 * extUrl, 链接
 * cTxt, 图文标题（要看原分享链接中是否填入)
 * tML（解码content，通过bfq.tML拿），文字内容
 * <p>
 * field_type == 4, 音乐
 * field_type == 5, 短视频 (抖音等)
 * <p>
 * field_type == 10, 表情
 * th1, th2, 缩略图
 * tML（解码content，通过bfq.tML拿），文字内容
 * <p>
 * field_type == 15, 视频
 * th1, th2, 视频
 * TXT, 文字内容
 * <p>
 * field_type == 7, 本人朋友圈背景
 * th1, th2, 缩略图
 */

/**
 * sourceType: 朋友圈来源类型, flag enum
 * 0 - 现在不可见: 对方已删除/曾经可见但超过三天且对方设置不可见/曾经可见但对方最近设置了不让看朋友圈/曾经可见不再是好友
 * 2 - 普通朋友圈
 * 4 - 自己的个人相册
 * 8 - 对方的个人相册
 * 32 - 广告
 */
class SnsInfo {
    companion object {
        const val IMAGE_TYPE = 1
        const val TEXT_TYPE = 2
        const val CARD_TYPE = 3
        const val MUSIC = 4
        const val SHARE_VIDEO = 5 // 视频分享
        const val EMOJI = 10
        const val VIDEO_TYPE = 15
    }


    @SerializedName("snsId")
    private var snsId: String? = null//朋友圈id
    private var createTime: Long = 0L //创建时间
    private var userName: String? = null //创建人微信id
    private var isDeleted : Boolean = false

    private var description: String? = null
    private var medias: ArrayList<String>? = null
    private var snsType: Int = 0
    private var url: String? = null
    private var shareTitle: String? = null

    private var comments : List<SnsComment>? = null
    private var likes : List<SnsLike>? = null

    fun getSnsId(): String? {
        return snsId
    }

    fun setSnsId(snsId: String?) {
        this.snsId = snsId
    }

    fun getCreateTime(): Long {
        return createTime
    }

    fun setCreateTime(createTime: Long) {
        this.createTime = createTime
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String) {
        this.userName = userName
    }

    fun getSnsType(): Int {
        return snsType
    }

    fun setSnsType(type: Int) {
        this.snsType = type
    }

    fun getDescription(): String? {
        return description
    }

    fun setDescription(desc: String?) {
        this.description = desc
    }

    fun getMedias(): ArrayList<String>? {
        return medias
    }

    fun setMedias(media: ArrayList<String>?) {
        this.medias = media
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String?) {
        this.url = url
    }

    fun getShareTitle(): String? {
        return shareTitle
    }

    fun setShareTitle(title: String?) {
        this.shareTitle = title
    }

    fun isDeleted() : Boolean {
        return isDeleted
    }

    fun setIsDeleted(isDeleted : Boolean) {
        this.isDeleted = isDeleted
    }

    fun getLikes() : List<SnsLike>? {
        return likes
    }

    fun setLikes(likes :  List<SnsLike>?) {
        this.likes = likes
    }

    fun getComments() : List<SnsComment>? {
        return comments
    }

    fun setComments(comments :  List<SnsComment>?) {
        this.comments = comments
    }

    fun addMedia(media : String?) {
        if (TextUtils.isEmpty(media)) {
            return
        }

        if (medias == null) {
            medias = ArrayList()
        }

        medias!!.add(media!!)
    }
}