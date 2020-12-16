package cn.yue.base.kotlin.test.component

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.common.widget.recyclerview.SwipeLayoutManager
import cn.yue.base.kotlin.test.R

import cn.yue.base.middle.components.BaseListFragment
import cn.yue.base.middle.net.wrapper.BaseListBean
import com.alibaba.android.arouter.facade.annotation.Route
import kotlinx.android.synthetic.main.item_test.view.*
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/11
 */
@Route(path = "/app/testPullList")
class TestListFragment : BaseListFragment<BaseListBean<TestItemBean>, TestItemBean>() {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPullList")
    }

    override fun initRecyclerView(baseRV: RecyclerView) {
        super.initRecyclerView(baseRV)
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return SwipeLayoutManager()
    }

    override fun getLayoutId(): Int {
        return R.layout.test_fragment_base_pull_page
    }

    override fun getItemType(position: Int): Int {
        return super.getItemType(position)
    }

    override fun getItemLayoutId(viewType: Int): Int {
       return R.layout.item_test
    }

    override fun bindItemData(holder: CommonViewHolder, position: Int, testItemBean: TestItemBean) {
        holder.itemView.testTV.text = testItemBean.name
    }

    override suspend fun getRequestScope(nt: String?): BaseListBean<TestItemBean>? {
        val listBean: BaseListBean<TestItemBean> = BaseListBean<TestItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<TestItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = TestItemBean()
            testItemBean.name = getItemString(i)
            list.add(testItemBean)
        }
        listBean.pageList = list
        return listBean
    }

    fun getItemString(item: Int): String {
        val list = arrayListOf<String>("hehe", "bbbbbbbbbbbbbbbbbbbbbbbbb", "ccccccc", "iii", "oooooooooooooooooooooooo")
        return list[item % list.size]
    }
}