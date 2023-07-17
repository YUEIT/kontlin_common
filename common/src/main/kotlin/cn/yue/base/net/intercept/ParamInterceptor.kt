package cn.yue.base.net.intercept

import cn.yue.base.init.InitConstant
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class ParamInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
                .url(original.url)
                .addHeader("Content-Type", "application/json")
                .addHeader("device", "android")
                .addHeader("version", InitConstant.getVersionName() ?: "")
                .addHeader("deviceId", InitConstant.getDeviceId() ?: "")
                .addHeader("token", InitConstant.getToken())
                .build()
        return chain.proceed(request)
    }
}