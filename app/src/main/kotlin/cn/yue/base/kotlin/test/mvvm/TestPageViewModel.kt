package cn.yue.base.kotlin.test.mvvm

import android.app.Application
import cn.yue.base.kotlin.test.mode.ItemBean
import cn.yue.base.middle.mvvm.ListViewModel
import cn.yue.base.middle.net.ResultException
import cn.yue.base.middle.net.observer.BaseNetObserver
import cn.yue.base.middle.net.wrapper.BaseListBean
import cn.yue.base.middle.net.wrapper.DataListBean
import io.reactivex.Single

class TestPageViewModel(application: Application)
    : ListViewModel<DataListBean<ItemBean>, ItemBean>(application) {

     fun getRequestScope(nt: String): DataListBean<ItemBean> {
        val listBean = BaseListBean<ItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
//        val list: MutableList<ItemBean> = ArrayList()
         var list: DataListBean<ItemBean> = DataListBean()
        for (i in 0..19) {
            val testItemBean = ItemBean()
            testItemBean.index = i
            testItemBean.name = "this is $i"
            list.add(testItemBean)
        }
        listBean.dataList = list
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