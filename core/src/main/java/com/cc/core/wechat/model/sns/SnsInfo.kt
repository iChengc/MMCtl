package com.cc.core.wechat.model.sns

class SnsInfo {
    private var description:String? = null
    private var medias : ArrayList<String>? = null
    private var type : Int = 0

    fun getType() : Int {
        return type
    }

    fun setType(type:Int) {
        this.type = type
    }

    fun getDescription() : String? {
        return description
    }

    fun setDescription(desc:String?) {
        this.description = desc
    }

    fun getMedias() : ArrayList<String>? {
        return medias
    }

    fun setMedias(media:ArrayList<String>?) {
        this.medias = media
    }
}