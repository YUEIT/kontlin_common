package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.Loader
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.view.PageHintView

/**
 * Description :
 * Created by yue on 2019/3/8
 */
abstract class BaseHintFragment : BaseFragment(), IBaseView, IPhotoView {
    var loader = Loader()
    private lateinit var hintView: PageHintView
    private lateinit var baseVS: ViewStub
    private var photoHelper: PhotoHelper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_hint
    }

    override fun initView(savedInstanceState: Bundle?) {
        loader.setPageStatus(PageStatus.NORMAL)
        hintView = findViewById(R.id.hintView)
        hintView.setOnReloadListener {
            if (NetworkUtils.isAvailable()) {
                showStatusView(loader.setPageStatus(PageStatus.NORMAL))
            } else {
                showShortToast(R.string.app_no_net.getString())
            }
        }
        baseVS = findViewById(R.id.baseVS)
        baseVS.layoutResource = getContentLayoutId()
        baseVS.setOnInflateListener { _, inflated -> bindLayout(inflated) }
        baseVS.inflate()
    }

    override fun initOther() {
        super.initOther()
        if (NetworkUtils.isAvailable()) {
            showStatusView(loader.setPageStatus(PageStatus.NORMAL))
        } else {
            showStatusView(loader.setPageStatus(PageStatus.NO_NET))
        }
    }

    abstract fun getContentLayoutId(): Int

    open fun bindLayout(inflated: View) {}

    fun getPageHintView(): PageHintView {
        return hintView
    }

    override fun showStatusView(status: PageStatus?) {
        if (loader.isFirstLoad) {
            hintView.show(status)
        } else {
            hintView.show(PageStatus.NORMAL)
        }
        if (status === PageStatus.NORMAL) {
            loader.isFirstLoad = false
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

    fun getPhotoHelper(): PhotoHelper {
        if (photoHelper == null) {
            photoHelper = PhotoHelper(mActivity, this)
        }
        return photoHelper!!
    }

    override fun selectImageResult(selectList: List<String>?) {}
    override fun cropImageResult(image: String?) {}
    override fun uploadImageResult(serverList: List<String>?) {}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (photoHelper != null) {
            photoHelper!!.onActivityResult(requestCode, resultCode, data)
        }
    }
}