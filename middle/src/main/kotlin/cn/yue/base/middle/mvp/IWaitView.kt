package cn.yue.base.middle.mvp

/**
 * Description :
 * Created by yue on 2019/3/13
 */
interface IWaitView {
    fun showWaitDialog(title: String?)
    fun dismissWaitDialog()
}