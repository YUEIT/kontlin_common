package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.Loader
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.net.ResponseCode
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.observer.BaseNetObserver
import cn.yue.base.middle.net.wrapper.IListModel
import cn.yue.base.middle.view.PageStateView
import cn.yue.base.middle.view.refresh.IRefreshLayout
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer

/**
 * Description :
 * Created by yue on 2019/3/7
 */
abstract class BaseListFragment<P : IListModel<S>, S> : BaseFragment(), IBaseView, IPhotoView {

    var dataList: MutableList<S> = ArrayList()
    var total = 0 //当接口返回总数时，为返回数量；接口未返回数量，为统计数量； = 0
    private var pageNt: String = "1"
    private var lastNt: String = "1"
    private var adapter: CommonAdapter<S>? = null
    private lateinit var footer: BaseFooter
    private lateinit var refreshL: IRefreshLayout
    private lateinit var stateView: PageStateView
    private val loader = Loader()

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull_page
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = findViewById(R.id.stateView)
        stateView.setOnReloadListener{
            if (NetworkUtils.isAvailable()) {
                if (autoRefresh()) {
                    refresh()
                }
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        refreshL = (findViewById<View>(R.id.refreshL) as IRefreshLayout)
        refreshL.setEnabledRefresh(canPullDown())
        refreshL.setOnRefreshListener {
            refresh()
        }
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        footer = BaseFooter(mActivity)
        footer.setOnReloadListener {
            loadData()
        }
        val baseRV = findViewById<RecyclerView>(R.id.baseRV)
        refreshL.setTargetView(baseRV)
        initRecyclerView(baseRV)
        addOnScrollListener(baseRV)
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            if (autoRefresh()) {
                refresh()
            }
        } else {
            showStatusView(loader.setPageStatus(PageStatus.NO_NET))
        }
    }

    open fun autoRefresh(): Boolean {
        return true
    }

    open fun canPullDown(): Boolean {
        return true
    }

    open fun initRecyclerView(baseRV: RecyclerView) {
        baseRV.layoutManager = getLayoutManager()
        adapter = getAdapter()
        baseRV.adapter = adapter
        adapter!!.addFooterView(footer)
        footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
    }

    private fun addOnScrollListener(baseRV: RecyclerView) {
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dataList.isEmpty()) {
                    return
                }
                var isTheLast = false
                recyclerView.layoutManager?.let {
                    if (it is LinearLayoutManager) {
                        isTheLast = it.findLastVisibleItemPosition() >= dataList.size - 1
                    } else if (it is GridLayoutManager) {
                        isTheLast = it.findLastVisibleItemPosition() >= dataList.size - it.spanCount - 1
                    } else if (it is StaggeredGridLayoutManager) {
                        val lastSpan = it.findLastVisibleItemPositions(null)
                        for (position in lastSpan) {
                            if (position >= dataList.size - it.spanCount - 1) {
                                isTheLast = true
                                break
                            }
                        }
                    }
                }

