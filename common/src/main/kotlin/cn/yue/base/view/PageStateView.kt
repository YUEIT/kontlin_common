package cn.yue.base.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import cn.yue.base.view.load.PageStatus
import cn.yue.base.view.refresh.IRefreshLayout


/**
 * Description :
 * Created by yue on 2022/3/5
 */

class PageStateView(context: Context, attributeSet: AttributeSet?): FrameLayout(context, attributeSet) {

    private var contentView: View? = null
    private var pageHintView: PageHintView = PageHintView(context, null)

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentView = getChildAt(0)
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(pageHintView, params)
    }

    fun setOnReloadListener(onReloadListener: (() -> Unit)?) {
        pageHintView.setOnReloadListener(onReloadListener)
    }

    fun setContentView(contentView: View?) {
        this.contentView = contentView
    }

    fun setContentViewById(@LayoutRes layoutId: Int) {
        this.contentView = View.inflate(context, layoutId, null)
    }

    fun setNoNetView(noNetView: View?) {
        pageHintView.setNoNetView(noNetView)
    }

    fun setNoNetViewById(@LayoutRes layoutId: Int): View {
        return pageHintView.setNoNetViewById(layoutId)
    }

    fun setNoDataView(noDataView: View?) {
        pageHintView.setNoDataView(noDataView)
    }

    fun setNoDataViewById(@LayoutRes layoutId: Int): View {
        return pageHintView.setNoDataViewById(layoutId)
    }

    fun setLoadingView(loadingView: View?) {
        pageHintView.setLoadingView(loadingView)
    }

    fun setLoadingViewById(@LayoutRes layoutId: Int): View {
        return pageHintView.setLoadingViewById(layoutId)
    }

    fun show(status: PageStatus?) {
        when (status) {
            PageStatus.NORMAL -> showSuccess()
            PageStatus.REFRESH -> showLoading()
            PageStatus.NO_NET -> showErrorNet()
            PageStatus.NO_DATA -> showErrorNoData()
            PageStatus.ERROR -> showErrorOperation()
            else -> {}
        }
    }

    fun showLoading() {
        pageHintView.showLoading()
        contentView?.visibility = View.GONE
    }

    fun showSuccess() {
        pageHintView.showSuccess()
        contentView?.visibility = View.VISIBLE
    }

    fun showErrorNet() {
        pageHintView.showErrorNet()
        contentView?.visibility = View.GONE
    }

    fun showErrorNoData() {
        pageHintView.showErrorNoData()
        contentView?.visibility = View.GONE
    }

    fun showErrorOperation() {
        pageHintView.showErrorOperation()
        contentView?.visibility = View.GONE
    }

    fun setRefreshTarget(refreshLayout: IRefreshLayout?) {
        pageHintView.setRefreshTarget(refreshLayout)
    }

}