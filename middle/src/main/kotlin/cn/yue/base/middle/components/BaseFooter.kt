package cn.yue.base.middle.components

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.yue.base.common.widget.dialog.AppProgressBar
import cn.yue.base.middle.R
import cn.yue.base.middle.components.load.LoadStatus

/**
 * Description :
 * Created by yue on 2019/3/7
 */
class BaseFooter(context: Context?) : RelativeLayout(context) {
    private var loadStatus: LoadStatus? = null
    private val progressBar: AppProgressBar?
    private val loadingLL: LinearLayout
    private val endLL: LinearLayout
    private val errorLL: LinearLayout
    private val emptyLL: LinearLayout

    init {
        View.inflate(context, R.layout.layout_footer_base_pull, this)
        progressBar = findViewById(R.id.progress)
        loadingLL = findViewById(R.id.loadingLL)
        endLL = findViewById(R.id.endLL)
        errorLL = findViewById(R.id.errorLL)
        errorLL.setOnClickListener {
            if (loadStatus === LoadStatus.NO_NET && onReloadListener != null) {
                onReloadListener!!()
            }
        }
        emptyLL = findViewById(R.id.emptyLL)
        showNormal()
    }

    fun showStatusView(loadStatus: LoadStatus?) {
        this.loadStatus = loadStatus
        when (loadStatus) {
            LoadStatus.NORMAL -> showNormal()
            LoadStatus.LOADING -> showLoading()
            LoadStatus.END -> showEnd()
            LoadStatus.NO_DATA -> showNoData()
            LoadStatus.NO_NET -> showNoNet()
        }
    }

    private fun showNormal() {
        loadingLL.visibility = View.GONE
        endLL.visibility = View.GONE
        errorLL.visibility = View.GONE
        emptyLL.visibility = View.GONE
        progressBar?.stopAnimation()
    }

    private fun showLoading() {
        loadingLL.visibility = View.VISIBLE
        endLL.visibility = View.GONE
        errorLL.visibility = View.GONE
        emptyLL.visibility = View.GONE
        if (progressBar != null) {
            progressBar.setProgressBarBackgroundColor(Color.parseColor("#EFEFEF"))
            progressBar.startAnimation()
        }
    }

    private fun showEnd() {
        loadingLL.visibility = View.GONE
        endLL.visibility = View.VISIBLE
        errorLL.visibility = View.GONE
        emptyLL.visibility = View.GONE
        progressBar?.stopAnimation()
    }

    private fun showNoNet() {
        loadingLL.visibility = View.GONE
        endLL.visibility = View.GONE
        errorLL.visibility = View.VISIBLE
        emptyLL.visibility = View.GONE
        progressBar?.stopAnimation()
    }

    private fun showNoData() {
        loadingLL.visibility = View.GONE
        endLL.visibility = View.GONE
        errorLL.visibility = View.GONE
        emptyLL.visibility = View.VISIBLE
        progressBar?.stopAnimation()
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

    private var onReloadListener: (() -> Unit)? = null
    fun setOnReloadListener(onReloadListener: (() -> Unit)?) {
        this.onReloadListener = onReloadListener
    }

}