package cn.yue.base.middle.components

import android.os.Bundle
import android.view.View

import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.middle.R
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.mvp.PageStatus
import cn.yue.base.middle.net.wrapper.BaseListBean
import kotlinx.android.synthetic.main.fragment_base_pull.*

/**
 * Description :
 * Created by yue on 2018/11/13
 */
abstract class BasePullFragment<T : BaseListBean<K>, K> : BaseFragment(), IStatusView {

    private var status: PageStatus = PageStatus.STATUS_NORMAL

    override fun getLayoutId(): Int {
        return R.layout.fragment_base_pull
    }

    override fun initView(savedInstanceState: Bundle?) {
        refreshL.setOnRefreshListener { refresh() }
        baseVS.layoutResource = getContentLayoutId()
        baseVS.inflate()
    }

    abstract fun getContentLayoutId(): Int

    abstract fun refresh()

    override fun showStatusView(status: PageStatus) {
        this.status = status
        when (status) {
            PageStatus.STATUS_SUCCESS -> {
                showPageHintSuccess()
            }
            PageStatus.STATUS_END -> {
                showPageHintSuccess()
            }
            PageStatus.STATUS_ERROR_NET -> {
                showPageHintErrorNet()
            }
            PageStatus.STATUS_ERROR_NO_DATA -> {
                showPageHintErrorNoData()
            }
            PageStatus.STATUS_ERROR_OPERATION -> {
                showPageHintErrorOperation()
            }
            else -> return
        }
    }

    protected fun showPageHintSuccess() {
        hintView.visibility = View.GONE
    }

    protected fun showPageHintErrorNet() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("网络异常~")
    }

    protected fun showPageHintErrorNoData() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("无数据~")
    }

    protected fun showPageHintErrorOperation() {
        hintView.visibility = View.VISIBLE
        hintView.setHintText("未知异常~")
    }

}
