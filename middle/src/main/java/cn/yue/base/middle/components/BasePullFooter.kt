package cn.yue.base.middle.components

import android.content.Context
import android.view.View
import android.widget.RelativeLayout
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvp.PageStatus
import kotlinx.android.synthetic.main.layout_footer_base_pull.view.*

/**
 * Description :
 * Created by yue on 2018/11/20
 */
class BasePullFooter : RelativeLayout, IStatusView {

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.layout_footer_base_pull, this)
        setOnClickListener{
            if (status == PageStatus.STATUS_ERROR_NET) {
                onReloadListener?.onReload()
            }
        } }

    private var status: PageStatus = PageStatus.STATUS_LOADING
    private var onReloadListener: OnReloadListener? = null
    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_LOADING ->  {
                hintTV.text = "加载中~"
            }
            PageStatus.STATUS_END -> {
                hintTV.text = "- END -"
            }
            PageStatus.STATUS_ERROR_NET -> {
                hintTV.text = "网络异常，点击重新加载~"
            }
        }
    }

    fun setOnReloadListener(onReloadListener: OnReloadListener) {
        this.onReloadListener = onReloadListener
    }

    interface OnReloadListener {
        fun onReload()
    }

}
