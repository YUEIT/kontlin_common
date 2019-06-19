package cn.yue.base.middle.view

import android.content.Context
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cn.yue.base.common.activity.FRouter
import cn.yue.base.common.image.ImageLoader
import cn.yue.base.middle.R
import kotlinx.android.synthetic.main.layout_page_hint_loading.view.*

/**
 * Description :
 * Created by yue on 2018/11/13
 */
class PageHintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr){

    private var noNetView: View
    private var noDataView: View
    private var loadingView: View
    private var serverErrorView: View
    init {
        isClickable = true
        noNetView = inflate(context, R.layout.layout_page_hint_no_net, null)
        noDataView = inflate(context, R.layout.layout_page_hint_no_data, null)
        loadingView = inflate(context, R.layout.layout_page_hint_loading, null)
        serverErrorView = View.inflate(context, R.layout.layout_page_hint_server_error, null)
        ImageLoader.getLoader().loadGif(loadingIV, R.drawable.icon_page_loading)
        noNetView.findViewById<TextView>(R.id.reloadTV).setOnClickListener{
            if (onReloadListener != null) {
                onReloadListener!!.onReload()
            }
        }
        noNetView.findViewById<TextView>(R.id.checkNetTV).setOnClickListener {
            FRouter.instance.build("/middle/noNet").navigation(context)
        }
        serverErrorView.findViewById<TextView>(R.id.reloadTV).setOnClickListener {
            if (onReloadListener != null) {
                onReloadListener!!.onRefresh()
            }
        }
        serverErrorView.findViewById<TextView>(R.id.checkNetTV).setOnClickListener{
            FRouter.instance.build("/middle/noNet").navigation(context)
        }
    }

    private var onReloadListener: OnReloadListener? = null

    fun setOnReloadListener(onReloadListener: OnReloadListener) {
        this.onReloadListener = onReloadListener
    }

    abstract class OnReloadListener {
        abstract fun onReload()
        open fun onRefresh() {}
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

    fun setLoadingView(loadingView: View) {
        if (noDataView != null) {
            this.loadingView = loadingView
        }
    }

    fun setLoadingViewById(@LayoutRes layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        setLoadingView(view)
        return view
    }

    fun showLoading() {
        if (loadingView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(loadingView)
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
            addView(noNetView)
            setRefreshEnable(false)
        }
    }

    fun showErrorNoData() {
        if (noDataView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(noDataView)
            setRefreshEnable(true)
        }
    }

    fun showErrorOperation() {
        if (noNetView != null) {
            visibility = View.VISIBLE
            removeAllViews()
            addView(serverErrorView)
            setRefreshEnable(false)
        }
    }

    private var refreshLayout: ViewGroup? = null
    fun setRefreshTarget(refreshLayout: ViewGroup) {
        this.refreshLayout = refreshLayout
    }

    fun setRefreshEnable(enable: Boolean) {
        if (refreshLayout != null) {
            refreshLayout!!.isEnabled = enable
        }
    }

    override fun addView(child: View) {
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(child, params)
    }

}
