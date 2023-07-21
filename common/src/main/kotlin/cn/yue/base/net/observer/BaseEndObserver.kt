package cn.yue.base.net.observer

import cn.yue.base.net.ResultException

/**
 * Description :
 * Created by yue on 2018/7/26
 */
class BaseEndObserver<T>(
    private val endBlock: ((result: Boolean, t: T?, e: ResultException?) -> Unit)? = null
) : BaseNetObserver<T>() {

    override fun onSuccess(t: T) {
        endBlock?.invoke(true, t, null)
    }

    override fun onException(e: ResultException) {
        endBlock?.invoke(false, null, e)
    }

}