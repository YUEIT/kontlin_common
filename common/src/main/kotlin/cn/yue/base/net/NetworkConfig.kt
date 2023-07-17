package cn.yue.base.net

import cn.yue.base.utils.debug.LogUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.nio.charset.Charset

object ResponseCode {
    //与服务器约定的
    var SUCCESS_FLAG = "0"
    var ERROR_SERVER = "1"
    //token 失效
    var ERROR_TOKEN_INVALID = "-1000"
    var ERROR_LOGIN_INVALID = "-1001"
    //自定义的
    var ERROR_CANCEL = "-10000"
    var ERROR_NO_DATA = "-10001"
    var ERROR_NO_NET = "-10002"
    var ERROR_OPERATION = "-10004"
}

object CharsetConfig {
    val CONTENT_TYPE = "application/json; charset=UTF-8".toMediaTypeOrNull()
    val ENCODING: Charset = Charset.forName("UTF-8")
}

fun String.netLog() {
    LogUtils.i("okhttp", this)
}