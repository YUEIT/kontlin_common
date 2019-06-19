package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvp.IWaitView
import cn.yue.base.middle.mvp.PageStatus
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.net.NetworkConfig
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.observer.BaseNetSingleObserver
import cn.yue.base.middle.net.wrapper.BaseListBean
import cn.yue.base.middle.view.PageHintView
import io.reactivex.Single
import kotlinx.android.synthetic.main.fragment_base_pull_page.*
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/19
 */
abstract class BasePullListFragment<P : BaseListBean<S>, S> : BaseFragment(), IStatusView, IWaitView, IBaseView, IPhotoView {

    private var pageNt: String? = "1"
    private var lastNt: String? = "1"
    protected var dataList: MutableList<S> = ArrayList()
    protected var total: Int = 0    //当接口返回总数时，为返回数量；接口未返回数量，为统计数量；
    private var adapter: CommonAdapter<S>? = null
    private var footer: BasePullFooter? = null
    private var status = PageStatus.STATUS_NORMAL
    private var isFirstLoading = true
    private var photoHelper: PhotoHelper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull_page
    }

    override fun initView(savedInstanceState: Bundle?) {
        hintView.setOnReloadListener(object : PageHintView.OnReloadListener() {
            override fun onReload() {
                if (NetworkUtils.isConnected) {
                    mActivity.recreateFragment(this@BasePullListFragment.javaClass.name)
                } else {
                    ToastUtils.showShortToast("网络不给力，请检查您的网络设置~")
                }
            }

            override fun onRefresh() {
                if (NetworkUtils.isConnected) {
                    if (autoRefresh()) {
                        refreshWithLoading()
                    }
                } else {
                    showStatusView(PageStatus.STATUS_ERROR_NET)
                }
            }
        })

        refreshL.init()
        refreshL.isEnabled = canPullDown()
        refreshL.setOnRefreshListener { refresh() }
        if (canPullDown()) {
            hintView!!.setRefreshTarget((refreshL as ViewGroup?)!!)
        }
        footer = getFooter()
        if (footer != null) {
            footer!!.setOnReloadListener(object : BasePullFooter.OnReloadListener {
                override fun onReload() {
                    loadData()
                }
            })
        }
        refreshL.setTargetView(baseRV)
        initRecyclerView(baseRV)
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dataList.isEmpty()) {
                    return
                }
                var isTheLast = false
                if (recyclerView!!.layoutManager is LinearLayoutManager) {
                    isTheLast = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() >= dataList.size - 1
                } else if (recyclerView.layoutManager is GridLayoutManager) {
                    isTheLast = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition() >= dataList.size - (recyclerView.layoutManager as GridLayoutManager).spanCount - 1
                } else if (recyclerView.layoutManager is StaggeredGridLayoutManager) {
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val lastSpan = layoutManager.findLastVisibleItemPositions(null)
                    for (position in lastSpan) {
                        if (position >= dataList.size - layoutManager.spanCount - 1) {
                            isTheLast = true
                            break
                        }
                    }
                }
                if (isTheLast && status === PageStatus.STATUS_SUCCESS) {
                    status = PageStatus.STATUS_LOADING_ADD
                    footer!!.showStatusView(status)
                    loadData()
                }
            }
        })
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isConnected) {
            if (autoRefresh()) {
                refreshWithLoading()
            }
        } else {
            showStatusView(PageStatus.STATUS_ERROR_NET)
        }
    }

    protected fun autoRefresh(): Boolean {
        return true
    }

    protected fun canPullDown(): Boolean {
        return true
    }

    protected fun initRecyclerView(baseRV: RecyclerView) {
        baseRV.layoutManager = getLayoutManager()
        baseRV.adapter = getAdapter()
        adapter!!.addFooterView(footer)
        footer!!.showStatusView(status)
    }

    protected fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(mActivity)
    }

    protected fun getAdapter(): CommonAdapter<S> {
        return if (adapter != null) {
            adapter!!
        } else object : CommonAdapter<S>(mActivity, ArrayList()) {

            override fun getViewType(position: Int): Int {
                return getItemType(position)
            }

            override fun getLayoutIdByType(viewType: Int): Int {
                return getItemLayoutId(viewType)
            }

            override fun bindData(holder: CommonViewHolder<S>, position: Int, s: S) {
                bindItemData(holder, position, s)
            }
        }
    }

    /**
     * 返回viewType，可选实现
     */
    protected fun getItemType(position: Int): Int {
        return 0
    }

    /**
     * 根据viewType 返回对应的layout
     */
    protected abstract fun getItemLayoutId(viewType: Int): Int

    /**
     * 布局
     */
    protected abstract fun bindItemData(holder: CommonViewHolder<S>, position: Int, s: S)

    protected fun getFooter(): BasePullFooter {
        if (footer == null) {
            footer = BasePullFooter(mActivity)
        }
        return footer!!
    }

    /**
     * 刷新 loading动画
     */
    fun refreshWithLoading() {
        baseRV!!.visibility = View.GONE
        showPageHintLoading()
        refresh(false)
    }

    /**
     * 刷新 swipe动画
     */
    fun refresh() {
        refresh(true)
    }

    /**
     * 刷新 选择是否有swipe动画
     * @param hasRefreshAnim
     */
    fun refresh(hasRefreshAnim: Boolean) {
        if (status === PageStatus.STATUS_LOADING_ADD || status === PageStatus.STATUS_LOADING_REFRESH) {
            return
        }
        status = PageStatus.STATUS_LOADING_REFRESH
        if (hasRefreshAnim) {
            refreshL!!.startRefresh()
        }
        pageNt = initPageNt()
        loadData()
    }

    protected fun initPageNt(): String {
        return "1"
    }

    protected abstract fun getRequestSingle(nt: String?): Single<P>?

    private fun loadData() {
        if (getRequestSingle(pageNt) == null) {
            return
        }
        getRequestSingle(pageNt)!!
                //                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(this.toBindLifecycle())
                .subscribe(object : BaseNetSingleObserver<P>() {

                    private var isLoadingRefresh = false
                    override fun onStart() {
                        super.onStart()
                        if (status === PageStatus.STATUS_LOADING_REFRESH) {
                            isLoadingRefresh = true
                        } else {
                            isLoadingRefresh = false
                        }
                    }

                    override fun onSuccess(p: P) {
                        refreshL!!.finishRefreshing()
                        if (isLoadingRefresh) {
                            dataList.clear()
                        }
                        if (isLoadingRefresh && p.getCurrentPageTotal() === 0) {
                            loadEmpty()
                        } else {
                            loadSuccess(p)
                            if (p.getCurrentPageTotal() < p.getPageSize()) {
                                loadNoMore()
                            } else if (p.getTotal() > 0 && p.getTotal() <= dataList.size) {
                                loadNoMore()
                            } else if (p.getCurrentPageTotal() === 0) {
                                loadNoMore()
                            } else if (TextUtils.isEmpty(p.getPageNt()) && !initPageNt().matches("\\d+".toRegex())) {
                                loadNoMore()
                            }
                        }
                        if (isLoadingRefresh) {
                            onRefreshComplete(p, null)
                        }
                    }

                    override fun onException(e: ResultException) {
                        refreshL!!.finishRefreshing()
                        loadFailed(e)
                        if (isLoadingRefresh) {
                            onRefreshComplete(null, e)
                        }
                    }

                    override fun onCancel(e: ResultException) {
                        super.onCancel(e)
                        loadFailed(e)
                    }
                })
    }

    protected fun loadSuccess(p: P) {
        showStatusView(PageStatus.STATUS_SUCCESS)
        footer!!.showStatusView(status)
        if (TextUtils.isEmpty(p.getPageNt())) {
            try {
                if (p.getPageNo() == 0) {
                    pageNt = (Integer.valueOf(pageNt) + 1).toString()
                } else {
                    pageNt = (p.getPageNo() + 1).toString()
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }

        } else {
            pageNt = p.getPageNt()
        }
        if (p.getTotal() > 0) {
            total = p.getTotal()
        } else {
            total += p.getCurrentPageTotal()
        }
        lastNt = pageNt
        dataList.addAll(if (p.getList() == null) ArrayList<S>() else p.getList()!!)
        notifyDataSetChanged()
    }

    protected fun loadFailed(e: ResultException) {
        pageNt = lastNt
        if (NetworkConfig.ERROR_NO_NET == e.code) {
            if (this.status === PageStatus.STATUS_LOADING_REFRESH) {
                showStatusView(PageStatus.STATUS_ERROR_NET)
            } else if (this.status === PageStatus.STATUS_LOADING_ADD) {
                footer!!.showStatusView(status)
            }
        } else if (NetworkConfig.ERROR_NO_DATA == e.code) {
            showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
        } else if (NetworkConfig.ERROR_CANCEL == e.code) {
            showStatusView(PageStatus.STATUS_SUCCESS)
            footer!!.showStatusView(PageStatus.STATUS_SUCCESS)
        } else if (NetworkConfig.ERROR_OPERATION == e.code) {
            showStatusView(PageStatus.STATUS_ERROR_OPERATION)
            ToastUtils.showShortToast(e.message)
        } else {
            showStatusView(PageStatus.STATUS_ERROR_SERVER)
            ToastUtils.showShortToast(e.message)
        }
    }

    protected fun loadNoMore() {
        showStatusView(PageStatus.STATUS_END)
    }

    protected fun loadEmpty() {
        total = 0
        dataList.clear()
        notifyDataSetChanged()
        if (showSuccessWithNoData()) {
            showStatusView(PageStatus.STATUS_SUCCESS)
            footer!!.showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
        } else {
            showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
        }
    }

    protected fun showSuccessWithNoData(): Boolean {
        return false
    }

    protected fun onRefreshComplete(p: P?, e: ResultException?) {

    }

    protected fun notifyDataSetChanged() {
        if (adapter != null) {
            adapter!!.setList(dataList)
        }
    }

    protected fun notifyItemChangedReally() {
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_LOADING_REFRESH -> showPageHintLoading()
            PageStatus.STATUS_SUCCESS -> showPageHintSuccess()
            PageStatus.STATUS_END -> {
                showPageHintSuccess()
                footer!!.showStatusView(status)
            }
            PageStatus.STATUS_ERROR_NET -> showPageHintErrorNet()
            PageStatus.STATUS_ERROR_NO_DATA -> showPageHintErrorNoData()
            PageStatus.STATUS_ERROR_OPERATION -> showPageHintErrorOperation()
            PageStatus.STATUS_ERROR_SERVER -> showPageHintErrorServer()
        }
    }

    private fun showPageHintLoading() {
        if (hintView != null) {
            hintView!!.showLoading()
        }
    }

    private fun showPageHintSuccess() {
        if (baseRV != null) {
            baseRV!!.visibility = View.VISIBLE
        }
        if (hintView != null) {
            hintView!!.showSuccess()
        }
        isFirstLoading = false
    }

    private fun showPageHintErrorNet() {
        if (hintView != null) {
            if (isFirstLoading) {
                hintView!!.showErrorNet()
            } else {
                ToastUtils.showShortToast("网络不给力，请检查您的网络设置~")
            }
        }
    }

    private fun showPageHintErrorNoData() {
        if (hintView != null) {
            hintView!!.showErrorNoData()
        }
    }

    private fun showPageHintErrorOperation() {
        if (hintView != null && isFirstLoading) {
            hintView!!.showErrorOperation()
        }
    }

    private fun showPageHintErrorServer() {
        if (hintView != null && isFirstLoading) {
            hintView!!.showErrorOperation()
        }
    }

    private var waitDialog: WaitDialog? = null
    override fun showWaitDialog(title: String) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        waitDialog!!.show(title, true, null)
    }

    override fun dismissWaitDialog() {
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog!!.cancel()
        }
    }

    fun getPhotoHelper(): PhotoHelper {
        if (photoHelper == null) {
            photoHelper = PhotoHelper(mActivity, this)
        }
        return photoHelper!!
    }

    override fun selectImageResult(selectList: MutableList<String>) {

    }

    override fun cropImageResult(image: String) {

    }

    override fun uploadImageResult(serverList: MutableList<String>) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoHelper != null) {
            photoHelper!!.onActivityResult(requestCode, resultCode, data)
        }
    }

}