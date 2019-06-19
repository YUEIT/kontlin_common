package cn.yue.base.middle.net.observer

import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.net.ResultException

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseWaitSingleObserver<T> : BaseNetSingleObserver<T> {

    private var iBaseView: IBaseView
    private var title: String = ""
    constructor(iBaseView: IBaseView): super() {
        this.iBaseView = iBaseView
    }

    constructor(iBaseView: IBaseView, title: String): this(iBaseView) {
        this.title = title
    }

    override fun onStart() {
        super.onStart()
        iBaseView.showWaitDialog(title)
    }

    override fun onException(e: ResultException) {
        ToastUtils.showShortToast(e.code)
        iBaseView.dismissWaitDialog()
    }

    override fun onSuccess(t: T) {
        iBaseView.dismissWaitDialog()
        onNext(t)
    }

    override fun onCancel(e: ResultException) {
        super.onCancel(e)
        iBaseView.dismissWaitDialog()
    }

    abstract fun onNext(t: T)
}