package cn.yue.base.net.observer

import cn.yue.base.mvp.IWaitView
import cn.yue.base.net.ResultException
import cn.yue.base.utils.debug.ToastUtils
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseWaitObserver<T>(
        private val iWaitView: IWaitView,
        private val title: String? = ""
    ) : BaseNetObserver<T>() {

    override fun onSubscribe(d: Disposable) {
        iWaitView.showWaitDialog(title ?: "")
    }

    override fun onException(e: ResultException) {
        ToastUtils.showShortToast(e.code)
        iWaitView.dismissWaitDialog()
    }

    override fun onSuccess(t: T) {
        iWaitView.dismissWaitDialog()
    }
}