package cn.yue.base.middle.net.observer

import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IPullView
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException

/**
 * Description :
 * Created by yue on 2019/4/1
 */
abstract class BasePullObserver<T>(private val iPullView: IPullView?) : BaseNetObserver<T>() {
    override fun onStart() {
        super.onStart()
    }

    override fun onSuccess(t: T) {
        if (iPullView != null) {
            iPullView.finishRefresh()
            iPullView.loadComplete(PageStatus.NORMAL)
        }
        onNext(t)
    }

    override fun onException(e: ResultException) {
        if (iPullView != null) {
            when {
                ResponseCode.ERROR_NO_NET == e.code -> {
                    iPullView.loadComplete(PageStatus.NO_NET)
                }
                ResponseCode.ERROR_NO_DATA == e.code -> {
                    iPullView.loadComplete(PageStatus.NO_DATA)
                }
                ResponseCode.ERROR_OPERATION == e.code -> {
                    iPullView.loadComplete(PageStatus.ERROR)
                    showShortToast(e.message)
                }
                else -> {
                    iPullView.loadComplete(PageStatus.ERROR)
                    showShortToast(e.message)
                }
            }
            iPullView.finishRefresh()
        }
    }

    override fun onCancel(e: ResultException) {
        super.onCancel(e)
        iPullView?.finishRefresh()
    }

    open fun onNext(t: T) {}

}