package cn.yue.base.common.widget.recyclerview

/**
 * 介绍：用DiffUtil和SortedList修改过的支持自动刷新和数据去重
 * （注意这个并不适合于数据移动，因为SortedList有自动排序的功能）
 */

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.util.*

abstract class AutoRefreshAdapter<T>
    : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private val dividerType = 0
    private val inHeaderType = Integer.MIN_VALUE/2
    private val inFooterType = Integer.MAX_VALUE/2

    /**
     * RecyclerView使用的，真正的Adapter
     */
    private var innerAdapter: RealAdapter

    private val mHeaderViews = ArrayList<View>()
    private val mFooterViews = ArrayList<View>()

    private var onItemClickListenerBlock: (() -> Unit)? = null
    private var onItemLongClickListenerBlock: (() -> Unit)? = null

    protected lateinit var context: Context

    /**
     * 先初始化，然后才能setAdapter
     */
    private lateinit var list: SortedList<T>

    /**
     * 最好直接用这个Callback，自定义需要方法中回调mInnerAdapter的notify方法
     * 使用SortedListAdapterCallback时需要覆盖onInserted、onRemoved、onMoved、onChanged
     * SortedListAdapterCallback回调不是包装类的notify如果有header/footer会刷新出错
     * 继承这个Callback，会需要重写compare方法（比较排序）、areContentsTheSame（比较内容是否相等）、areItemsTheSame（比较是否为同一项）
     */
    abstract inner class AutoSortedListCallback<T2> : SortedList.Callback<T2>() {
        override fun onInserted(position: Int, count: Int) {
            notifyItemInsertedReally(position)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRemovedReally(position)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMovedReally(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemChangedReally(position)
        }

    }

    constructor(context: Context? = null) {
        if (context != null) {
            this.context = context
        }
        innerAdapter = RealAdapter()
        setAdapter(innerAdapter)
    }

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    /**
     * 返回最后一个FooterView
     */
    fun getFooterView(): View? {
        return if (getFooterViewsCount() > 0) mFooterViews[getFooterViewsCount() - 1] else null
    }

    fun getFooterView(position: Int): View? {
        return if (getFooterViewsCount() > position) mFooterViews[position] else null
    }

    /**
     * 返回第一个HeaderView
     */
    fun getHeaderView(): View? {
        return if (getHeaderViewsCount() > 0) mHeaderViews[0] else null
    }

    fun getHeaderView(position: Int): View? {
        return if (getHeaderViewsCount() > position) mHeaderViews[position] else null
    }

    fun getHeaderViewsCount(): Int = mHeaderViews.size

    fun getFooterViewsCount(): Int = mFooterViews.size

    fun getReallyItemCount(): Int = innerAdapter.itemCount

    /**
     * 刷新数据
     */
    fun notifyDataSetChangedReally() {
        innerAdapter.notifyDataSetChanged()
    }

    /**
     * 单条刷新
     * @param position
     */
    fun notifyItemChangedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemChanged(position)
        }
    }

    /**
     * 单条插入
     * @param position
     */
    fun notifyItemInsertedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemInserted(position)
            innerAdapter.notifyItemRangeChanged(position, list.size() - position)
        }
    }

    /**
     * 单条删除
     */
    fun notifyItemRemovedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemRemoved(position)
            innerAdapter.notifyItemRangeChanged(position, list.size() - position)
        }
    }


    /**
     * 单条移动
     */
    fun notifyItemMovedReally(fromPosition: Int, toPosition: Int) {
        if (fromPosition > -1 && fromPosition < innerAdapter.itemCount
                && toPosition > -1 && toPosition < innerAdapter.itemCount) {
            innerAdapter.notifyItemMoved(fromPosition, toPosition)
            innerAdapter.notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition))
        }
    }

    fun clear() {
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
    fun addList(list: Collection<T>) {
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
    fun addItem(t: T) {
        if (null != t) {
            list.add(t)
        }
    }

    fun removeItem(t: T) {
        if (null != t) {
            list.remove(t)
        }
    }

    fun removeItemAt(index: Int) {
        if (index > 0) {
            list.removeItemAt(index)
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

    /**
     * 设置adapter
     */
    private fun setAdapter(adapter: RealAdapter) {
        this.innerAdapter = adapter
        notifyItemRangeRemoved(getHeaderViewsCount(), innerAdapter.itemCount)
        innerAdapter.registerAdapterDataObserver(mDataObserver)
        notifyItemRangeInserted(getHeaderViewsCount(), innerAdapter.itemCount)
    }

    fun addHeaderView(header: View) {
        header.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mHeaderViews.add(header)
        this.notifyDataSetChanged()
    }

    fun addFooterView(footer: View) {
        mFooterViews.add(footer)
        this.notifyDataSetChanged()
    }

    fun removeHeaderView(view: View) {
        mHeaderViews.remove(view)
        this.notifyDataSetChanged()
    }

    fun removeFooterView(view: View) {
        mFooterViews.remove(view)
        this.notifyDataSetChanged()
    }

    /**
     * 隐藏或展示Item
     */
    protected fun setItemVisible(v: View?, visible: Boolean) {
        if (null != v) {
            if (visible) {
                if (null == v.layoutParams) {
                    v.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                }
                v.visibility = View.VISIBLE
            } else {
                if (null == v.layoutParams) {
                    v.layoutParams = RecyclerView.LayoutParams(-1, 1)
                }
                v.visibility = View.GONE
            }
        }
    }

    fun isHeader(position: Int): Boolean {
        return getHeaderViewsCount() > position
    }

    fun isFooter(position: Int): Boolean {
        return getFooterViewsCount() > 0 && position >= getHeaderViewsCount() + itemCount
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        if (!this::context.isInitialized) {
            context = recyclerView.context
        }
        innerAdapter.onAttachedToRecyclerView(recyclerView)
        //为了兼容GridLayout
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position < getHeaderViewsCount()) {
                        return layoutManager.spanCount
                    } else if (position >= getHeaderViewsCount() + innerAdapter.itemCount) {
                        return layoutManager.spanCount
                    }
                    return spanSizeLookup?.getSpanSize(position) ?: 1
                }
            }
            layoutManager.spanCount = layoutManager.spanCount
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            viewType in inHeaderType..-1 -> {
                CommonAdapter.ViewHolder(mHeaderViews[viewType - inHeaderType])
            }
            viewType >= inFooterType -> {
                CommonAdapter.ViewHolder(mFooterViews[viewType - inFooterType])
            }
            else -> {
                innerAdapter.onCreateViewHolder(parent, viewType)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        val headerViewsCountCount = getHeaderViewsCount()
        if (position >= headerViewsCountCount && position < headerViewsCountCount + innerAdapter.itemCount) {
            innerAdapter.onBindViewHolder(holder, position - headerViewsCountCount, payloads)
        } else {
            val layoutParams = holder.itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    override fun getItemCount(): Int {
        return getHeaderViewsCount() + getFooterViewsCount() + innerAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        val innerCount: Int = innerAdapter.itemCount
        val headerViewsCountCount = getHeaderViewsCount()
        // min/2 ~ 0 header   0 ~ max/2 inner  max/2 ~ max  footer
        return if (position < headerViewsCountCount) {
            inHeaderType + position
        } else if (headerViewsCountCount <= position && position < headerViewsCountCount + innerCount) {
            val innerItemViewType: Int = innerAdapter.getItemViewType(position - headerViewsCountCount)
            require(innerItemViewType < Int.MAX_VALUE / 2) {
                "your adapter's return value of getViewTypeCount() must < Integer.MAX_VALUE / 2"
            }
            innerItemViewType
        } else {
            inFooterType + position - headerViewsCountCount - innerCount
        }
    }

    fun getData(): SortedList<T> {
        return list
    }

    fun remove(position: Int) {
        if (position > -1 && list.size() > position) {
            list.removeItemAt(position)
            innerAdapter.notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): T? {
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
    
    fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CommonViewHolder.getHolder(context, getLayoutIdByType(viewType), parent)
    }
    
    abstract fun bindData(holder: CommonViewHolder, position: Int, t: T)

    open fun changeItem(holder: CommonViewHolder, position: Int, t: T?, bundle: Bundle){
    }

    open fun getViewType(position: Int): Int {
        return 0
    }

    abstract fun getLayoutIdByType(viewType: Int): Int

    /**
     * 真实的Adapter
    */
    private inner class RealAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return getViewHolder(parent.context, parent, viewType)
        }
        
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
            if (holder is CommonViewHolder) {
                if (payloads.isEmpty()) {
                    holder.setOnItemClickListener(onItemClickListenerBlock)
                    holder.setOnItemLongClickListener(onItemLongClickListenerBlock)
                    val t = getItem(position)
                    setItemVisible(holder.itemView, t != null)
                    if (t != null) {
                        bindData(holder, position, t)
                    }
                } else {
                    val t = getItem(position)
                    changeItem(holder, position, t, payloads[0] as Bundle)
                }
            } else {
                throw RuntimeException("Holder must be not null !")
            }
        }

        override fun getItemCount(): Int {
            return list.size()
        }

        override fun getItemViewType(position: Int): Int {
            return getInnerViewType(position)
        }
    }
    
    fun getInnerViewType(position: Int): Int {
        return getViewType(position)
    }
}
