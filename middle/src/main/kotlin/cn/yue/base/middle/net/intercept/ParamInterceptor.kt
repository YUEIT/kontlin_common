package cn.yue.base.middle.net.intercept

import cn.yue.base.middle.init.InitConstant
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class ParamInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
                .addQueryParameter("version", InitConstant.getVersionName())
                .addQueryParameter("device", "android")
                .addQueryParameter("deviceId", InitConstant.getDeviceId())
                .build()
        val request = original.newBuilder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build()
        return chain.proceed(request)
    }
}