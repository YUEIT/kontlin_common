package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvp.IWaitView
import cn.yue.base.middle.mvp.PageStatus
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.net.NetworkConfig
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.wrapper.BaseListBean
import cn.yue.base.middle.view.PageHintView
import kotlinx.android.synthetic.main.fragment_base_pull.*

/**
 * Description :
 * Created by yue on 2018/11/13
 */
abstract class BasePullFragment<T : BaseListBean<K>, K> : BaseFragment(), IStatusView, IWaitView, IBaseView, IPhotoView {

    private var isFirstLoading: Boolean = true
    protected var status: PageStatus = PageStatus.STATUS_NORMAL
    private var photoHelper: PhotoHelper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

    override fun initView(savedInstanceState: Bundle?) {
        hintView.setOnReloadListener(object : PageHintView.OnReloadListener(){
            override fun onReload() {
                if (NetworkUtils.isConnected) {
                    mActivity.recreateFragment(this@BasePullFragment::javaClass.name)
                } else {
                    ToastUtils.showShortToast("网络不给力，请检查您的网络设置")
                }
            }

            override fun onRefresh() {
                super.onRefresh()
                if (NetworkUtils.isConnected) {
                    refresh()
                } else {
                    showPageHintErrorNet()
                }
            }
        })
        refreshL.setOnRefreshListener { refresh() }
        refreshL.isEnabled = canPullDown()
        if (canPullDown()) {
            hintView.setRefreshTarget(refreshL as ViewGroup)
        }
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { stub, inflated -> stubInflate(stub, inflated) }
        baseVS.inflate()
    }

    abstract fun getContentLayoutId(): Int

    abstract fun refresh()

    abstract fun canPullDown() : Boolean

    open fun stubInflate(stub: ViewStub, inflater: View) {}

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isConnected) {
            showLoadingView()
            refresh()
        } else {
            showPageHintErrorNet()
        }
    }

    fun stopRefreshAnim() {
        refreshL.finishRefreshing()
    }

    fun showLoadingView() {
        if (isFirstLoading) {
            baseVS.visibility = View.GONE
            showStatusView(PageStatus.STATUS_LOADING_REFRESH)
        } else {
            refreshL.startRefresh()
        }
    }

    fun showFailedView(e: ResultException) {
        if (NetworkConfig.ERROR_NO_NET == e.code) {
            showStatusView(PageStatus.STATUS_ERROR_NET)
        } else if (NetworkConfig.ERROR_NO_DATA == e.code) {
            showStatusView(PageStatus.STATUS_ERROR_NO_DATA)
        } else if (NetworkConfig.ERROR_OPERATION == e.code) {
            showStatusView(PageStatus.STATUS_ERROR_OPERATION)
            ToastUtils.showShortToast(e.message)
        } else {
            showStatusView(PageStatus.STATUS_ERROR_SERVER)
            ToastUtils.showShortToast(e.message)
        }
    }


    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_NORMAL, PageStatus.STATUS_SUCCESS -> showPageHintSuccess()
            PageStatus.STATUS_LOADING_REFRESH -> showPageHintLoading()
            PageStatus.STATUS_END -> showPageHintSuccess()
            PageStatus.STATUS_ERROR_NET -> showPageHintErrorNet()
            PageStatus.STATUS_ERROR_NO_DATA -> showPageHintErrorNoData()
            PageStatus.STATUS_ERROR_OPERATION -> showPageHintErrorOperation()
            PageStatus.STATUS_ERROR_SERVER -> showPageHintErrorServer()
        }
    }

    private fun showPageHintLoading() {
        if (hintView != null) {
            hintView.showLoading()
        }
    }

    private fun showPageHintSuccess() {
        if (baseVS != null) {
            baseVS.visibility = View.VISIBLE
        }
        if (hintView != null) {
            hintView.showSuccess()
        }
        isFirstLoading = false
    }

    private fun showPageHintErrorNet() {
        if (hintView != null) {
            if (isFirstLoading) {
                hintView.showErrorNet()
            } else {
                ToastUtils.showShortToast("网络不给力，请检查您的网络设置~")
            }
        }
    }

    private fun showPageHintErrorNoData() {
        if (hintView != null) {
            hintView.showErrorNoData()
        }
    }

    private fun showPageHintErrorOperation() {
        if (hintView != null && isFirstLoading) {
            hintView.showErrorOperation()
        }
    }

    private fun showPageHintErrorServer() {
        if (hintView != null && isFirstLoading) {
            hintView.showErrorOperation()
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
