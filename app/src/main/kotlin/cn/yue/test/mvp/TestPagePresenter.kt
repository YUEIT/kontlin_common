package cn.yue.test.mvp

import cn.yue.base.middle.mvp.IListPresenter
import cn.yue.base.middle.mvp.IListView
import cn.yue.base.middle.net.wrapper.BaseListBean
import cn.yue.test.mode.ItemBean
import io.reactivex.Single
import java.util.*


/**
 * Description :
 * Created by yue on 2022/3/8
 */

class TestPagePresenter(iView: IListView<ItemBean>)
    : IListPresenter<BaseListBean<ItemBean>, ItemBean>(iView) {

    override fun doLoadData(nt: String?) {
        Single.create<BaseListBean<ItemBean>> {
            it.onSuccess(getRequestScope(nt))
        }.compose(PageTransformer())
            .subscribe();
    }

    fun getRequestScope(nt: String?): BaseListBean<ItemBean> {
        val listBean: BaseListBean<ItemBean> = BaseListBean<ItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<ItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = ItemBean()
            testItemBean.name = getItemString(i)
            list.add(testItemBean)
        }
        listBean.mList = list
        return listBean
    }

    fun getItemString(item: Int): String {
        val list = arrayListOf<String>("hehe", "bbbbbbbbbbbbbbbbbbbbbbbbb", "ccccccc", "iii", "oooooooooooooooooooooooo")
        return list[item % list.size]
    }
}