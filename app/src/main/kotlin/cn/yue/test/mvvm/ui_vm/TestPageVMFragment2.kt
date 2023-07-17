package cn.yue.test.mvvm.ui_vm

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.widget.TopBar
import cn.yue.base.widget.recyclerview.CommonViewHolder
import cn.yue.base.widget.recyclerview.DiffRefreshAdapter
import cn.yue.base.mvvm.ItemViewModel
import cn.yue.base.mvvm.components.BaseListVMFragment
import cn.yue.test.BR
import cn.yue.test.R
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testPageVM2")
class TestPageVMFragment2 : BaseListVMFragment<TestUiViewModel, ItemViewModel>() {

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPageVM2")
    }

    override fun initAdapter(): DiffRefreshAdapter<ItemViewModel> {
        return object : DiffRefreshAdapter<ItemViewModel>(mActivity) {

            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test_other2
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemViewModel) {
                val binding: ViewDataBinding? = DataBindingUtil.bind(holder.itemView)
                if (binding != null) {
                    binding.setVariable(BR.viewModel, itemData)
                    binding.executePendingBindings()
                }
            }

            override fun areItemsTheSame(item1: ItemViewModel, item2: ItemViewModel): Boolean {
                return item1 == item2
            }

            override fun areContentsTheSame(oldItem: ItemViewModel, newItem: ItemViewModel): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun setData(list: MutableList<ItemViewModel>) {
        getAdapter()?.setList(list)
    }
}