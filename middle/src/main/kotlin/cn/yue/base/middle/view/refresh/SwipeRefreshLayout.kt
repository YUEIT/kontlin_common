package cn.yue.base.middle.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.yue.base.middle.R

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class SwipeRefreshLayout(context: Context, attributeSet: AttributeSet): SwipeRefreshLayout(context, attributeSet), IRefreshLayout {

    private val colors = intArrayOf(R.color.progress_color_1,
            R.color.progress_color_2,
            R.color.progress_color_3,
            R.color.progress_color_4
    )

    init {
        setColorSchemeResources(*colors)
    }

    override fun setTargetView(targetView: View) {

    }

    override fun setEnabledRefresh(enable: Boolean) {
        super.setEnabled(enable)
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