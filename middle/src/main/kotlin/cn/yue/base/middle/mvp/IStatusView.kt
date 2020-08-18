package cn.yue.base.middle.mvp

import cn.yue.base.middle.components.load.PageStatus

/**
 * Description :
 * Created by yue on 2018/11/13
 */
interface IStatusView {
    fun showStatusView(status: PageStatus?)
}