package cn.yue.base.net.observer

import cn.yue.base.mvp.IStatusView
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2019/4/1
 */
abstract class BasePullObserver<T>(private val iStatusView: IStatusView?) : BaseNetObserver<T>() {
    
    override fun onSuccess(t: T) {
        iStatusView?.changePageStatus(PageStatus.NORMAL)
    }

    override fun onException(e: ResultException) {
        when(e.code) {
            ResponseCode.ERROR_NO_NET -> {
                iStatusView?.changePageStatus(PageStatus.NO_NET)
            }
            ResponseCode.ERROR_NO_DATA -> {
                iStatusView?.changePageStatus(PageStatus.NO_DATA)
            }
            ResponseCode.ERROR_OPERATION -> {
                iStatusView?.changePageStatus(PageStatus.ERROR)
                showShortToast(e.message)
            }
            ResponseCode.ERROR_CANCEL -> {
                iStatusView?.changeLoadStatus(LoadStatus.NORMAL)
            }
            else -> {
                iStatusView?.changePageStatus(PageStatus.ERROR)
                showShortToast(e.message)
            }
        }
    }

}