package cn.yue.test.mvvm

import android.app.Application
import cn.yue.base.mvvm.ListViewModel
import cn.yue.base.mvvm.PageViewModel
import cn.yue.base.net.ResultException
import cn.yue.base.net.observer.BaseNetObserver
import cn.yue.base.net.wrapper.BaseListBean
import cn.yue.base.net.wrapper.DataListBean
import cn.yue.test.mode.ItemBean
import io.reactivex.rxjava3.core.Single

class TestPageViewModel : PageViewModel<ItemBean>() {

    var loadTemp = 0

     fun getRequestScope(nt: Int): BaseListBean<ItemBean> {
        val listBean = BaseListBean<ItemBean>()
        listBean.mTotal = 22
//        val list: MutableList<ItemBean> = ArrayList()
         var list: DataListBean<ItemBean> = DataListBean()
         loadTemp++
        for (i in 0..19) {
            val testItemBean = ItemBean()
            testItemBean.index = i
            testItemBean.name = "this is $loadTemp $i"
            list.add(testItemBean)
        }
        listBean.mList = list
        return listBean
    }

    override fun doLoadData(nt: Int) {
        //        viewModelScope.request({
//            getRequestScope(nt)
//        }, PageDelegateObserver())
        Single.create {
            it.onSuccess(getRequestScope(nt))
        }.defaultSubscribe()
    }

}