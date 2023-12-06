package cn.yue.base.mvvm

import cn.yue.base.mvvm.data.MutableListLiveData
import cn.yue.base.net.ResponseCode
import cn.yue.base.net.ResultException
import cn.yue.base.net.observer.BaseNetObserver
import cn.yue.base.net.observer.WrapperObserver
import cn.yue.base.net.wrapper.IListModel
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.core.SingleTransformer
import io.reactivex.rxjava3.disposables.Disposable

abstract class ListViewModel<P : IListModel<S>, S> : BaseViewModel() {

    private var pageNt: Int = 1
    private var lastNt: Int = 1
    //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    var total = 0
    var dataLiveData = MutableListLiveData<S>()
    protected val dataList = ArrayList<S>()

    open fun initPageNt(): Int {
        return 1
    }

    open fun initPageSize(): Int {
        return 20
    }

    /**
     * 刷新
     */
    open fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.isLoading()) {
            return
        }
        if (isPageRefreshAnim) {
            loader.pageStatus = PageStatus.REFRESH
        } else {
            loader.loadStatus = LoadStatus.REFRESH
        }
        pageNt = initPageNt()
        loadData()
    }

    /**
     * 上拉刷新时
     */
    fun loadRefresh() {
        if (loader.isLoading()) {
            return
        }
        loader.loadStatus = LoadStatus.REFRESH
        pageNt = initPageNt()
        loadData()
    }

    /**
     * 下拉加载时
     */
    fun loadMoreData() {
        if (loader.pageStatus === PageStatus.NORMAL && loader.loadStatus === LoadStatus.NORMAL) {
            loader.loadStatus = LoadStatus.LOAD_MORE
            loadData()
        }
    }

    private fun loadData() {
        doLoadData(pageNt)
    }

    abstract fun doLoadData(nt: Int)

    fun Single<P>.defaultSubscribe() {
        this.compose(PageTransformer())
            .subscribe(WrapperObserver())
    }

    inner class PageTransformer : SingleTransformer<P, P> {
        override fun apply(upstream: Single<P>): SingleSource<P> {
            val pageObserver = getPageObserver()
            return upstream
                .compose(toBindLifecycle())
                .doOnSubscribe { pageObserver.onSubscribe(it) }
                .doOnSuccess { pageObserver.onSuccess(it) }
                .doOnError { pageObserver.onError(it) }
        }
    }

    inner class PageDelegateObserver(val observer: WrapperObserver<P>? = null)
        : WrapperObserver<P>() {

        private val pageObserver = getPageObserver()

        override fun onSubscribe(d: Disposable) {
            super.onSubscribe(d)
            pageObserver.onSubscribe(d)
            observer?.onSubscribe(d)
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            pageObserver.onError(e)
            observer?.onError(e)
        }
        
        override fun onSuccess(t: P) {
            pageObserver.onSuccess(t)
            observer?.onSuccess(t)
        }
    }

    open inner class PageObserver: BaseNetObserver<P>() {

        private var isLoadingRefresh = false

        override fun onSubscribe(d: Disposable) {
            isLoadingRefresh = (loader.pageStatus == PageStatus.REFRESH
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

        open fun loadSuccess(p: P) {
            loader.pageStatus = PageStatus.NORMAL
            loader.loadStatus = LoadStatus.NORMAL
            try {
                pageNt = if (p.getPageNo() == 0) {
                    (pageNt + 1)
                } else {
                    (p.getPageNo() + 1)
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            if (p.getTotal() > 0) {
                total = p.getTotal()
            } else {
                total += p.getCurrentPageTotal()
            }
            lastNt = pageNt
            dataList.addAll(p.getList() ?: ArrayList())
            dataLiveData.setValue(dataList)
        }

        open fun loadFailed(e: ResultException) {
            pageNt = lastNt
            if (loader.isFirstLoad) {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        loader.pageStatus = PageStatus.NO_NET
                    }
                    else -> {
                        loader.pageStatus = PageStatus.ERROR
                        showShortToast(e.message)
                    }
                }
            } else {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        loader.loadStatus = LoadStatus.NO_NET
                    }
                    else -> {
                        loader.loadStatus = LoadStatus.NORMAL
                        showShortToast(e.message)
                    }
                }
            }
        }

        open fun loadNoMore() {
            loader.loadStatus = LoadStatus.END
        }

        open fun loadEmpty() {
            resetNoData()
            if (showSuccessWithNoData()) {
                loader.pageStatus = PageStatus.NORMAL
                loader.loadStatus = LoadStatus.NO_DATA
            } else {
                loader.pageStatus = PageStatus.NO_DATA
                loader.loadStatus = LoadStatus.NORMAL
            }
        }

        open fun onRefreshComplete(p: P?, e: ResultException?) {}

    }

    /**
     * 没有数据时，显示正常内容页面状态
     */
    open fun showSuccessWithNoData(): Boolean {
        return false
    }

    open fun getPageObserver(): BaseNetObserver<P> {
        return PageObserver()
    }

    fun resetNoData() {
        total = 0
        dataList.clear()
        dataLiveData.setValue(dataList)
    }

    fun scrollToLoadMore(lastPosition: Int, spanCount: Int) {
        if (dataList.size <= 0) {
            return
        }
        if (lastPosition >= dataList.size - spanCount - 1) {
            loadMoreData()
        }
    }
}
