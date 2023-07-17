package cn.yue.base.mvvm.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import cn.yue.base.common.R
import cn.yue.base.mvvm.BaseViewModel
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils.showShortToast
import cn.yue.base.utils.device.NetworkUtils
import cn.yue.base.view.PageStateView
import cn.yue.base.view.load.PageStatus
import cn.yue.base.widget.TopBar

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class BaseHintVMFragment<VM : BaseViewModel> : BaseVMFragment<VM>() {
    private lateinit var stateView: PageStateView

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_hint
    }

    abstract fun getContentLayoutId(): Int

    override fun initView(savedInstanceState: Bundle?) {
        stateView = findViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                viewModel.loader.pageStatus = PageStatus.NORMAL
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { _, inflated -> bindLayout(inflated) }
        baseVS.inflate()
    }

    open fun bindLayout(inflated: View) {}

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            viewModel.loader.pageStatus = PageStatus.NORMAL
        } else {
            viewModel.loader.pageStatus = PageStatus.NO_NET
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.loader.observePage(this, Observer {
                pageStatus -> showStatusView(pageStatus)
        })
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
    }

    private fun showStatusView(status: PageStatus?) {
        if (viewModel.loader.isFirstLoad) {
            stateView.show(status)
        }
        if (status == PageStatus.NORMAL) {
            viewModel.loader.isFirstLoad = false
        }
    }

    fun getPageStateView(): PageStateView {
        return stateView
    }
}