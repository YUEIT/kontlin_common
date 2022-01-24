package cn.yue.base.kotlin.test.mvvm

import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.kotlin.test.mode.ItemBean
import cn.yue.base.middle.mvvm.CommonVMAdapter
import cn.yue.base.middle.mvvm.ItemViewModel
import cn.yue.base.middle.mvvm.components.BaseListVMFragment
import cn.yue.base.middle.mvvm.data.BR
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testPageVM")
class TestPageVMFragment : BaseListVMFragment<TestPageViewModel, ItemBean>() {

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPageVM")
    }

    override fun initAdapter(): CommonAdapter<ItemBean> {
        return object : CommonVMAdapter<ItemBean>(mActivity) {
            override fun initItemViewModel(itemBean: ItemBean): ItemViewModel {
                return if (itemBean.index % 2 == 0) {
                    TestItemViewModel(itemBean, viewModel)
                } else {
                    TestItemViewModel2(itemBean, viewModel)
                }
            }

            override fun getVariable(): Int {
                return BR.viewModel
            }
        }
    }

    override fun setData(list: MutableList<ItemBean>) {
        getAdapter()?.setList(list)
    }
}