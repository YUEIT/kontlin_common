package cn.yue.base.middle.mvvm

import android.app.Application
import cn.yue.base.middle.components.load.LoadStatus
import cn.yue.base.middle.components.load.PageStatus
import cn.yue.base.middle.mvp.IPullView
/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class PullViewModel(application: Application) : BaseViewModel(application), IPullView {

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

    override fun finishRefresh() {
        loader.loadStatus = LoadStatus.NORMAL
    }

    override fun loadComplete(pageStatus: PageStatus?) {
        loader.pageStatus = pageStatus
    }
}