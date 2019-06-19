package cn.yue.base.middle.net.intercept

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class ParamInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain?): Response {
        val orginal = chain!!.request()
        val url = orginal.url().newBuilder()
                .addQueryParameter("", "")
                .addQueryParameter("", "")
                .addQueryParameter("", "")
                .build()
        val request = orginal.newBuilder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .build()
        return chain.proceed(request)
    }
}