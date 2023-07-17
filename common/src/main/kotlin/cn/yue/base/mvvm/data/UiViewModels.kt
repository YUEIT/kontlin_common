package cn.yue.base.mvvm.data

import cn.yue.base.mvvm.ItemViewModel
import cn.yue.base.net.wrapper.IListModel


/**
 * Description :
 * Created by yue on 2022/2/21
 */

class UiViewModels : ArrayList<ItemViewModel>(), IListModel<ItemViewModel> {

    override fun getList(): MutableList<ItemViewModel>? {
        return this
    }

    override fun getTotal(): Int {
        return 0
    }

    override fun getPageNo(): Int {
        return 0
    }

    override fun getPageSize(): Int {
        return size
    }

    override fun getPageNt(): String? {
        return null
    }

    override fun getCurrentPageTotal(): Int {
        return size
    }
}