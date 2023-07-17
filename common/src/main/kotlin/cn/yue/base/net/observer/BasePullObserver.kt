package cn.yue.base.net.observer

import cn.yue.base.mvp.IStatusView
import cn.yue.base.net.ResultException
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.net.ResponseCode
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2019/4/1
 */
abstract class BasePullObserver<T>(private val iStatusView: IStatusView?) : BaseNetObserver<T>() {

    override fun onStart() {
        super.onStart()
    }

    override fun onSuccess(t: T) {
        iStatusView?.changePageStatus(PageStatus.NORMAL)
        onNext(t)
    }

    override fun onException(e: ResultException) {
        when {
            ResponseCode.ERROR_NO_NET == e.code -> {
                iStatusView?.changePageStatus(PageStatus.NO_NET)
            }
            ResponseCode.ERROR_NO_DATA == e.code -> {
                iStatusView?.changePageStatus(PageStatus.NO_DATA)
            }
            ResponseCode.ERROR_OPERATION == e.code -> {
                iStatusView?.changePageStatus(PageStatus.ERROR)
                showShortToast(e.message)
            }
            else -> {
                iStatusView?.changePageStatus(PageStatus.ERROR)
                showShortToast(e.message)
            }
        }
    }

    override fun onCancel(e: ResultException) {
        super.onCancel(e)
        iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
    }

    open fun onNext(t: T) {}

}