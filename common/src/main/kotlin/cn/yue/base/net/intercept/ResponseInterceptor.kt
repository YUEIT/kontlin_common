package cn.yue.base.net.intercept

import cn.yue.base.R
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.wrapper.BaseBean
import cn.yue.base.utils.code.getString
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Description :
 * Created by yue on 2019/6/18
 */
class ResponseInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val proceed: Response = chain.proceed(request)
        val statusCode: Int = proceed.code
        if (statusCode < 200 || statusCode >= 300) {
            val body = proceed.body
                    ?: throw ResultException(ResponseCode.ERROR_NO_NET, R.string.app_server_response_fail.getString())
            try {
                val (message, code, data) = Gson().fromJson(body.string(), BaseBean::class.java)
                if (code == null) {
                    throw ResultException(ResponseCode.ERROR_SERVER, R.string.app_server_response_fail.getString())
                } else {
                    throw ResultException(code, message ?: "")
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                throw ResultException(ResponseCode.ERROR_SERVER, R.string.app_convert_data_fail.getString())
            }
        }
        return proceed
    }

}