package cn.yue.base.middle.mvvm.components

import android.os.Bundle
import android.view.View
import android.view.ViewStub
import androidx.lifecycle.Observer
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.middle.R
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.view.PageStateView
import cn.yue.base.middle.view.load.PageStatus

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
        viewModel.loader.observePage(this, Observer {
            pageStatus -> showStatusView(pageStatus)
        })
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