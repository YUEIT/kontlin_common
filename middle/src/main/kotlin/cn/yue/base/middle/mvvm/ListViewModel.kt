package cn.yue.base.middle.mvvm

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.viewModelScope
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
import cn.yue.base.middle.net.coroutine.request
import cn.yue.base.middle.net.observer.BaseNetObserver
import cn.yue.base.middle.net.wrapper.BaseListBean
import io.reactivex.Single
import java.util.*

abstract class ListViewModel<P : BaseListBean<S>, S>(application: Application) : BaseViewModel(application) {
    private var pageNt: String = "1"
    private var lastNt: String = "1"
    var total = 0 //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    @JvmField
    var dataLiveData = MutableListLiveData<S>()
    private val dataList = ArrayList<S>()
    open fun initPageNt(): String {
        return "1"
    }
    /**
     * 刷新
     */
    @JvmOverloads
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

    open fun getRequestSingle(nt: String): Single<P>? = null

    open suspend fun getRequestScope(nt: String): P? = null

    fun loadData() {
        val observer = object : BaseNetObserver<P>() {
            private var isLoadingRefresh = false
            override fun onStart() {
                super.onStart()
                isLoadingRefresh = (loader.pageStatus === PageStatus.LOADING
                        || loader.loadStatus === LoadStatus.REFRESH)
            }

            override fun onSuccess(p: P) {
                if (isLoadingRefresh) {
                    dataList.clear()
                }
                if (isLoadingRefresh && p.getCurrentPageTotal() == 0) {
                    loadEmpty()
                } else {
                    loadSuccess(p)
                    if (p.getCurrentPageTotal() < p.getPageSize()) {
                        loadNoMore()
                    } else if (p.getTotal() > 0 && p.getTotal() <= dataList.size) {
                        loadNoMore()
                    } else if (p.getCurrentPageTotal() == 0) {
                        loadNoMore()
                    } else if (TextUtils.isEmpty(p.getPageNt()) && !initPageNt().matches(Regex("\\d+"))) {
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
        }
        val requestSingle = getRequestSingle(pageNt)
        if (requestSingle != null) {
            requestSingle
//                    .delay(1000, TimeUnit.MILLISECONDS)
                    .compose(this.toBindLifecycle())
                    .subscribe(observer)
        } else {
            viewModelScope.request({
                getRequestScope(pageNt)!!
            }, observer)
        }
    }

    open fun loadSuccess(p: P) {
        loader.pageStatus = PageStatus.NORMAL
        loader.loadStatus = LoadStatus.NORMAL
        if (TextUtils.isEmpty(p.getPageNt())) {
            try {
                pageNt = if (p.getPageNo() == 0) {
                    Integer.valueOf(pageNt + 1).toString()
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
        dataList.addAll((if (p.getList() == null) ArrayList() else p.getList())!!)
        dataLiveData.postValue(dataList)
    }

    open fun loadFailed(e: ResultException) {
        pageNt = lastNt
        if (loader.isFirstLoad) {
            when {
                ResponseCode.ERROR_NO_NET == e.code -> {
                    loader.pageStatus = PageStatus.NO_NET
                }
                ResponseCode.ERROR_NO_DATA == e.code -> {
                    loader.pageStatus = PageStatus.NO_DATA
                }
                ResponseCode.ERROR_CANCEL == e.code -> {
                    loader.pageStatus = PageStatus.NO_NET
                    showShortToast(e.message)
                }
                ResponseCode.ERROR_OPERATION == e.code -> {
                    loader.pageStatus = PageStatus.ERROR
                    showShortToast(e.message)
                }
                else -> {
                    loader.pageStatus = PageStatus.ERROR
                    showShortToast(e.message)
                }
            }
        } else {
            when {
                ResponseCode.ERROR_NO_NET == e.code -> {
                    loader.loadStatus = LoadStatus.NO_NET
                }
                ResponseCode.ERROR_NO_DATA == e.code -> {
                    loader.loadStatus = LoadStatus.NO_DATA
                }
                ResponseCode.ERROR_CANCEL == e.code -> {
                    loader.loadStatus = LoadStatus.NORMAL
                    showShortToast(e.message)
                }
                ResponseCode.ERROR_OPERATION == e.code -> {
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

    open fun showSuccessWithNoData(): Boolean {
        return false
    }

    open fun onRefreshComplete(p: P?, e: ResultException?) {}

    fun hasLoad(layoutManager: RecyclerView.LayoutManager?) {
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