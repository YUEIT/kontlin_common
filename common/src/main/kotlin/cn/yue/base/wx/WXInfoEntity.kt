package cn.yue.base.wx

import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Created by yue on 2020/12/13
 */
class WXInfoEntity (
        @SerializedName("openid") val openId: String,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("sex") val sex: Int,
        @SerializedName("province") val province: String,
        @SerializedName("city") val city: String,
        @SerializedName("country") val country: String,
        @SerializedName("headimgurl") val headImgUrl: String,
        @SerializedName("unionid") val unionId: String,
        val privilege: List<String>
)