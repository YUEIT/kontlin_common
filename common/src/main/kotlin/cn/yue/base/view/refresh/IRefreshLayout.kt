package cn.yue.base.view.refresh

import android.view.View

/**
 * Description :
 * Created by yue on 2019/6/19
 */
interface IRefreshLayout {
    fun setTargetView(targetView: View)
    fun setEnabledRefresh(enable: Boolean)
    fun startRefresh()
    fun finishRefreshing()
    fun setOnRefreshListener(onRefresh: (() -> Unit))
}