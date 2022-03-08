package cn.yue.base.middle.mvvm.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.middle.R
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.view.PageStateView
import cn.yue.base.middle.view.load.LoadStatus
import cn.yue.base.middle.view.load.PageStatus
import cn.yue.base.middle.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BasePullVMFragment<VM : PullViewModel> : BaseVMFragment<VM>() {
    private lateinit var refreshL: IRefreshLayout
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

    override fun initView(savedInstanceState: Bundle?) {
        stateView = findViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                viewModel.refresh()
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        refreshL = findViewById<View>(R.id.refreshL) as IRefreshLayout
        refreshL.setOnRefreshListener {
            viewModel.refresh()
        }
        refreshL.setEnabledRefresh(canPullDown())
        if (canPullDown()) {
            stateView.setRefreshTarget(refreshL)
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { _, inflated ->
            bindLayout(inflated)
        }
        baseVS.inflate()
    }

    open fun bindLayout(inflated: View) {}

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            viewModel.refresh()
        } else {
            viewModel.loader.pageStatus = PageStatus.NO_NET
        }
        viewModel.loader.observePage(this, Observer { pageStatus ->
            showStatusView(pageStatus)
        })
        viewModel.loader.observeLoad(this, Observer { loadStatus ->
            if (loadStatus === LoadStatus.REFRESH) {
                refreshL.startRefresh()
            } else {
                refreshL.finishRefreshing()
            }
        })
    }

    abstract fun getContentLayoutId(): Int

    open fun canPullDown(): Boolean {
        return true
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