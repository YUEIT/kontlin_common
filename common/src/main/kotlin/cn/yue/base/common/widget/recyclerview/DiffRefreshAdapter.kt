package cn.yue.base.common.widget.recyclerview

import android.content.Context
import androidx.recyclerview.widget.*
import java.util.*

/**
 * 通过DiffUtils改过的，比较两个数据源间的差异，实现最小程度的刷新
 * （数据源必须不一样）
 */
abstract class DiffRefreshAdapter<T> : CommonAdapter<T> {

    constructor(context: Context? = null): super(context)

    constructor(context: Context?, list: MutableList<T>): super(context, list)

    private var mDiffCallback = MessageDiffCallBack()

    fun setDataCollection(mData: List<T>?) {
        val newList = mData ?: ArrayList()
        if ((this.getList().isEmpty() && newList.isNotEmpty()) || (this.getList().isNotEmpty() && newList.isEmpty())) {
            this.getList().clear()
            this.getList().addAll(newList)
            notifyDataSetChanged()
        } else {
            this.mDiffCallback.setNewList(newList)
            val diffResult = DiffUtil.calculateDiff(this.mDiffCallback, false)
            this.getList().clear()
            this.getList().addAll(newList)
            diffResult.dispatchUpdatesTo(object : ListUpdateCallback {
                override fun onInserted(position: Int, count: Int) {
                    notifyItemInsertedReally(position)
                }

                override fun onRemoved(position: Int, count: Int) {
                    notifyItemRemovedReally(position)
                }

                override fun onMoved(fromPosition: Int, toPosition: Int) {
                    notifyItemMovedReally(fromPosition, toPosition)
                }

                override fun onChanged(position: Int, count: Int, payload: Any?) {
                    notifyItemChangedReally(position)
                }
            })
        }
    }

    private inner class MessageDiffCallBack : DiffUtil.Callback() {
        private var newList: List<T>? = null
        override fun getOldListSize(): Int {
            return getList().size
        }

        override fun getNewListSize(): Int {
            return newList?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (getList().size > oldItemPosition && newList != null && newList!!.size > newItemPosition) {
                return this@DiffRefreshAdapter.areItemsTheSame(getList()[oldItemPosition], newList!![newItemPosition])
            }
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (getList().size > oldItemPosition && newList != null && newList!!.size > newItemPosition) {
                return this@DiffRefreshAdapter.areContentsTheSame(getList()[oldItemPosition], newList!![newItemPosition])
            }
            return true
        }

        fun setNewList(newList: List<T>?) {
            this.newList = newList
        }
    }

    abstract fun areItemsTheSame(item1: T, item2: T): Boolean

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    override fun setList(list: List<T>?) {
        setDataCollection(list)
    }
}



