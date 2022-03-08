package cn.yue.base.middle.mvvm

import android.app.Application
import cn.yue.base.middle.mvp.IStatusView
import cn.yue.base.middle.view.load.LoadStatus
import cn.yue.base.middle.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PullViewModel(application: Application) : BaseViewModel(application), IStatusView {

    /**
     * 刷新
     */
    @JvmOverloads
    fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.loadStatus === LoadStatus.REFRESH
                || loader.pageStatus === PageStatus.LOADING) {
            return
        }
        if (isPageRefreshAnim) {
            loader.pageStatus = PageStatus.LOADING
        } else {
            loader.loadStatus = LoadStatus.REFRESH
        }
        loadData()
    }

    fun loadRefresh() {
        refresh(false)
    }

    protected abstract fun loadData()

    private fun startRefresh() {
        loader.loadStatus = LoadStatus.REFRESH
    }

    override fun changePageStatus(status: PageStatus) {
        loader.pageStatus = status
    }

    override fun changeLoadStatus(status: LoadStatus) {
        loader.loadStatus = status
    }
}