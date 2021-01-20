package cn.yue.base.middle.mvvm

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import java.util.*
/**
 * Description :
 * Created by yue on 2020/8/8
 */
abstract class CommonVMAdapter<T> : CommonAdapter<T> {
    // key为itemType; value为layoutId
    private val typeToLayoutMap: MutableMap<Int, Int> = HashMap()

    // key为data.hashCode; value为ItemViewModel
    private val modelList: MutableMap<Int, ItemViewModel> = LinkedHashMap()

    constructor(context: Context? = null) : super(context)

    constructor(context: Context?, list: MutableList<T>) : super(context, list) {
        modelList.clear()
        addAllModel(list)
    }

    abstract fun initItemViewModel(t: T): ItemViewModel

    override fun setList(list: List<T>?) {
        if (list != null) {
            modelList.clear()
            addAllModel(list)
            super.setList(list)
        }
    }

    override fun addList(list: Collection<T>?) {
        if (list != null) {
            addAllModel(list)
            super.addList(list)
        }
    }

    private fun addAllModel(list: Collection<T>) {
        for (t in list) {
            addModel(t)
        }
    }

    private fun addModel(t: T) {
        val itemViewModel = initItemViewModel(t)
        modelList[t.hashCode()] = itemViewModel
        typeToLayoutMap[itemViewModel.getItemType()] = itemViewModel.getLayoutId()
    }

    override fun addItem(t: T?) {
        if (t != null) {
            val itemViewModel = initItemViewModel(t)
            modelList[t.hashCode()] = itemViewModel
            typeToLayoutMap[itemViewModel.getItemType()] = itemViewModel.getLayoutId()
        }
        super.addItem(t)
    }

    override fun clear() {
        modelList.clear()
        typeToLayoutMap.clear()
        super.clear()
    }

    override fun remove(t: T?) {
        if (t != null) {
            modelList.remove(t.hashCode())
        }
        super.remove(t)
    }

    override fun remove(position: Int) {
        if (getData().size > position && getData()[position] != null) {
            modelList.remove(getData()[position].hashCode())
        }
        super.remove(position)
    }

    abstract fun getVariable(): Int

    override fun getViewType(position: Int): Int {
        var itemViewModel:ItemViewModel? = null
        if (getData().size > position && getData()[position] != null) {
            itemViewModel = modelList[getData()[position].hashCode()]
        }
        return itemViewModel?.getItemType() ?: super.getViewType(position)
    }

    override fun getLayoutIdByType(viewType: Int): Int {
        val layoutId = typeToLayoutMap[viewType]
        return layoutId ?: 0
    }

    override fun bindData(holder: CommonViewHolder, position: Int, t: T) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(holder.itemView)
        if (binding != null) {
            binding.setVariable(getVariable(), modelList[t.hashCode()])
            binding.executePendingBindings()
        }
    }
}