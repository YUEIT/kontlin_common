package cn.yue.base.middle.components.binding

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.components.BaseListFragment
import cn.yue.base.middle.net.wrapper.BaseListBean
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BasePageBindFragment<DB : ViewDataBinding, S> : BaseListFragment<BaseListBean<S>, S>() {

    override fun getAdapter(): CommonAdapter<S> {
        return object : CommonAdapter<S>(mActivity, ArrayList()) {
            override fun getViewType(position: Int): Int {
                return getItemType(position)
            }

            override fun getLayoutIdByType(viewType: Int): Int {
                return getItemLayoutId(viewType)
            }

            override fun bindData(holder: CommonViewHolder, position: Int, itemData: S) {
                val binding = DataBindingUtil.bind<DB>(holder.itemView)!!
                bindItemData(binding, position, itemData)
            }
        }
    }

    override fun bindItemData(holder: CommonViewHolder, position: Int, itemData: S) {
        throw IllegalStateException("this function is deprecated")
    }

    protected abstract fun bindItemData(binding: DB, position: Int, itemData: S)
}