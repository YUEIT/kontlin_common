package cn.yue.base.middle.view.refresh

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
    }

    override fun setTargetView(targetView: View) {

    }

    override fun setEnabledRefresh(enable: Boolean) {
        super.setEnableRefresh(enable)
    }

    override fun startRefresh() {
        autoRefresh()
    }

    override fun finishRefreshing() {
        super.finishRefresh()
    }

    override fun setOnRefreshListener(onRefresh: () -> Unit) {
        super.setOnRefreshListener {
            onRefresh()
        }
    }
}