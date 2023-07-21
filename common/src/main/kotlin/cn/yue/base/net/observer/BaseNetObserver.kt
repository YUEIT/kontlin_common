package cn.yue.base.net.observer

import cn.yue.base.common.R
import cn.yue.base.event.AppViewModes
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import java.util.concurrent.CancellationException

/**
 * Description : 网络请求回调，拦截登录失效
 * Created by yue on 2018/7/26
 */

abstract class BaseNetObserver<T> : DisposableSingleObserver<T>() {
    
    public override fun onStart() {
        super.onStart()
    }

    abstract fun onException(e: ResultException)

    override fun onError(e: Throwable) {
        val resultException: ResultException
        when (e) {
            is ResultException -> {
                resultException = e
                if (resultException.code == ResponseCode.ERROR_TOKEN_INVALID
                    || resultException.code == ResponseCode.ERROR_LOGIN_INVALID) {
                    onLoginInvalid()
                    return
                }
                onException(resultException)
            }
            is CancellationException -> {
                onException(ResultException(ResponseCode.ERROR_CANCEL, R.string.app_request_cancel.getString()))
            }
            else -> {
                onException(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
            }
        }
    }

    private fun onLoginInvalid() {
        ToastUtils.showShortToast(R.string.app_login_fail.getString())
        AppViewModes.getNotifyViewModel().loginStatusLiveData.setValue(-1)
    }

}
