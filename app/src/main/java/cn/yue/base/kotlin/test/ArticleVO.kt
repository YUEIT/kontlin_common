package cn.yue.base.kotlin.test

import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Created by yue on 2018/11/23
 */
data class ArticleVO(
        @SerializedName("id")
        private var id: String? = null,
        @SerializedName("postType")
        private var postType: Int = 0,
        @SerializedName("channelId")
        private var channelId: Int = 0,
        @SerializedName("title")
        private var title: String? = null,
        @SerializedName("images")
        private var images: String? = null,
        @SerializedName("viewCt")
        private var viewCt: Int = 0,
        @SerializedName("weightedViewCt")
        private var weightedViewCt: Int = 0,
        @SerializedName("upCt")
        private var upCt: Int = 0,
        @SerializedName("commentCt")
        private var commentCt: Int = 0,
        @SerializedName("shareCt")
        private var shareCt: Int = 0,
        @SerializedName("slashCt")
        private var slashCt: Int = 0,
        @SerializedName("subScore")
        private var subScore: Int = 0,
        @SerializedName("createTime")
        private var createTime: Long = 0,
        @SerializedName("updateTime")
        private var updateTime: Long = 0,
        @SerializedName("isDeleted")
        private var isDeleted: Int = 0,
        @SerializedName("version")
        private var version: Int = 0,
        @SerializedName("content")
        private var content: String? = null,
        @SerializedName("longContent")
        private var longContent: String? = null,
        @SerializedName("channelName")
        private var channelName: String? = null,
        @SerializedName("imagesList")
        private var imagesList: List<String>? = null,
        @SerializedName("brandImage")
        private var brandImage: String? = null,
        @SerializedName("upFlag")
        private var upFlag: Int = 0,
        @SerializedName("brandCode")
        private var brandCode: String? = null,
        @SerializedName("brandName")
        private var brandName: String? = null )