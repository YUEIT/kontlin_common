package cn.yue.test.mvvm

import android.app.Application
import cn.yue.base.mvvm.ListViewModel
import cn.yue.base.net.ResultException
import cn.yue.base.net.observer.BaseNetObserver
import cn.yue.base.net.wrapper.BaseListBean
import cn.yue.base.net.wrapper.DataListBean
import cn.yue.test.mode.ItemBean
import io.reactivex.rxjava3.core.Single

class TestPageViewModel(application: Application)
    : ListViewModel<DataListBean<ItemBean>, ItemBean>(application) {

    var loadTemp = 0

     fun getRequestScope(nt: String): DataListBean<ItemBean> {
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
        return list
    }

    override fun doLoadData(nt: String) {
//        viewModelScope.request({
//            getRequestScope(nt)
//        }, PageDelegateObserver())
        Single.create<DataListBean<ItemBean>> {
            it.onSuccess(getRequestScope(nt))
        }.compose(PageTransformer())
            .subscribe()
    }

    override fun getPageObserver(): BaseNetObserver<DataListBean<ItemBean>> {
        return object : PageObserver() {
            override fun onRefreshComplete(p: DataListBean<ItemBean>?, e: ResultException?) {
                super.onRefreshComplete(p, e)
            }
        }
    }
}