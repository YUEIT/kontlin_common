package cn.yue.base.middle.net.intercept

import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NoNetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!NetworkUtils.isConnected()) {
            throw ResultException(ResponseCode.ERROR_NO_NET, "无网络: ${chain.request().url}")
        } else {
            chain.proceed(chain.request())
        }
    }
}
