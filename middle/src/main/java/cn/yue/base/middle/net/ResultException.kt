package cn.yue.base.middle.net

/**
 * Intro: 用于Retrofit的网络失败的异常类，
 * 包含错误code 和 msg
 * Author: zhangxutong
 * E-mail: mcxtzhang@163.com
 * Home Page: http://blog.csdn.net/zxt0601
 * Created:   2017/2/10.
 * History:
 */
class ResultException(var errCode: String = "-1", val errorMsg: String) : RuntimeException(errorMsg) {

    val isIgnore: Boolean
        get() = isUserCancel || isNoDataError


    val isUserCancel: Boolean
        get() = NetworkConfig.ERROR_CANCEL == errCode

    val isNoDataError: Boolean
        get() = NetworkConfig.ERROR_NO_DATA == errCode

    override fun toString(): String {
        return super.toString() + ", ResultException{" +
                "errCode='" + errCode + '\''.toString() +
                '}'.toString()
    }
}