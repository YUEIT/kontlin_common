package cn.yue.base.middle.mvvm

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import cn.yue.base.middle.mvvm.data.BR
import java.util.*

abstract class CommonVMAdapter<T> : CommonAdapter<T> {
    // key为itemType; value为layoutId
    private val typeToLayoutMap: MutableMap<Int, Int> = HashMap()

    // key为data.hashCode; value为ItemViewModel
    private val modelList: MutableMap<Int, ItemViewModel> = LinkedHashMap()

    constructor(context: Context) : super(context)

    constructor(context: Context, list: MutableList<T>) : super(context, list) {
        modelList.clear()
        addAllModel(list)
    }

    abstract fun initItemViewModel(t: T): ItemViewModel

    override fun setList(list: MutableList<T>?) {
        modelList.clear()
        addAllModel(list)
        super.setList(list)
    }

    override fun addList(list: Collection<T>?) {
        addAllModel(list)
        super.addList(list)
    }

    private fun addAllModel(list: Collection<T>?) {
        if (list != null) {
            for (t in list) {
                addModel(t)
            }
        }
    }

    private fun addModel(t: T) {
        val itemViewModel = initItemViewModel(t)
        modelList[t.hashCode()] = itemViewModel
        typeToLayoutMap[itemViewModel.itemType] = itemViewModel.layoutId
    }

    override fun addItem(t: T?) {
        if (t != null) {
            val itemViewModel = initItemViewModel(t)
            modelList[t.hashCode()] = itemViewModel
            typeToLayoutMap[itemViewModel.itemType] = itemViewModel.layoutId
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
        if (getData() != null && getData()!!.size > position && getData()!![position] != null) {
            modelList.remove(getData()!![position].hashCode())
        }
        super.remove(position)
    }

    open fun getVariable(): Int = BR.viewModel

    override fun getViewType(position: Int): Int {
        var itemViewModel:ItemViewModel? = null
        if (getData() != null && getData()!!.size > position && getData()!![position] != null) {
            itemViewModel = modelList[getData()!![position].hashCode()]
        }
        return itemViewModel?.itemType ?: super.getViewType(position)
    }

    override fun getLayoutIdByType(viewType: Int): Int {
        val layoutId = typeToLayoutMap[viewType]
        return layoutId ?: 0
    }

    override fun bindData(holder: CommonViewHolder<T>, position: Int, t: T) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(holder.itemView)
        if (binding != null) {
            binding.setVariable(getVariable(), modelList[t.hashCode()])
            binding.executePendingBindings()
        }
    }
}