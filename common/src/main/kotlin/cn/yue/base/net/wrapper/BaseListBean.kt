package cn.yue.base.net.wrapper

/**
 * 分页类型
 * Created by yue on 2018/7/11.
 */

open class BaseListBean<T> : BaseUnityListBean<T>(), IListModel<T> {

    override fun getList(): MutableList<T>? {
        return getRealList()
    }

    override fun getTotal(): Int {
        return getRealTotal()
    }

    override fun getPageNo(): Int {
        return getRealPageNo()
    }

    override fun getPageSize(): Int {
        return getRealPageSize()
    }

    override fun getCurrentPageTotal() : Int {
        return getList()?.size ?: 0
    }

}

