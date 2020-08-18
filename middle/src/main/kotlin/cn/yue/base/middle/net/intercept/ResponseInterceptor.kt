package cn.yue.base.middle.net.intercept

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class ResponseInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val proceed: Response = chain.proceed(request)
        val code: Int = proceed.code
        if (code != 200 && code != 404) {
            return proceed.newBuilder().code(200).build()
        }
        return proceed
    }
}