                if (isTheLast
                    && loader.pageStatus === PageStatus.NORMAL
                    && loader.loadStatus === LoadStatus.NORMAL) {
                    footer.showStatusView(loader.setLoadStatus(LoadStatus.LOADING))
                    loadData()
                }
            }
        })
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(mActivity)
    }

    open fun getAdapter(): CommonAdapter<S> {
        return adapter ?: object : CommonAdapter<S>(mActivity, ArrayList()) {
            override fun getViewType(position: Int): Int {
                return getItemType(position)
            }

            override fun getLayoutIdByType(viewType: Int): Int {
                return getItemLayoutId(viewType)
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: S) {
                bindItemData(holder, position, itemData)
            }
        }
    }

    /**
     * 返回viewType，可选实现
     */
    open fun getItemType(position: Int): Int {
        return 0
    }

    /**
     * 根据viewType 返回对应的layout
     */
    abstract fun getItemLayoutId(viewType: Int): Int

    /**
     * 布局
     */
    abstract fun bindItemData(holder: CommonViewHolder, position: Int, itemData: S)

    fun getFooter(): BaseFooter {
        return footer
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }

    /**
     * 刷新 选择是否页面加载动画
     */
    @JvmOverloads
    fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.pageStatus === PageStatus.LOADING
                || loader.loadStatus === LoadStatus.LOADING
                || loader.loadStatus === LoadStatus.REFRESH) {
            return
        }
        if (isPageRefreshAnim) {
            showStatusView(loader.setPageStatus(PageStatus.LOADING))
        } else {
            loader.setLoadStatus(LoadStatus.REFRESH)
            refreshL.startRefresh()
        }
        pageNt = initPageNt()
        loadData()
    }

    open fun initPageNt(): String {
        return "1"
    }

    open fun initPageSize(): Int {
        return 20
    }

    private fun loadData() {
        doLoadData(pageNt)
    }

    abstract fun doLoadData(nt: String)

    inner class PageTransformer : SingleTransformer<P, P> {
        override fun apply(upstream: Single<P>): SingleSource<P> {
            val pageObserver = getPageObserver()
            return upstream
                .compose(getLifecycleProvider().toBindLifecycle())
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
            refreshL.finishRefreshing()
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
            refreshL.finishRefreshing()
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
            showStatusView(loader.setPageStatus(PageStatus.NORMAL))
            footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
            if (TextUtils.isEmpty(p.getPageNt())) {
                try {
                    pageNt = if (p.getPageNo() == 0) {
                        (Integer.valueOf(pageNt!!) + 1).toString()
                    } else {
                        (p.getPageNo() + 1).toString()
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
                pageNt = p.getPageNt()!!
            }
            if (p.getTotal() > 0) {
                total = p.getTotal()
            } else {
                total += p.getCurrentPageTotal()
            }
            lastNt = pageNt
            dataList.addAll(p.getList() ?: ArrayList())
            notifyDataSetChanged()
        }

        open fun loadFailed(e: ResultException) {
            pageNt = lastNt
            if (loader.isFirstLoad) {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        showStatusView(loader.setPageStatus(PageStatus.NO_NET))
                    }
                    ResponseCode.ERROR_NO_DATA -> {
                        showStatusView(loader.setPageStatus(PageStatus.NO_DATA))
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        showStatusView(loader.setPageStatus(PageStatus.ERROR))
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        showStatusView(loader.setPageStatus(PageStatus.ERROR))
                        showShortToast(e.message)
                    }
                    else -> {
                        showStatusView(loader.setPageStatus(PageStatus.ERROR))
                        showShortToast(e.message)
                    }
                }
            } else {
                when(e.code) {
                    ResponseCode.ERROR_NO_NET -> {
                        footer.showStatusView(loader.setLoadStatus(LoadStatus.NO_NET))
                    }
                    ResponseCode.ERROR_NO_DATA -> {
                        footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
                    }
                    ResponseCode.ERROR_CANCEL -> {
                        footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
                    }
                    ResponseCode.ERROR_OPERATION -> {
                        footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
                        showShortToast(e.message)
                    }
                    else -> {
                        footer.showStatusView(loader.setLoadStatus(LoadStatus.NORMAL))
                        showShortToast(e.message)
                    }
                }
            }
        }

        open fun loadNoMore() {
            footer.showStatusView(loader.setLoadStatus(LoadStatus.END))
        }

        open fun loadEmpty() {
            total = 0
            dataList.clear()
            notifyDataSetChanged()
            if (showSuccessWithNoData()) {
                showStatusView(loader.setPageStatus(PageStatus.NORMAL))
                footer.showStatusView(loader.setLoadStatus(LoadStatus.NO_DATA))
            } else {
                showStatusView(loader.setPageStatus(PageStatus.NO_DATA))
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

    open fun notifyDataSetChanged() {
        adapter?.setList(dataList)
    }

    fun notifyItemChangedReally() {
        adapter?.notifyDataSetChanged()
    }

    override fun showStatusView(status: PageStatus?) {
        if (loader.isFirstLoad) {
            stateView.show(status)
        } else {
            stateView.show(PageStatus.NORMAL)
        }
        if (status === PageStatus.NORMAL) {
            loader.isFirstLoad = false
        }
    }

    private var waitDialog: WaitDialog? = null
    override fun showWaitDialog(title: String) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        waitDialog?.show(title, true, null)
    }

    override fun dismissWaitDialog() {
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog?.cancel()
        }
    }

    private var photoHelper: PhotoHelper? = null
    fun getPhotoHelper(): PhotoHelper {
        if (photoHelper == null) {
            photoHelper = PhotoHelper(mActivity, this)
        }
        return photoHelper!!
    }

    override fun selectImageResult(selectList: List<String>?) {}
    override fun cropImageResult(image: String?) {}
    override fun uploadImageResult(serverList: List<String>?) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoHelper != null) {
            photoHelper!!.onActivityResult(requestCode, resultCode, data)
        }
    }
}