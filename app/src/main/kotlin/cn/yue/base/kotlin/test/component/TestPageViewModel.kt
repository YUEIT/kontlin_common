package cn.yue.base.kotlin.test.component

import android.app.Application
import cn.yue.base.middle.mvvm.PageViewModel
import cn.yue.base.middle.net.wrapper.BaseListBean
import io.reactivex.Single
import java.util.*

class TestPageViewModel(application: Application) : PageViewModel<TestItemBean>(application) {
    override fun getRequestSingle(nt: String?): Single<BaseListBean<TestItemBean>>? {
        val listBean = BaseListBean<TestItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<TestItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = TestItemBean()
            testItemBean.index = i
            testItemBean.name = "this is $i"
            list.add(testItemBean)
        }
        listBean.dataList = list
        return Single.just<BaseListBean<TestItemBean>>(listBean)
    }
}