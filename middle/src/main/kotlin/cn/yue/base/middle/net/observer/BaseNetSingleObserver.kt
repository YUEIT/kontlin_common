package cn.yue.base.middle.net.observer

import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import io.reactivex.observers.DisposableSingleObserver
import java.util.concurrent.CancellationException

/**
 * Description : 网络请求回调，拦截登录失效
 * Created by yue on 2018/7/26
 */

abstract class BaseNetSingleObserver<T> : DisposableSingleObserver<T>() {


    public override fun onStart() {
        super.onStart()
    }

    abstract fun onException(e: ResultException)

    open fun onCancel(e: ResultException) {}

    override fun onError(e: Throwable) {
        val resultException: ResultException
        if (e is ResultException) {
            resultException = e
            if (resultException.code == ResponseCode.ERROR_TOKEN_INVALID
                    || resultException.code == ResponseCode.ERROR_LOGIN_INVALID) {
                onLoginInvalid()
                return
            }
            onException(resultException)
        } else if (e is CancellationException) {
            onCancel(ResultException(ResponseCode.ERROR_CANCEL, "请求取消~"))
        } else {
            onException(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
        }
    }

    private fun onLoginInvalid() {
        if (invalid != null) {
            ToastUtils.showShortToast("登录失效~")
            invalid!!.onInvalid()
        }
    }

    interface LoginInvalidListener {
        fun onInvalid()
    }

    companion object {

        private var invalid: LoginInvalidListener? = null

        fun setLoginInvalidListener(invalid: LoginInvalidListener) {
            BaseNetSingleObserver.invalid = invalid
        }
    }
}
