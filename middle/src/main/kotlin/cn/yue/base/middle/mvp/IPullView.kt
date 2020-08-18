package cn.yue.base.middle.mvp

import cn.yue.base.middle.components.load.PageStatus

interface IPullView {
    fun finishRefresh()
    fun loadComplete(pageStatus: PageStatus?)
}