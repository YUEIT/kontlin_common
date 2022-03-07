package cn.yue.test.mvvm

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.common.widget.recyclerview.DiffRefreshAdapter
import cn.yue.base.middle.mvvm.components.BaseListVMFragment
import cn.yue.test.BR
import cn.yue.test.R
import cn.yue.test.mode.ItemBean
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = "/app/testPageVM")
class TestPageVMFragment : BaseListVMFragment<TestPageViewModel, ItemBean>() {

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setCenterTextStr("testPageVM")
    }

    override fun initAdapter(): DiffRefreshAdapter<ItemBean> {
        return object : DiffRefreshAdapter<ItemBean>(mActivity) {

            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_test_other
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemBean) {
                val binding: ViewDataBinding? = DataBindingUtil.bind(holder.itemView)
                if (binding != null) {
                    binding.setVariable(BR.data, itemData)
                    binding.executePendingBindings()
                }
            }

            override fun areItemsTheSame(item1: ItemBean, item2: ItemBean): Boolean {
                return item1 == item2
            }

            override fun areContentsTheSame(oldItem: ItemBean, newItem: ItemBean): Boolean {
                return oldItem == newItem
            }
        }
//        return object : CommonAdapter<ItemBean>(mActivity) {
//
//            override fun getLayoutIdByType(viewType: Int): Int {
//                return R.layout.item_test_other
//            }
//
//            override fun bindData(holder: CommonViewHolder, position: Int, itemData: ItemBean) {
//                val binding: ViewDataBinding? = DataBindingUtil.bind(holder.itemView)
//                if (binding != null) {
//                    binding.setVariable(BR.data, itemData)
//                    binding.executePendingBindings()
//                }
//            }
//        }
    }

    override fun setData(list: MutableList<ItemBean>) {
        getAdapter()?.setList(list)
    }
}