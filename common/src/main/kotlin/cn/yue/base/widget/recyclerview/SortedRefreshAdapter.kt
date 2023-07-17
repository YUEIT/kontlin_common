package cn.yue.base.widget.recyclerview

/**
 * 介绍：用SortedList修改过的支持自动刷新和数据去重
 * （注意这个并不适合于数据移动，因为SortedList有自动排序的功能）
 */

import android.content.Context
import androidx.recyclerview.widget.SortedList
import java.lang.reflect.ParameterizedType

abstract class SortedRefreshAdapter<T>(context: Context? = null) : CommonAdapter<T>(context) {

    /**
     * 先初始化，然后才能setAdapter
     */
    private var list: SortedList<T> = SortedList(getType(), AutoSortedListCallback())

    private fun getType(): Class<T> {
        val clazz: Class<T>?
        val type = javaClass.genericSuperclass
        clazz = if (type is ParameterizedType) {
            type.actualTypeArguments[0] as Class<T>
        } else {
            throw IllegalArgumentException("泛型获取错误")
        }
        return clazz
    }
    
    /**
     * 最好直接用这个Callback，自定义需要方法中回调mInnerAdapter的notify方法
     * 使用SortedListAdapterCallback时需要覆盖onInserted、onRemoved、onMoved、onChanged
     * SortedListAdapterCallback回调不是包装类的notify如果有header/footer会刷新出错
     * 会需要重写compare方法（比较排序）、areContentsTheSame（比较内容是否相等）、areItemsTheSame（比较是否为同一项）
     */
    inner class AutoSortedListCallback : SortedList.Callback<T>() {
        override fun onInserted(position: Int, count: Int) {
            notifyItemInsertedReally(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRemovedReally(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMovedReally(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemChangedReally(position, count)
        }

        override fun compare(o1: T, o2: T): Int {
            return this@SortedRefreshAdapter.compare(o1, o2)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return this@SortedRefreshAdapter.areContentsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(item1: T, item2: T): Boolean {
            return this@SortedRefreshAdapter.areItemsTheSame(item1, item2)
        }

    }

    abstract fun compare(item1: T, item2: T): Int

    abstract fun areItemsTheSame(item1: T, item2: T): Boolean

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    override fun clear() {
        if (list.size() > 0) {
            list.clear()
        }
    }

    /**
     * 设置数据
     */
    fun setList(list: SortedList<T>) {
        this.list = list
    }

    /**
     * 批量添加数据
     * Collection 必须以升序排列，不管sortedList以什么规则排序的
     * @param list
     */
    override fun addList(list: Collection<T>?) {
        if (list.isNullOrEmpty()) {
            return
        }
        this.list.addAll(list)
    }

    /**
     * 批量插入数据
     * @param list
     */
    fun addAll(list: List<T>) {
        this.list.beginBatchedUpdates()
        try {
            for (t in list) {
                this.list.add(t)
            }
        } finally {
            this.list.endBatchedUpdates()
        }
    }

    /**
     * 添加单条数据
     * @param t
     */
    override fun addItem(t: T?) {
        if (null != t) {
            list.add(t)
        }
    }

    override fun remove(t: T?) {
        if (null != t) {
            list.remove(t)
        }
    }

    override fun remove(position: Int) {
        if (position > -1 && list.size() > position) {
            list.removeItemAt(position)
        }
    }

    /**
     * 更新位置index的数据，更新后会根据排序规则调用move
     */
    fun updateItemAt(index: Int, t: T) {
        if (null != t && index > 0) {
            list.updateItemAt(index, t)
        }
    }

    fun getSortedList(): SortedList<T> {
        return list
    }

    override fun getItem(position: Int): T? {
        return if (position < list.size() && position >= 0) {
            list.get(position)
        } else null
    }

    /**
     * 返回具体位置
     * 源码中会调用 mCallback.compare(myItem, item);
     * 以排序规则来判断位置，如果排序比较项和areItemsTheSame的比较项不同，不要使用这个方法
     */
    fun indexOf(t: T): Int {
        return list.indexOf(t)
    }

    override fun getListSize(): Int {
        return list.size()
    }
}
