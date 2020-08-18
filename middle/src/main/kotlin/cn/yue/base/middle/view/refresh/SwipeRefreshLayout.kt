package cn.yue.base.middle.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class SwipeRefreshLayout(context: Context, attributeSet: AttributeSet): SwipeRefreshLayout(context, attributeSet), IRefreshLayout {

    var COLORS = intArrayOf(cn.yue.base.common.R.color.progress_color_1, cn.yue.base.common.R.color.progress_color_2, cn.yue.base.common.R.color.progress_color_3, cn.yue.base.common.R.color.progress_color_4)

    override fun init() {
        setColorSchemeResources(*COLORS)
    }

    override fun setTargetView(targetView: View) {

    }

    override fun startRefresh() {
        isRefreshing = true
    }

    override fun finishRefreshing() {
        if (isRefreshing) {
            isRefreshing = false
        }
    }

    override fun setOnRefreshListener(onRefresh: () -> Unit) {
        super.setOnRefreshListener{
            onRefresh.invoke()
        }
    }

}