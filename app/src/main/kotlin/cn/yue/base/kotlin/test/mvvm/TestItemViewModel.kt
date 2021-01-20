package cn.yue.base.kotlin.test.mvvm

import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.mode.ItemBean
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel

class TestItemViewModel(var itemBean: ItemBean, parentViewModel: BaseViewModel)
    : ItemViewModel(parentViewModel) {

    override fun getLayoutId(): Int {
        return R.layout.item_test_other
    }

}