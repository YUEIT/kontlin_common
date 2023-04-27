package cn.yue.base.middle.mvp.components

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.components.data.Loader
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.view.PageStateView
import cn.yue.base.middle.view.load.LoadStatus
import cn.yue.base.middle.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2019/3/8
 */
abstract class BaseHintFragment : BaseFragment(), IBaseView, IPhotoView {
    var loader = Loader()
    private lateinit var stateView: PageStateView

    private var photoHelper: PhotoHelper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_hint
    }

    override fun initView(savedInstanceState: Bundle?) {
        loader.setPageStatus(PageStatus.NORMAL)
        stateView = findViewById(R.id.stateView)
        stateView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                changePageStatus(PageStatus.NORMAL)
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        val baseVS = findViewById<ViewStub>(R.id.baseVS)
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { _, inflated -> bindLayout(inflated) }
        baseVS.inflate()
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            changePageStatus(PageStatus.NORMAL)
        } else {
            changePageStatus(PageStatus.NO_NET)
        }
    }

    abstract fun getContentLayoutId(): Int

    open fun bindLayout(inflated: View) {}

    fun getPageStateView(): PageStateView {
        return stateView
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
    }

    override fun changeLoadStatus(status: LoadStatus) {

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

    fun getPhotoHelper(): PhotoHelper {
        if (photoHelper == null) {
            photoHelper = PhotoHelper(mActivity, this)
        }
        return photoHelper!!
    }

    override fun selectImageResult(selectList: List<MediaData>?) {}
    override fun cropImageResult(image: Uri?) {}
    override fun uploadImageResult(serverList: List<String>?) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoHelper != null) {
            photoHelper!!.onActivityResult(requestCode, resultCode, data)
        }
    }
}