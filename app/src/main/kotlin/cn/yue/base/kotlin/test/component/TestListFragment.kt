package cn.yue.base.kotlin.test.component

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.components.BaseListFragment
import cn.yue.base.middle.net.wrapper.BaseListBean
import com.alibaba.android.arouter.facade.annotation.Route
import io.reactivex.Single
import java.util.*

/**
 * Description :
 * Created by yue on 2019/6/11
 */
@Route(path = "/app/testPullList")
class TestListFragment : BaseListFragment<BaseListBean<TestItemBean>, TestItemBean>() {
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initTest()
    }

    override fun getLayoutId(): Int {
        return R.layout.test_fragment_base_pull_page
    }

    override fun getItemType(position: Int): Int {
        return if (position == 1) {
            1
        } else super.getItemType(position)
    }

    override fun getItemLayoutId(viewType: Int): Int {
        return if (viewType == 1) {
            R.layout.item_test_recyclerview
        } else R.layout.item_test
    }

    override fun bindItemData(holder: CommonViewHolder<TestItemBean>?, position: Int, testItemBean: TestItemBean) {

    }

    private val testList: MutableList<String> = ArrayList()
    private fun initTest() {
        for (i in 0..9) {
            testList.add("ssssa$i")
        }
    }

    override fun getRequestSingle(nt: String?): Single<BaseListBean<TestItemBean>>? {
        val listBean: BaseListBean<TestItemBean> = BaseListBean<TestItemBean>()
        listBean.mPageSize = 20
        listBean.mTotal = 22
        val list: MutableList<TestItemBean> = ArrayList()
        for (i in 0..19) {
            val testItemBean = TestItemBean()
            testItemBean.name = "this is $i"
            list.add(testItemBean)
        }
        listBean.pageList = list
        return Single.just<BaseListBean<TestItemBean>>(listBean)
    }
}