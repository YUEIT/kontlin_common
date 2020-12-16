package cn.yue.base.kotlin.test.component

import androidx.databinding.ObservableField

import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel

class TestItemViewModel2(private val itemBean: TestItemBean, parentViewModel: BaseViewModel)
    : ItemViewModel(parentViewModel) {

    override val layoutId: Int = cn.yue.base.kotlin.test.R.layout.item_test_other2

    var nameField = ObservableField<String>()

    init {
        nameField.set(itemBean.name)
    }
}