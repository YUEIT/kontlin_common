package cn.yue.base.net.observer

import cn.yue.base.R
import cn.yue.base.event.NotifyViewModel
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.CancellationException

/**
 * Description : 网络请求回调，拦截登录失效
 * Created by yue on 2018/7/26
 */

abstract class BaseNetObserver<T> : SingleObserver<T> {

    override fun onSubscribe(d: Disposable) {

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
                onCancel()
            }
            else -> {
                onException(ResultException(ResponseCode.ERROR_SERVER, e.message?:""))
            }
        }
    }

    /**
     * 多数情况下，不需要额外处理，情况多数发生在绑定声明周期的页面销毁后的取消
     */
    open fun onCancel() {

    }

    private fun onLoginInvalid() {
        ToastUtils.showShortToast(R.string.app_login_fail.getString())
        NotifyViewModel.getLoadStatus().setValue(-1)
    }

}
