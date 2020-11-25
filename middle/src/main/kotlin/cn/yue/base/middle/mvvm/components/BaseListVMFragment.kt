package cn.yue.base.middle.mvvm.components

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.middle.R
import cn.yue.base.middle.components.BaseFooter
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvvm.ListViewModel
import cn.yue.base.middle.view.PageHintView
import cn.yue.base.middle.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseListVMFragment<VM : ListViewModel<*, *>> : BaseVMFragment<VM>(), IStatusView {
    private var adapter: CommonAdapter<*>? = null
    private lateinit var footer: BaseFooter
    private lateinit var refreshL: IRefreshLayout
    private lateinit var baseRV: RecyclerView
    private lateinit var hintView: PageHintView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull_page
    }

    override fun initView(savedInstanceState: Bundle?) {
        hintView = findViewById(R.id.hintView)
        hintView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                if (autoRefresh()) {
                    viewModel.refresh()
                }
            } else {
                showShortToast("网络不给力，请检查您的网络设置~")
            }
        }
        refreshL = findViewById<View>(R.id.refreshL) as IRefreshLayout
        refreshL.setEnabledRefresh(canPullDown())
        refreshL.setOnRefreshListener {
            viewModel.refresh()
        }
        if (canPullDown()) {
            hintView.setRefreshTarget(refreshL)
        }
        footer = initFooter()
        footer.setOnReloadListener {
            viewModel.loadData()
        }
        baseRV = findViewById(R.id.baseRV)
        refreshL.setTargetView(baseRV)
        initRecyclerView(baseRV)
        baseRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.hasLoad(recyclerView.layoutManager)
            }
        })
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

    abstract fun initAdapter(): CommonAdapter<*>?

    open fun getAdapter(): CommonAdapter<*>? {
        return adapter
    }

    open fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(mActivity)

    abstract fun setData(list: MutableList<*>)

    open fun initFooter(): BaseFooter {
        return BaseFooter(mActivity)
    }

    fun getFooter(): BaseFooter {
        return footer
    }

    fun getPageHintView(): PageHintView {
        return hintView
    }

    override fun showStatusView(status: PageStatus?) {
        if (viewModel.loader.isFirstLoad) {
            hintView.show(status)
            if (status == PageStatus.NORMAL) {
                baseRV.visibility = View.VISIBLE
            } else {
                baseRV.visibility = View.GONE
            }
        } else {
            hintView.show(PageStatus.NORMAL)
            baseRV.visibility = View.VISIBLE
        }
        if (status == PageStatus.NORMAL) {
            viewModel.loader.isFirstLoad = false
        }
    }
}