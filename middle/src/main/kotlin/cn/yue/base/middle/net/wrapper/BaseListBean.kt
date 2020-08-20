package cn.yue.base.middle.net.wrapper

/**
 * 分页类型
 * Created by yue on 2018/7/11.
 */

open class BaseListBean<T> : BaseUnityListBean<T>() {


    fun getList(): MutableList<T>? {
        return super.getRealList()
    }

    fun getTotal(): Int {
        return super.getRealTotal()
    }

    fun getPageNo(): Int {
        return mPageNo
    }

    fun getPageSize(): Int {
        return if (getList() == null) 0 else getList()!!.size
    }

    fun getPageNt() : String? {
        return nt
    }

    fun getCurrentPageTotal() : Int {
        return if (getList()==null) 0 else getList()!!.size
    }

}

