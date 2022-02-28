package cn.yue.base.middle.mvvm.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvvm.PullViewModel
import cn.yue.base.middle.view.PageHintView
import cn.yue.base.middle.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BasePullVMFragment<VM : PullViewModel> : BaseVMFragment<VM>(), IStatusView {
    private lateinit var refreshL: IRefreshLayout
    private lateinit var hintView: PageHintView
    private lateinit var contentView: View

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

    override fun initView(savedInstanceState: Bundle?) {
        hintView = findViewById(R.id.hintView)
        hintView.setOnReloadListener {
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
            hintView.setRefreshTarget(refreshL)
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { _, inflated ->
            contentView = inflated
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

    fun getPageHintView(): PageHintView {
        return hintView
    }

    override fun showStatusView(status: PageStatus?) {
        if (viewModel.loader.isFirstLoad) {
            hintView.show(status)
            if (status == PageStatus.NORMAL) {
                contentView.visibility = View.VISIBLE
            } else {
                contentView.visibility = View.GONE
            }
        } else {
            hintView.show(PageStatus.NORMAL)
            contentView.visibility = View.VISIBLE
        }
        if (status == PageStatus.NORMAL) {
            viewModel.loader.isFirstLoad = false
        }
    }
}