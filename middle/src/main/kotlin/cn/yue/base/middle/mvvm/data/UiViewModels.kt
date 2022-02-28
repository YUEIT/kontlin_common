package cn.yue.base.middle.mvvm

import cn.yue.base.middle.net.wrapper.IListModel


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