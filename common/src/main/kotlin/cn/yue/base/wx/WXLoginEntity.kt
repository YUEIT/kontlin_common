package cn.yue.base.wx

import com.google.gson.annotations.SerializedName

/**
 * Description :
 * Created by yue on 2020/12/13
 */
class WXLoginEntity (
        //接口调用凭证
        @SerializedName("access_token") var accessToken: String?,
        //调用凭证超时时间（秒）
        @SerializedName("expires_in") var expiresIn: String?,
        //用户刷新access_token
        @SerializedName("refresh_token") var refreshToken: String?,
        //授权用户唯一标识
        @SerializedName("openid") var openId: String?,
        //用户授权的作用域
        @SerializedName("scope") var scope: String?,
        @SerializedName("unionid") var unionId: String?
)