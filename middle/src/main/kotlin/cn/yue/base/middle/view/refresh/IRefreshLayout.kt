package cn.yue.base.middle.view.refresh

import android.view.View

/**
 * Description :
 * Created by yue on 2019/6/19
 */
interface IRefreshLayout {
    fun init()
    fun setTargetView(targetView: View)
    fun setEnabled(enable: Boolean)
    fun startRefresh()
    fun finishRefreshing()
    fun setOnRefreshListener(onRefresh: (() -> Unit))
}