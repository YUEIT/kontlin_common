package cn.yue.base.net.observer

import cn.yue.base.net.ResultException

/**
 * Description :
 * Created by yue on 2018/7/26
 */
abstract class BaseEndObserver<T> : BaseNetObserver<T>() {

    override fun onSuccess(t: T) {
        onEnd(true, t, null)
    }

    override fun onException(e: ResultException) {
        onEnd(false, null, e)
    }

    override fun onCancel(e: ResultException) {
        onEnd(false, null, e)
    }

    abstract fun onEnd(result: Boolean, t: T?, e: ResultException?)
}
