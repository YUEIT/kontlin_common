package cn.yue.base.mvp

import android.text.TextUtils
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.mvp.components.data.Loader
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.observer.BaseNetObserver
import cn.yue.base.net.wrapper.IListModel
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer

/**
 * Description :
 * Created by yue on 2022/3/8
 */
abstract class IListPresenter<P : IListModel<S>, S>(private val iView: IListView<S>) {

    private val loader: Loader = iView.getLoader()
    private var pageNt = "1"
    private var lastNt = "1"
    protected var dataList: MutableList<S> = ArrayList()

    //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    protected var total = 0

    protected fun initPageNt(): String {
        return "1"
    }

    protected fun initPageSize(): Int {
        return 20
    }

    fun loadData(isRefresh: Boolean) {
        if (isRefresh) {
            pageNt = initPageNt()
        }
        doLoadData(pageNt)
    }

    protected abstract fun doLoadData(nt: String?)

    inner class PageTransformer (private val pageObserver: BaseNetObserver<P> = PageObserver())
        : SingleTransformer<P, P> {

        override fun apply(upstream: Single<P>): SingleSource<P> {
            return upstream
                .compose(iView.getLifecycleProvider().toBindLifecycle())
                .doOnSubscribe { pageObserver.onStart() }
                .doOnSuccess { p -> pageObserver.onSuccess(p) }
                .doOnError { throwable -> pageObserver.onError(throwable) }
        }
    }

    inner class PageObserver : BaseNetObserver<P>() {
        private var isLoadingRefresh = false
        override fun onStart() {
            super.onStart()
            isLoadingRefresh = (loader.pageStatus == PageStatus.LOADING
                    || loader.loadStatus == LoadStatus.REFRESH)
        }

        override fun onSuccess(p: P) {
            if (isLoadingRefresh) {
                dataList.clear()
            }
            if (isLoadingRefresh && p.getCurrentPageTotal() == 0) {
                loadEmpty()
            } else {
                loadSuccess(p)
                if (p.getCurrentPageTotal() < p.getPageSize()
                    || (p.getTotal() > 0 && p.getTotal() <= dataList.size)
                    || (p.getCurrentPageTotal() == 0)
                    || (TextUtils.isEmpty(p.getPageNt()) && !initPageNt().matches(Regex("\\d+")))
                    || (p.getCurrentPageTotal() < initPageSize())) {
                    loadNoMore()
                }
            }
            if (isLoadingRefresh) {
                onRefreshComplete(p, null)
            }
        }

        override fun onException(e: ResultException) {
            loadFailed(e)
            if (isLoadingRefresh) {
                onRefreshComplete(null, e)
            }
        }

        override fun onCancel(e: ResultException) {
            super.onCancel(e)
            loadFailed(e)
        }

        fun loadSuccess(p: P) {
            iView.changePageStatus(PageStatus.NORMAL)
            iView.changeLoadStatus(LoadStatus.NORMAL)
            if (TextUtils.isEmpty(p.getPageNt())) {
                try {
                    pageNt = if (p.getPageNo() == 0) {
                        (pageNt.toInt() + 1).toString()
                    } else {
                        (p.getPageNo() + 1).toString()
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
                pageNt = p.getPageNt() ?: ""
            }
            if (p.getTotal() > 0) {
                total = p.getTotal()
            } else {
                total += p.getCurrentPageTotal()
            }
            lastNt = pageNt
            dataList.addAll(p.getList() ?: ArrayList())
            iView.setData(dataList)
        }

        fun loadFailed(e: ResultException) {
            pageNt = lastNt
            if (loader.isFirstLoad) {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        iView.changePageStatus(PageStatus.NO_NET)
                    }
                    ResponseCode.ERROR_NO_DATA -> {
                        iView.changePageStatus(PageStatus.NO_DATA)
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        iView.changePageStatus(PageStatus.ERROR)
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        iView.changePageStatus(PageStatus.ERROR)
                        ToastUtils.showShortToast(e.message)
                    }
                    else -> {
                        iView.changePageStatus(PageStatus.ERROR)
                        ToastUtils.showShortToast(e.message)
                    }
                }
            } else {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        iView.changeLoadStatus(LoadStatus.NO_NET)
                    }
                    ResponseCode.ERROR_NO_DATA -> {
                        iView.changeLoadStatus(LoadStatus.NORMAL)
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        iView.changeLoadStatus(LoadStatus.NORMAL)
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        iView.changeLoadStatus(LoadStatus.NORMAL)
                        ToastUtils.showShortToast(e.message)
                    }
                    else -> {
                        iView.changeLoadStatus(LoadStatus.NORMAL)
                        ToastUtils.showShortToast(e.message)
                    }
                }
            }
        }

        fun loadNoMore() {
            iView.changeLoadStatus(LoadStatus.END)
        }

        fun loadEmpty() {
            total = 0
            dataList.clear()
            iView.setData(dataList)
            if (showSuccessWithNoData()) {
                iView.changePageStatus(PageStatus.NORMAL)
                iView.changeLoadStatus(LoadStatus.NO_DATA)
            } else {
                iView.changePageStatus(PageStatus.NO_DATA)
            }
        }

        fun onRefreshComplete(p: P?, e: ResultException?) {}
    }

    protected fun showSuccessWithNoData(): Boolean {
        return false
    }

    inner class PageDelegateObserver(private val observer: BaseNetObserver<P>?,
                                     private val pageObserver: BaseNetObserver<P> = PageObserver()) : BaseNetObserver<P>() {

        override fun onStart() {
            super.onStart()
            pageObserver.onStart()
            observer?.onStart()
        }

        override fun onSuccess(p: P) {
            pageObserver.onSuccess(p)
            observer?.onSuccess(p)
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            pageObserver.onError(e)
            observer?.onError(e)
        }

        override fun onCancel(e: ResultException) {
            super.onCancel(e)
        }

        override fun onException(e: ResultException) {}
    }

}