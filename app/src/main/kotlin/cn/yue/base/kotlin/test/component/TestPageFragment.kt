package cn.yue.base.kotlin.test.component

import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.common.widget.recyclerview.SwipeLayoutManager
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.mode.ItemBean
import cn.yue.base.middle.components.BasePageFragment
import cn.yue.base.middle.net.wrapper.BaseListBean
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/11
 */
@Route(path = "/app/testPage")
class TestPageFragment : BasePageFragment<ItemBean>() {

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPage")
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return SwipeLayoutManager()
    }

    override fun getLayoutId(): Int {
        return R.layout.test_fragment_base_pull_page
    }

    override fun getItemLayoutId(viewType: Int): Int {
       return R.layout.item_test
    }

    override fun bindItemData(holder: CommonViewHolder, position: Int, itemData: ItemBean) {
        holder.setText(R.id.testTV, itemData.name)
    }

    override suspend fun getRequestScope(nt: String?): BaseListBean<ItemBean>? {
        val listBean: BaseListBean<ItemBean> = BaseListBean<ItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<ItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = ItemBean()
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