package cn.yue.base.mvp.components.data

import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

class Loader {

    var isFirstLoad: Boolean
    var pageStatus: PageStatus
    var loadStatus: LoadStatus

    init {
        pageStatus = PageStatus.NORMAL
        loadStatus = LoadStatus.NORMAL
        isFirstLoad = true
    }

    fun setLoadStatus(loadStatus: LoadStatus): LoadStatus {
        this.loadStatus = loadStatus
        return loadStatus
    }

    fun setPageStatus(pageStatus: PageStatus): PageStatus {
        this.pageStatus = pageStatus
        return pageStatus
    }

}