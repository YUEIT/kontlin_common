package cn.yue.base.middle.mvvm.components

import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.middle.mvvm.PageViewModel

open class BasePageVMFragment<VM : PageViewModel<S>, S> : BaseListVMFragment<VM>() {

    override fun initAdapter(): CommonAdapter<S>? {
        return null
    }

    override fun getAdapter(): CommonAdapter<S>? {
        return super.getAdapter() as CommonAdapter<S>?
    }

    override fun setData(list: MutableList<*>) {
        getAdapter()?.setList(list as MutableList<S>)
    }
}