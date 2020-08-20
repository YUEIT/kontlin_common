package cn.yue.base.middle.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import cn.yue.base.common.image.ImageLoader
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.router.FRouter.Companion.instance

/**
 * Description :
 * Created by yue on 2018/11/13
 */
class PageHintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    private var noNetView: View? = null
    private var noDataView: View? = null
    private var loadingView: View? = null
    private var serverErrorView: View? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        isClickable = true
        setDefault(context)
    }

    private fun setDefault(context: Context) {
        loadingView = View.inflate(context, R.layout.layout_page_hint_loading, null)
        noNetView = View.inflate(context, R.layout.layout_page_hint_no_net, null)
        noDataView = View.inflate(context, R.layout.layout_page_hint_no_data, null)
        serverErrorView = View.inflate(context, R.layout.layout_page_hint_server_error, null)
        val loadingIV = loadingView?.findViewById<ImageView>(R.id.loadingIV)
        ImageLoader.getLoader().loadGif(loadingIV, R.drawable.app_icon_wait)
        noNetView?.findViewById<View>(R.id.reloadTV)?.setOnClickListener {
                onReloadListener?.apply {
                    invoke()
                }
        }
        noNetView?.findViewById<View>(R.id.checkNetTV)?.setOnClickListener {
            instance.build("/middle/noNet").navigation(context)
        }
        serverErrorView?.findViewById<View>(R.id.reloadTV)?.setOnClickListener {
            onReloadListener?.apply {
                invoke()
            }
        }
        serverErrorView?.findViewById<View>(R.id.checkNetTV)?.setOnClickListener {
            instance.build("/middle/noNet").navigation(context)
        }
    }

    private var onReloadListener: (() -> Unit)? = null
    fun setOnReloadListener(onReloadListener: (() -> Unit)?) {
        this.onReloadListener = onReloadListener
    }


    fun setNoNetView(noNetView: View?) {
        if (noNetView != null) {
            this.noNetView = noNetView
        }
    }

    fun setNoNetViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setNoNetView(view)
        return view
    }

    fun setNoDataView(noDataView: View?) {
        if (noDataView != null) {
            this.noDataView = noDataView
        }
    }

    fun setNoDataViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setNoDataView(view)
        return view
    }

    fun setLoadingView(loadingView: View?) {
        if (noDataView != null) {
            this.loadingView = loadingView
        }
    }

    fun setLoadingViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setLoadingView(view)
        return view
    }

    fun show(status: PageStatus?) {
        when (status) {
            PageStatus.NORMAL -> showSuccess()
            PageStatus.LOADING -> showLoading()
            PageStatus.NO_NET -> showErrorNet()
            PageStatus.NO_DATA -> showErrorNoData()
            PageStatus.ERROR -> showErrorOperation()
        }
    }

    fun showLoading() {
        if (loadingView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(loadingView!!)
            setRefreshEnable(false)
        }
    }

    fun showSuccess() {
        visibility = View.GONE
        setRefreshEnable(true)
    }

    fun showErrorNet() {
        if (noNetView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(noNetView!!)
            setRefreshEnable(true)
        }
    }

    fun showErrorNoData() {
        if (noDataView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(noDataView!!)
            setRefreshEnable(true)
        }
    }

    fun showErrorOperation() {
        if (noNetView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(serverErrorView!!)
        }
    }

    private var refreshLayout: ViewGroup? = null

    fun setRefreshTarget(refreshLayout: ViewGroup?) {
        this.refreshLayout = refreshLayout
    }

    private fun setRefreshEnable(enable: Boolean) {
        if (refreshLayout != null) {
            refreshLayout!!.isEnabled = enable
        }
    }

    override fun addView(child: View) {
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(child, params)
    }


}