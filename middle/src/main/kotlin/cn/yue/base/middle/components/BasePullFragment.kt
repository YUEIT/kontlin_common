package cn.yue.base.middle.components

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewStub
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.utils.debug.ToastUtils.showShortToast
import cn.yue.base.common.utils.device.NetworkUtils
import cn.yue.base.common.widget.dialog.WaitDialog
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.Loader
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IBaseView
import cn.yue.base.middle.mvp.IPullView
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.base.middle.view.PageHintView
import cn.yue.base.middle.view.refresh.IRefreshLayout

/**
 * Description :
 * Created by yue on 2019/3/7
 */
abstract class BasePullFragment : BaseFragment(), IBaseView, IPhotoView, IPullView {
    private val loader = Loader()
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
                refresh()
            } else {
                showShortToast("网络不给力，请检查您的网络设置~")
            }
        }
        refreshL = (findViewById<View>(R.id.refreshL) as IRefreshLayout)
        refreshL.setOnRefreshListener {
            refresh()
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
        if (NetworkUtils.isAvailable()) {
            refresh()
        } else {
            showStatusView(loader.setPageStatus(PageStatus.NO_NET))
        }
    }

    abstract fun getContentLayoutId(): Int

    //回调继承 BasePullSingleObserver 以适应加载逻辑
    abstract fun loadData()

    open fun canPullDown(): Boolean {
        return true
    }

    /**
     * 刷新 选择是否页面加载动画
     */
    @JvmOverloads
    fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.pageStatus === PageStatus.LOADING
                || loader.loadStatus === LoadStatus.REFRESH) {
            return
        }
        if (isPageRefreshAnim) {
            contentView.visibility = View.GONE
            showStatusView(loader.setPageStatus(PageStatus.LOADING))
        } else {
            startRefresh()
        }
        loadData()
    }

    private fun startRefresh() {
        loader.setLoadStatus(LoadStatus.REFRESH)
        refreshL.startRefresh()
    }

    override fun finishRefresh() {
        loader.setLoadStatus(LoadStatus.NORMAL)
        refreshL.finishRefreshing()
    }

    override fun loadComplete(status: PageStatus?) {
        showStatusView(loader.setPageStatus(status!!))
    }

    /**
     * @hide 子类切勿直接调用
     */
    override fun showStatusView(status: PageStatus?) {
        if (loader.isFirstLoad) {
            hintView.show(status)
            if (loader.pageStatus === PageStatus.NORMAL) {
                contentView.visibility = View.VISIBLE
            } else {
                contentView.visibility = View.GONE
            }
        } else {
            hintView.show(PageStatus.NORMAL)
            contentView.visibility = View.VISIBLE
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

    private var photoHelper: PhotoHelper? = null

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