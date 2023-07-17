package cn.yue.test.mvvm.ui_vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import cn.yue.base.mvvm.ItemViewModel
import cn.yue.base.mvvm.ListViewModel
import cn.yue.base.mvvm.data.UiViewModels
import cn.yue.base.net.coroutine.request
import cn.yue.base.net.wrapper.DataListBean
import cn.yue.test.mode.ItemBean
import io.reactivex.rxjava3.core.Single


/**
 * Description :
 * Created by yue on 2022/2/21
 */

class TestUiViewModel(application: Application): ListViewModel<UiViewModels, ItemViewModel>(application) {

    private suspend fun requestSuspend(nt: String): DataListBean<ItemBean> {
        val list = DataListBean<ItemBean>()
        for (i in 0..19) {
            list.add(ItemBean(i, "this is $i"))
        }
        return list
    }

    private fun requestObservable(nt: String): Single<DataListBean<ItemBean>> {
        return Single.create<DataListBean<ItemBean>> {
            val list = DataListBean<ItemBean>()
            for (i in 0..19) {
                list.add(ItemBean(i, "this is $i"))
            }
            it.onSuccess(list)
        }
    }

    override fun doLoadData(nt: String) {
        viewModelScope.request({
            val list = requestSuspend(nt)
            val uiModels = UiViewModels()
            for (item in list) {
                val itemViewModel = TestItemViewModel(item, this)
                uiModels.add(itemViewModel)
            }
            uiModels
        }, PageDelegateObserver())
//        requestObservable(nt)
//            .map {
//                val list = UiViewModels()
//                for (item in it) {
//                    val itemViewModel = TestItemViewModel(item, this)
//                    list.add(itemViewModel)
//                }
//                list
//            }.compose(PageTransformer())
//            .subscribe()
    }
}