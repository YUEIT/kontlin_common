package cn.yue.base.middle.net.wrapper

/**
 * 分页类型
 * Created by yue on 2018/7/11.
 */

open class BaseListBean<T> : BaseListJavaBean<T>() {

    fun getPageNt(): String? = nt


    fun getList(): MutableList<T>? {
        return super.getRealList()
    }

    fun getTotal(): Int {
        return super.getRealTotal()
    }

    fun getPageNo(): Int {
        return mPageNo
    }

    fun getPageCount(): Int {
        return if (getList() == null) 0 else getList()!!.size
    }
}

