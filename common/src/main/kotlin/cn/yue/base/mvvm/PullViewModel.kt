package cn.yue.base.mvvm

import cn.yue.base.mvp.IStatusView
import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PullViewModel : BaseViewModel(), IStatusView {

    /**
     * 刷新
     */
    @JvmOverloads
    fun refresh(isPageRefreshAnim: Boolean = loader.isFirstLoad) {
        if (loader.isLoading()) {
            return
        }
        if (isPageRefreshAnim) {
            loader.pageStatus = PageStatus.REFRESH
        } else {
            loader.loadStatus = LoadStatus.REFRESH
        }
        loadData()
    }

    fun loadRefresh() {
        refresh(false)
    }

    abstract fun loadData()

    fun silenceLoadData() {
        if (loader.isLoading()) {
            return
        }
        loadData()
    }

    override fun changePageStatus(status: PageStatus) {
        loader.pageStatus = status
    }

    override fun changeLoadStatus(status: LoadStatus) {
        loader.loadStatus = status
    }
}