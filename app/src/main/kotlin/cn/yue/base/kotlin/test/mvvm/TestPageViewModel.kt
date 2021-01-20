package cn.yue.base.kotlin.test.mvvm

import android.app.Application
import cn.yue.base.kotlin.test.mode.ItemBean
import cn.yue.base.middle.mvvm.PageViewModel
import cn.yue.base.middle.net.wrapper.BaseListBean
import java.util.*

class TestPageViewModel(application: Application) : PageViewModel<ItemBean>(application) {

    override suspend fun getRequestScope(nt: String): BaseListBean<ItemBean>? {
        val listBean = BaseListBean<ItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<ItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = ItemBean()
            testItemBean.index = i
            testItemBean.name = "this is $i"
            list.add(testItemBean)
        }
        listBean.dataList = list
        return listBean
    }
}