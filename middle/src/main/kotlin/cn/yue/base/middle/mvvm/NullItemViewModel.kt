package cn.yue.base.middle.mvvm

import cn.yue.base.middle.R

/**
 * Description :
 * Created by yue on 2020/11/5
 */
class NullItemViewModel(parentViewModel: BaseViewModel): ItemViewModel(parentViewModel) {

    override fun getItemType(): Int {
        return 0
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_space_binding
    }
}