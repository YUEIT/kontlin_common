package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.yue.base.common.utils.code.hasValue
import java.util.*

/**
 * 介绍：通用adapter
 */

abstract class CommonAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private val dividerType = 0
    private val inHeaderType = Integer.MIN_VALUE/2
    private val inFooterType = Integer.MAX_VALUE/2
    /**
     * RecyclerView使用的，真正的Adapter
     */
    private var innerAdapter: RealAdapter

    private var onItemClickListenerBlock: ((position: Int, itemData: T) -> Unit)? = null
    private var onItemLongClickListenerBlock: ((position: Int, itemData: T) -> Boolean)? = null

    private val mHeaderViews = ArrayList<View>()
    private val mFooterViews = ArrayList<View>()

    protected lateinit var context: Context
    private var list: MutableList<T> = arrayListOf()

    constructor(context: Context? = null) {
        if (context != null) {
            this.context = context
        }
        innerAdapter = RealAdapter()
        setAdapter(innerAdapter)
    }

    constructor(context: Context?, list: MutableList<T>) {
        if (context != null) {
            this.context = context
        }
        this.list = list
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

    fun getData(): List<T> = list

    fun setOnItemClickListener(l: ((position: Int, itemData: T) -> Unit)?) {
        this.onItemClickListenerBlock = l
    }

    fun setOnItemLongClickListener(l: ((position: Int, itemData: T) -> Boolean)?) {
        this.onItemLongClickListenerBlock = l
    }

    /**
     * 刷新数据
     */
    fun notifyDataSetChangedReally() {
        innerAdapter.notifyDataSetChanged()
    }

    /**
     * 单条刷新
     */
    fun notifyItemChangedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemChanged(position)
        }
    }

    /**
     * 单条插入
     */
    fun notifyItemInsertedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemInserted(position)
            innerAdapter.notifyItemRangeChanged(position, getListSize() - position)
        }
    }

    /**
     * 单条删除
     */
    fun notifyItemRemovedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemRemoved(position)
            innerAdapter.notifyItemRangeChanged(position, getListSize() - position)
        }
    }

    /**
     * 单条移动
     */
    fun notifyItemMovedReally(fromPosition: Int, toPosition: Int) {
        if (fromPosition > -1 && fromPosition < innerAdapter.itemCount
                && toPosition > -1 && toPosition < innerAdapter.itemCount) {
            innerAdapter.notifyItemMoved(fromPosition, toPosition)
            innerAdapter.notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1)
        }
    }

    open fun getListSize(): Int {
        return list.size
    }

    fun getList(): MutableList<T> {
        return list
    }

    open fun clear() {
        if (list.hasValue()) {
            list.clear()
        }
        notifyDataSetChanged()
    }

    /**
     * 设置数据
     * @param list
     */
    open fun setList(list: List<T>?) {
        if (list != null) {
            this.list = list.toMutableList()
            notifyDataSetChanged()
        }
    }

    open fun addList(list: Collection<T>?) {
        if (list != null) {
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    /**
     * 添加单条数据
     * @param t
     */
    open fun addItem(t: T?) {
        if (null != t) {
            list.add(t)
        }
        notifyDataSetChanged()
    }

    open fun remove(t: T?) {
        if (null != t) {
            list.remove(t)
        }
        notifyDataSetChanged()
    }

    open fun remove(position: Int) {
        if (position > -1 && list.size > position) {
            list.removeAt(position)
            innerAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 设置adapter
     */
    private fun setAdapter(adapter: RealAdapter) {
        this.innerAdapter = adapter
        notifyItemRangeRemoved(getHeaderViewsCount(), innerAdapter.itemCount)
        //            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
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
                    v.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
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
                ViewHolder(mHeaderViews[viewType - inHeaderType])
            }
            viewType >= inFooterType -> {
                ViewHolder(mFooterViews[viewType - inFooterType])
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


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    protected fun setVisible(view: View?, visible: Boolean) {
        if (null != view) {
            if (visible) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

    }

    /**
     * 获取具体View
     */
    fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): CommonViewHolder {
        return CommonViewHolder.getHolder(context, getLayoutIdByType(viewType), parent)
    }

    /**
     * 获取当前Item数据
     */
    open fun getItem(position: Int): T? {
        return if (position < list.size && position >= 0) {
            list[position]
        } else null
    }

    /**
     * 还是兼容下DiffUtil
     */
    open fun changeItem(holder: CommonViewHolder, position: Int, t: T?, bundle: Bundle) {
    }

    /**
     * 真实的Adapter
    */
    private inner class RealAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return getViewHolder(parent.context, parent, viewType)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
            if (holder is CommonViewHolder) {
                if (payloads.isEmpty()) {
                    val t = getItem(position)
                    setItemVisible(holder.itemView, t != null)
                    t?: return
                    onItemClickListenerBlock?.let { block ->
                        holder.itemView.setOnClickListener {
                            block(position, t)
                        }
                    }
                    onItemLongClickListenerBlock?.let { block ->
                        holder.itemView.setOnLongClickListener {
                            block(position, t)
                        }
                    }
                    bindData(holder, position, t)
                } else {
                    val t = getItem(position)
                    changeItem(holder, position, t, payloads[0] as Bundle)
                }
            } else {
                throw RuntimeException("Holder must be not null !")
            }
        }

        override fun getItemCount(): Int {
            return getListSize()
        }

        override fun getItemViewType(position: Int): Int {
            return getInnerViewType(position)
        }
    }

    fun getInnerViewType(position: Int): Int {
        return getViewType(position)
    }

    open fun getViewType(position: Int): Int {
        return 0
    }

    abstract fun getLayoutIdByType(viewType: Int): Int

    abstract fun bindData(holder: CommonViewHolder, position: Int, itemData: T)
}



