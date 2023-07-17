package cn.yue.base.wx

import android.os.Handler
import android.os.Looper
import cn.yue.base.net.RetrofitManager
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object WXApi {

    fun getAccessToken(appId: String, appSecret: String, code: String, loginCallback: LoginCallback) {
        val url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=${appId}&secret=${appSecret}&code=${code}&grant_type=authorization_code"
        val request = Request.Builder().url(url).get().build()
        val mCall = RetrofitManager.instance.getOkHttpClient().newCall(request)
        mCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                postUI { loginCallback.failure("授权失败") }
            }

            override fun onResponse(call: Call, response: Response) {
                val wxLogin = Gson().fromJson(response.body!!.string(), WXLoginEntity::class.java)
                if (wxLogin.accessToken == null) {
                    loginCallback.failure("授权失败")
                } else {
                    getUserInfo(wxLogin, loginCallback)
                }
            }
        })
    }

    fun getUserInfo(wxLoginEntity: WXLoginEntity, loginCallback: LoginCallback) {
        val url = "https://api.weixin.qq.com/sns/userinfo?" +
                "access_token=${wxLoginEntity.accessToken}&openid=${wxLoginEntity.openId}"
        val request = Request.Builder().url(url).get().build()
        val mCall = RetrofitManager.instance.getOkHttpClient().newCall(request)
        mCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                postUI { loginCallback.failure("获取用户信息失败") }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body
                if (body == null) {
                    postUI { loginCallback.failure("获取用户信息失败") }
                    return
                }
                val wxInfo = Gson().fromJson(body.string(), WXInfoEntity::class.java)
                postUI { loginCallback.success(wxInfo) }
            }
        })
    }

    fun postUI(block: () -> Unit) {
        Handler(Looper.getMainLooper()).post {
            block.invoke()
        }
    }
}