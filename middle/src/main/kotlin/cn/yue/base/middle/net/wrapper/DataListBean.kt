package cn.yue.base.middle.net.wrapper


/**
 * Description :
 * Created by yue on 2022/1/21
 */

class DataListBean<T> : ArrayList<T>(), IListModel<T>{

    override fun getList(): MutableList<T> {
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