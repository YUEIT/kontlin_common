package cn.yue.base.kotlin.test.component

import androidx.databinding.ObservableField
import cn.yue.base.kotlin.test.R
import cn.yue.base.middle.mvvm.BaseViewModel
import cn.yue.base.middle.mvvm.ItemViewModel

class TestItemViewModel2(private val itemBean: TestItemBean, parentViewModel: BaseViewModel) : ItemViewModel(parentViewModel) {

    override val itemType: Int = 2

    override val layoutId: Int = R.layout.item_test_other2

    var nameField = ObservableField<String>()

    init {
        nameField.set(itemBean.name)
    }
}