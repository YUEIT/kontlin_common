package cn.yue.base.mvp

import cn.yue.base.view.load.LoadStatus
import cn.yue.base.view.load.PageStatus

/**
 * Description :
 * Created by yue on 2018/11/13
 */
interface IStatusView {
    fun changePageStatus(status: PageStatus)
    fun changeLoadStatus(status: LoadStatus)
}