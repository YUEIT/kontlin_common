package cn.yue.base.middle.mvp

/**
 * Description :
 * Created by yue on 2019/6/18
 */
interface IWaitView {

    fun showWaitDialog(title: String)

    fun dismissWaitDialog()
}