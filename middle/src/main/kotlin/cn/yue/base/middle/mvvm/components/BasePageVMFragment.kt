package cn.yue.base.middle.mvvm.components

import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.middle.mvvm.PageViewModel
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