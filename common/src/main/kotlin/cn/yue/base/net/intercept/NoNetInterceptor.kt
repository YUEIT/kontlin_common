package cn.yue.base.net.intercept

import cn.yue.base.R
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.device.NetworkUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NoNetInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!NetworkUtils.isAvailable()) {
            throw ResultException(ResponseCode.ERROR_NO_NET, R.string.app_no_net.getString())
        } else {
            chain.proceed(chain.request())
        }
    }
}
