package cn.yue.base.kotlin.test.mvvm

import androidx.databinding.ObservableField
import cn.yue.base.kotlin.test.R
import cn.yue.base.kotlin.test.mode.ItemBean

import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel

class TestItemViewModel2(private val itemBean: ItemBean, parentViewModel: BaseViewModel)
    : ItemViewModel(parentViewModel) {

    override fun getLayoutId(): Int {
        return R.layout.item_test_other2
    }

    var nameField = ObservableField<String>()

    init {
        nameField.set(itemBean.name)
    }


}