package cn.yue.base.net.observer

import cn.yue.base.mvp.IWaitView
import cn.yue.base.net.ResultException
import cn.yue.base.utils.debug.ToastUtils

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseWaitObserver<T>(
        private val iWaitView: IWaitView
    ) : BaseNetObserver<T>() {

    private var title: String = ""

    constructor(iWaitView: IWaitView, title: String): this(iWaitView) {
        this.title = title
    }

    override fun onStart() {
        super.onStart()
        iWaitView.showWaitDialog(title)
    }

    override fun onException(e: ResultException) {
        ToastUtils.showShortToast(e.code)
        iWaitView.dismissWaitDialog()
    }

    override fun onSuccess(t: T) {
        iWaitView.dismissWaitDialog()
        onNext(t)
    }

    override fun onCancel(e: ResultException) {
        super.onCancel(e)
        iWaitView.dismissWaitDialog()
    }

    abstract fun onNext(t: T)
}