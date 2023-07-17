package cn.yue.base.net.wrapper


/**
 * Description :
 * Created by yue on 2022/1/21
 */

interface IListModel<T> {

    fun getList(): MutableList<T>?

    fun getTotal(): Int

    fun getPageNo(): Int

    fun getPageSize(): Int

    fun getPageNt() : String?

    fun getCurrentPageTotal() : Int
}