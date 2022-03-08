package cn.yue.base.middle.mvvm.components

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.components.BaseFooter
import cn.yue.base.middle.mvvm.ListViewModel
import cn.yue.base.middle.view.PageStateView
import cn.yue.base.middle.view.load.LoadStatus
import cn.yue.base.middle.view.load.PageStatus
import cn.yue.base.middle.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseListVMFragment<VM : ListViewModel<*, S>, S> : BaseVMFragment<VM>() {
    private var adapter: CommonAdapter<S>? = null
    private lateinit var footer: BaseFooter
    private lateinit var refreshL: IRefreshLayout
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull_page
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = findViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                if (autoRefresh()) {
                    viewModel.refresh()
                }
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        refreshL = findViewById<View>(R.id.refreshL) as IRefreshLayout
        refreshL.setEnabledRefresh(canPullDown())
        refreshL.setOnRefreshListener {
            viewModel.refresh()
        }
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        footer = initFooter()
        footer.setOnReloadListener {
            viewModel.loadData()
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
                viewModel.refresh()
            }
        } else {
            viewModel.loader.pageStatus = PageStatus.NO_NET
        }
        viewModel.dataLiveData.observe(this, Observer { list ->
            adapter?.apply {
                setData(list)
            }
        })
        viewModel.loader.observePage(this, Observer { pageStatus ->
            showStatusView(pageStatus)
        })
        viewModel.loader.observeLoad(this, Observer { loadStatus ->
            if (loadStatus == LoadStatus.REFRESH) {
                refreshL.startRefresh()
            } else {
                refreshL.finishRefreshing()
            }
            footer.showStatusView(loadStatus)
        })
    }

    open fun autoRefresh(): Boolean {
        return true
    }

    open fun canPullDown(): Boolean {
        return true
    }

    open fun initRecyclerView(baseRV: RecyclerView) {
        baseRV.layoutManager = getLayoutManager()
        baseRV.adapter = initAdapter().also { adapter = it }
        adapter?.apply {
            addFooterView(footer)
        }
    }

    private fun addOnScrollListener(baseRV: RecyclerView) {
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.scrollToLoad(recyclerView.layoutManager)
            }
        })
    }

    abstract fun initAdapter(): CommonAdapter<S>?

    open fun getAdapter(): CommonAdapter<S>? {
        return adapter
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(mActivity)

    abstract fun setData(list: MutableList<S>)

    open fun initFooter(): BaseFooter {
        return BaseFooter(mActivity)
    }

    fun getFooter(): BaseFooter {
        return footer
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }

    private fun showStatusView(status: PageStatus?) {
        if (viewModel.loader.isFirstLoad) {
            stateView.show(status)
        } else {
            stateView.show(PageStatus.NORMAL)
        }
        if (status == PageStatus.NORMAL) {
            viewModel.loader.isFirstLoad = false
        }
    }
}