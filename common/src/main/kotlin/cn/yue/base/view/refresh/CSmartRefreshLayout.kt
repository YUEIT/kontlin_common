package cn.yue.base.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Description :
 * Created by yue on 2020/10/26
 */
class CSmartRefreshLayout(context: Context, attributeSet: AttributeSet) :
        SmartRefreshLayout(context, attributeSet), IRefreshLayout {

    init {
        setRefreshHeader(CustomClassicsHeader(context))
        setEnableOverScrollBounce(true)
        setEnableOverScrollDrag(true)
    }

    override fun setTargetView(targetView: View) {

    }

    override fun setRefreshEnable(enable: Boolean) {
        super.setEnableRefresh(enable)
    }

    override fun startRefresh() {
        autoRefresh()
    }

    override fun finishRefreshingState() {
        super.finishRefresh()
    }

    override fun setOnRefreshListener(onRefresh: () -> Unit) {
        super.setOnRefreshListener {
            onRefresh()
        }
    }

    override fun showLoadMoreEnd(show: Boolean) {

    }
}