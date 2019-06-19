package cn.yue.base.middle.components

import android.content.Context
import android.graphics.Color
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

    private var status: PageStatus = PageStatus.STATUS_LOADING_ADD
    private var onReloadListener: OnReloadListener? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.layout_footer_base_pull, this)
        setOnClickListener{
            if (status == PageStatus.STATUS_ERROR_NET) {
                onReloadListener?.onReload()
            }
        } }

    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_NORMAL, PageStatus.STATUS_LOADING_ADD, PageStatus.STATUS_SUCCESS -> {
                loadingLL.visibility = View.VISIBLE
                endLL.visibility = View.GONE
                errorLL.visibility = View.GONE
                emptyLL.visibility = View.GONE
                if (progress != null) {
                    progress.setProgressBarBackgroundColor(Color.parseColor("#EFEFEF"))
                    progress.startAnimation()
                }
            }
            PageStatus.STATUS_END -> {
                loadingLL.visibility = View.GONE
                endLL.visibility = View.VISIBLE
                errorLL.visibility = View.GONE
                emptyLL.visibility = View.GONE
                if (progress != null) {
                    progress.stopAnimation()
                }
            }
            PageStatus.STATUS_ERROR_NET -> {
                loadingLL.visibility = View.GONE
                endLL.visibility = View.GONE
                errorLL.visibility = View.VISIBLE
                emptyLL.visibility = View.GONE
                if (progress != null) {
                    progress.stopAnimation()
                }
            }
            PageStatus.STATUS_ERROR_NO_DATA -> {
                loadingLL.visibility = View.GONE
                endLL.visibility = View.GONE
                errorLL.visibility = View.GONE
                emptyLL.visibility = View.VISIBLE
                if (progress != null) {
                    progress.stopAnimation()
                }
            }
        }
    }

    fun setFooterSuccess(layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        loadingLL.removeAllViews()
        loadingLL.addView(view)
        return view
    }

    fun setFooterEnd(layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        endLL.removeAllViews()
        endLL.addView(view)
        return view
    }

    fun setFooterEmpty(layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        emptyLL.removeAllViews()
        emptyLL.addView(view)
        return view
    }

    fun setFooterError(layoutId: Int): View {
        val view = View.inflate(context, layoutId, null)
        errorLL.removeAllViews()
        errorLL.addView(view)
        return view
    }

    fun setOnReloadListener(onReloadListener: OnReloadListener) {
        this.onReloadListener = onReloadListener
    }

    interface OnReloadListener {
        fun onReload()
    }

}
