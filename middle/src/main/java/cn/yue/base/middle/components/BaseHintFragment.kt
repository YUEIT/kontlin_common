package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import cn.yue.base.middle.view.PageHintView
import kotlinx.android.synthetic.main.fragment_base_hint.*

/**
 * Description :
 * Created by yue on 2019/6/18
 */
abstract class BaseHintFragment : BaseFragment(), IStatusView, IWaitView, IBaseView, IPhotoView {

    private var isFirstLoading = true
    protected var status: PageStatus = PageStatus.STATUS_NORMAL
    private var photoHelper: PhotoHelper? = null

    override fun getLayoutId(): Int = R.layout.fragment_base_hint

    override fun initView(savedInstanceState: Bundle?) {
        hintView.setOnReloadListener(object : PageHintView.OnReloadListener() {
            override fun onReload() {
                if (NetworkUtils.isConnected) {
                    mActivity.recreateFragment(this@BaseHintFragment::class.java.name)
                } else {
                    ToastUtils.showShortToast("网络不给力，请检查您的网络设置~")
                }
            }

        })
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { stub, inflated -> stubInflate(stub, inflated) }
        baseVS.inflate()
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isConnected) {
            showStatusView(status)
        } else {
            showStatusView(PageStatus.STATUS_ERROR_NET)
        }
    }

    abstract fun getContentLayoutId(): Int

    fun stubInflate(stub: ViewStub, inflated: View) {}

    override fun showStatusView(status: PageStatus) {
        when (status) {
            PageStatus.STATUS_NORMAL, PageStatus.STATUS_SUCCESS -> showPageHintSuccess()
            PageStatus.STATUS_LOADING_REFRESH -> showPageHintLoading()
            PageStatus.STATUS_END -> showPageHintSuccess()
            PageStatus.STATUS_ERROR_NET -> showPageHintErrorNet()
            PageStatus.STATUS_ERROR_NO_DATA -> showPageHintErrorNoData()
            PageStatus.STATUS_ERROR_OPERATION -> showPageHintErrorOperation()
            PageStatus.STATUS_ERROR_SERVER -> showPageHintErrorServer()
        }
        this.status = status
    }

    private fun showPageHintLoading() {
        hintView.showLoading()
    }

    private fun showPageHintSuccess() {
        hintView.showSuccess()
        isFirstLoading = false
    }

    private fun showPageHintErrorNet() {
        if (isFirstLoading) {
            hintView.showErrorNet()
        } else {
            ToastUtils.showShortToast("网络不给力，请检查您的网络设置~")
        }
    }

    private fun showPageHintErrorNoData() {
        hintView.showErrorNoData()
    }

    private fun showPageHintErrorOperation() {
        if (isFirstLoading) {
            hintView.showErrorOperation()
        }
    }

    private fun showPageHintErrorServer() {
        if (isFirstLoading) {
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