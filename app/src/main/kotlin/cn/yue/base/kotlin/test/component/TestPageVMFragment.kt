package cn.yue.base.kotlin.test.component

import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.middle.mvvm.CommonVMAdapter
import cn.yue.base.middle.mvvm.ItemViewModel
import cn.yue.base.middle.mvvm.components.BasePageVMFragment
import cn.yue.base.middle.mvvm.data.BR
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testPageVM")
class TestPageVMFragment : BasePageVMFragment<TestPageViewModel, TestItemBean>() {

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPageVM")
    }

    override fun initAdapter(): CommonAdapter<TestItemBean>? {
        return object : CommonVMAdapter<TestItemBean>(mActivity) {
            override fun initItemViewModel(itemBean: TestItemBean): ItemViewModel {
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
}