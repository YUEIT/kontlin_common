package cn.yue.test.mvvm.ui_vm

import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel
import cn.yue.test.mode.ItemBean

class TestItemViewModel(var itemBean: ItemBean, parentViewModel: BaseViewModel)
    : ItemViewModel(parentViewModel) {

}