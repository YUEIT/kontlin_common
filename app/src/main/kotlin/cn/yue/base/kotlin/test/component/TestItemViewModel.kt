package cn.yue.base.kotlin.test.component

import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel

class TestItemViewModel(var itemBean: TestItemBean, parentViewModel: BaseViewModel)
    : ItemViewModel(parentViewModel) {

    override val itemType: Int = 1

    override val layoutId: Int = R.layout.item_test_other

}