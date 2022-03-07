package cn.yue.base.middle.mvvm

import android.app.Application
import android.text.TextUtils
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvvm.data.MutableListLiveData
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.observer.BaseNetObserver
import cn.yue.base.middle.net.wrapper.IListModel
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

abstract class ListViewModel<P : IListModel<S>, S>(application: Application) : BaseViewModel(application) {

    private var pageNt: String = "1"
    private var lastNt: String = "1"
    var total = 0 //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    var dataLiveData = MutableListLiveData<S>()
    private val dataList = ArrayList<S>()

    open fun initPageNt(): String {
        return "1"
    }

    open fun initPageSize(): Int {
        return 20
    }

    /**
     * 刷新
     */
    open fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.loadStatus == LoadStatus.LOADING
                || loader.loadStatus == LoadStatus.REFRESH
                || loader.pageStatus == PageStatus.LOADING) {
            return
        }
        if (isPageRefreshAnim) {
            loader.pageStatus = PageStatus.LOADING
        } else {
            loader.loadStatus = LoadStatus.REFRESH
        }
        pageNt = initPageNt()
        loadData()
    }

    fun loadRefresh() {
        refresh(false)
    }

    fun loadData() {
        doLoadData(pageNt)
    }

    abstract fun doLoadData(nt: String)

    inner class PageTransformer : SingleTransformer<P, P> {
        override fun apply(upstream: Single<P>): SingleSource<P> {
            val pageObserver = getPageObserver()
            return upstream
                .compose(toBindLifecycle())
                .doOnSubscribe { pageObserver.onStart() }
                .doOnSuccess { pageObserver.onSuccess(it) }
                .doOnError { pageObserver.onError(it) }
        }
    }

    inner class PageDelegateObserver(val observer: BaseNetObserver<P>? = null)
        : BaseNetObserver<P>() {

        private val pageObserver = getPageObserver()

        override fun onStart() {
            super.onStart()
            pageObserver.onStart()
            observer?.onStart()
        }

        override fun onCancel(e: ResultException) {
            super.onCancel(e)
        }

        override fun onError(e: Throwable) {
            super.onError(e)
            pageObserver.onError(e)
            observer?.onError(e)
        }

        override fun onException(e: ResultException) {}

        override fun onSuccess(t: P) {
            pageObserver.onSuccess(t)
            observer?.onSuccess(t)
        }
    }

    open inner class PageObserver: BaseNetObserver<P>() {

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

        open fun loadSuccess(p: P) {
            loader.pageStatus = PageStatus.NORMAL
            loader.loadStatus = LoadStatus.NORMAL
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
            dataLiveData.postValue(dataList)
        }

        open fun loadFailed(e: ResultException) {
            pageNt = lastNt
            if (loader.isFirstLoad) {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        loader.pageStatus = PageStatus.NO_NET
                    }
                    ResponseCode.ERROR_NO_DATA -> {
                        loader.pageStatus = PageStatus.NO_DATA
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        loader.pageStatus = PageStatus.NO_NET
                        showShortToast(e.message)
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        loader.pageStatus = PageStatus.ERROR
                        showShortToast(e.message)
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
                    ResponseCode.ERROR_NO_DATA -> {
                        loader.loadStatus = LoadStatus.NO_DATA
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        loader.loadStatus = LoadStatus.NORMAL
                        showShortToast(e.message)
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        loader.loadStatus = LoadStatus.NORMAL
                        showShortToast(e.message)
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
            total = 0
            dataList.clear()
            dataLiveData.postValue(dataList)
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

    open fun showSuccessWithNoData(): Boolean {
        return false
    }

    open fun getPageObserver(): BaseNetObserver<P> {
        return PageObserver()
    }

    fun scrollToLoad(layoutManager: RecyclerView.LayoutManager?) {
        if (dataList.size <= 0) {
            return
        }
        var isTheLast = false
        if (layoutManager is GridLayoutManager) {
            isTheLast = layoutManager.findLastVisibleItemPosition() >= dataList.size - layoutManager.spanCount - 1
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val lastSpan = layoutManager.findLastVisibleItemPositions(null)
            for (position in lastSpan) {
                if (position >= dataList.size - layoutManager.spanCount - 1) {
                    isTheLast = true
                    break
                }
            }
        } else if (layoutManager is LinearLayoutManager) {
            isTheLast = layoutManager.findLastVisibleItemPosition() >= dataList.size - 1
        }
        if (isTheLast && loader.pageStatus === PageStatus.NORMAL && loader.loadStatus === LoadStatus.NORMAL) {
            loader.loadStatus = LoadStatus.LOADING
            loadData()
        }
    }
}
