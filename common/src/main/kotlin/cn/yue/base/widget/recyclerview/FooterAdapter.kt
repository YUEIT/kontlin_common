package cn.yue.base.widget.recyclerview

import android.view.ViewGroup
import cn.yue.base.R
import cn.yue.base.mvp.components.BaseFooter
import cn.yue.base.view.load.IFooter
import cn.yue.base.view.load.LoadStatus
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.alibaba.android.vlayout.layout.SingleLayoutHelper

class FooterAdapter: DelegateAdapter.Adapter<CommonViewHolder>(), IFooter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return CommonViewHolder.getHolder(parent.context, R.layout.layout_footer_adater, parent)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return SingleLayoutHelper()
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        holder.getView<BaseFooter>(R.id.v_footer)?.apply {
            showStatusView(status)
            setOnReloadListener(onReloadListener)
        }
    }

    private var status: LoadStatus? = null

    override fun showStatusView(loadStatus: LoadStatus?) {
        this.status = loadStatus
        notifyDataSetChanged()
    }

    private var onReloadListener: (() -> Unit)? = null

    fun setOnReloadListener(onReloadListener: (() -> Unit)?) {
        this.onReloadListener = onReloadListener
    }
}