package cn.yue.base.view.refresh

import android.view.View

/**
 * Description :
 * Created by yue on 2019/6/19
 */
interface IRefreshLayout {
    fun setTargetView(targetView: View)
    fun setRefreshEnable(enable: Boolean)
    fun startRefresh()
    fun finishRefreshingState()
    fun setOnRefreshListener(onRefresh: (() -> Unit))

    fun showLoadMoreEnd(show: Boolean)
}