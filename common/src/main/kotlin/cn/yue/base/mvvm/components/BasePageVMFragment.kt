package cn.yue.base.mvvm.components

import cn.yue.base.mvvm.PageViewModel
import cn.yue.base.widget.recyclerview.CommonAdapter
/**
 * Description :
 * Created by yue on 2020/8/8
 */
open class BasePageVMFragment<VM : PageViewModel<S>, S> : BaseListVMFragment<VM, S>() {

    override fun initAdapter(): CommonAdapter<S>? {
        return null
    }

    override fun setData(list: MutableList<S>) {
        getAdapter()?.setList(list)
    }

}