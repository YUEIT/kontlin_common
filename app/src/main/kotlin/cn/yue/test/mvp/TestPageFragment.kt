package cn.yue.test.mvp

import androidx.recyclerview.widget.RecyclerView
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.common.widget.recyclerview.SwipeLayoutManager
import cn.yue.base.middle.mvp.components.BaseListFragment
import cn.yue.test.R
import cn.yue.test.mode.ItemBean
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2019/6/11
 */
@Route(path = "/app/testPage")
class TestPageFragment : BaseListFragment<ItemBean>() {

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

    override fun getAdapter(): CommonAdapter<ItemBean> {
        return object : CommonAdapter<ItemBean>(mActivity) {
            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemBean) {
                holder.setText(R.id.testTV, itemData.name)
            }

        }
    }

    val presenter = TestPagePresenter(this)

    override fun loadData(isRefresh: Boolean) {
        presenter.loadData(isRefresh)
    }
}