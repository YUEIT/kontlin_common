package cn.yue.base.middle.mvvm

/**
 * Description :
 * Created by yue on 2020/11/5
 */
class NullItemViewModel(parentViewModel: BaseViewModel): ItemViewModel(parentViewModel) {

    override val itemType: Int = 0
    override val layoutId: Int = cn.yue.base.middle.R.layout.layout_space_binding
}