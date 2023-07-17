package cn.yue.base.mvp.components

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.yue.base.common.R
import cn.yue.base.activity.BaseFragment
import cn.yue.base.mvp.IListView
import cn.yue.base.mvp.components.data.Loader
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout
import cn.yue.base.widget.dialog.WaitDialog
import cn.yue.base.widget.recyclerview.CommonAdapter

/**
 * Description :
 * Created by yue on 2019/3/7
 */
abstract class BaseListFragment<S> : BaseFragment(), IListView<S> {

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
                val dataList = adapter?.getList()
                if (dataList.isNullOrEmpty()) {
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

    abstract fun getAdapter(): CommonAdapter<S>

    fun getFooter(): BaseFooter {
        return footer
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }

    override fun getLoader(): Loader {
        return loader
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
            changePageStatus(PageStatus.LOADING)
        } else {
            changeLoadStatus(LoadStatus.REFRESH)
        }
        loadData(true)
    }

    abstract fun loadData(isRefresh: Boolean = false)

    override fun setData(list: List<S>) {
        adapter?.setList(list)
    }

    private fun showStatusView(status: PageStatus) {
        if (loader.isFirstLoad) {
            stateView.show(status)
        } else {
            stateView.show(PageStatus.NORMAL)
        }
        if (status === PageStatus.NORMAL) {
            loader.isFirstLoad = false
        }
    }

    override fun changePageStatus(status: PageStatus) {
        showStatusView(loader.setPageStatus(status))
        refreshL.finishRefreshing()
    }

    override fun changeLoadStatus(status: LoadStatus) {
        loader.setLoadStatus(status)
        if (status === LoadStatus.REFRESH) {
            refreshL.startRefresh()
        } else {
            refreshL.finishRefreshing()
            footer.showStatusView(status)
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
    
}