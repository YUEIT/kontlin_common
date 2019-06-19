package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * 介绍：通用adapter
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/11/14.
 */

abstract class CommonAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * RecyclerView使用的，真正的Adapter
     */
    private var innerAdapter: RealAdapter
    private var onItemClickListener: CommonViewHolder.OnItemClickListener<T>? = null
    private var onItemLongClickListener: CommonViewHolder.OnItemLongClickListener<T>? = null

    private var onItemClickListenerBlock: ((position: Int, t: T) -> Unit)? = null
    private var onItemLongClickListenerBlock: ((position: Int, t: T) -> Unit)? = null

    private val mHeaderViews = ArrayList<View>()
    private val mFooterViews = ArrayList<View>()

    protected var context: Context
    private var list: MutableList<T>? = null
    protected var inflater: LayoutInflater

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
     * 返回第一个FooterView
     * @return
     */
    val footerView: View?
        get() = if (getFooterViewsCount() > 0) mFooterViews[getFooterViewsCount() - 1] else null

    /**
     * 返回第一个HeaderView
     * @return
     */
    fun getHeaderView(): View? = if (getHeaderViewsCount() > 0) mHeaderViews[0] else null

    fun getHeaderViewsCount(): Int = mHeaderViews.size

    fun getFooterViewsCount(): Int = mFooterViews.size

    fun getReallyItemCount(): Int = innerAdapter.itemCount

    fun getData(): List<T>? = list


    /**
     *
     * @param context
     */
    constructor(context: Context) {
        this.context = context
        inflater = LayoutInflater.from(context)
        innerAdapter = RealAdapter()
        setAdapter(innerAdapter)
    }

    constructor(context: Context, list: MutableList<T>) {
        this.context = context
        this.list = list
        inflater = LayoutInflater.from(context)
        innerAdapter = RealAdapter()
        setAdapter(innerAdapter)
    }

    fun setOnItemClickListener(l: CommonViewHolder.OnItemClickListener<T>) {
        this.onItemClickListener = l
    }

    fun setOnItemLongClickListener(l: CommonViewHolder.OnItemLongClickListener<T>) {
        this.onItemLongClickListener = l
    }

    fun setOnItemClickListener(l: ((position: Int, t: T) -> Unit)?) {
        this.onItemClickListenerBlock = l
    }

    fun setOnItemLongClickListener(l: ((position: Int, t: T) -> Unit)?) {
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
            innerAdapter.notifyItemRangeChanged(position, list!!.size - position)
        }
    }

    /**
     * 单条删除
     * @param position
     */
    fun notifyItemRemovedReally(position: Int) {
        if (position > -1 && position < innerAdapter.itemCount) {
            innerAdapter.notifyItemRemoved(position)
            innerAdapter.notifyItemRangeChanged(position, list!!.size - position)
        }
    }


    /**
     * 单条移动
     * @param fromPosition
     * @param toPosition
     */
    fun notifyItemMovedReally(fromPosition: Int, toPosition: Int) {
        if (fromPosition > -1 && fromPosition < innerAdapter.itemCount
                && toPosition > -1 && toPosition < innerAdapter.itemCount) {
            innerAdapter.notifyItemMoved(fromPosition, toPosition)
            innerAdapter.notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1)
        }
    }


    fun getList(): List<T>? {
        return list
    }

    fun clear() {
        if (list != null && list!!.size > 0) {
            list!!.clear()
        }
    }

    /**
     * 设置数据
     * @param list
     */
    fun setList(list: MutableList<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addList(list: Collection<T>?) {
        if (null != list) {
            if (null != this.list) {
                this.list!!.addAll(list)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * 添加单条数据
     * @param t
     */
    fun addItem(t: T?) {
        if (null != t) {
            if (null != list) {
                list!!.add(t)
            }
        }
        notifyDataSetChanged()
    }

    fun remove(t: T?) {
        if (null != t) {
            if (null != list) {
                list!!.remove(t)
            }
        }
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        if (position > -1 && null != list && list!!.size > position) {
            list!!.removeAt(position)
            innerAdapter.notifyDataSetChanged()
        }
    }


    /**
     * 设置adapter
     * @param adapter
     */
    private fun setAdapter(adapter: RealAdapter) {

        //        if (adapter != null) {
        //            if (!(adapter instanceof RecyclerView.Adapter))
        //                throw new RuntimeException("your adapter must be a RecyclerView.Adapter");
        //        }

        this.innerAdapter = adapter
        notifyItemRangeRemoved(getHeaderViewsCount(), innerAdapter.itemCount)
        //            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
        innerAdapter.registerAdapterDataObserver(mDataObserver)
        notifyItemRangeInserted(getHeaderViewsCount(), innerAdapter.itemCount)
    }

    fun addHeaderView(header: View?) {

        if (header == null) {
            throw RuntimeException("header is null")
        }

        mHeaderViews.add(header)
        this.notifyDataSetChanged()
    }

    fun addFooterView(footer: View?) {

        if (footer == null) {
            throw RuntimeException("footer is null")
        }

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
     * @param v
     * @param visible
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
        return getHeaderViewsCount() > 0 && position == 0
    }

    fun isFooter(position: Int): Boolean {
        val lastPosition = itemCount - 1
        return getFooterViewsCount() > 0 && position == lastPosition
    }

    fun getFooter(position: Int): View? {
        val footerPosition = itemCount - getHeaderViewsCount() - position
        return if (mFooterViews.size > footerPosition && footerPosition > -1) {
            mFooterViews[footerPosition]
        } else null
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        innerAdapter.onAttachedToRecyclerView(recyclerView)
        //为了兼容GridLayout
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager
            val spanSizeLookup = gridLayoutManager.spanSizeLookup

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position < getHeaderViewsCount()) {
                        return gridLayoutManager.spanCount
                    } else if (position >= getHeaderViewsCount() + innerAdapter.itemCount) {
                        return gridLayoutManager.spanCount
                    }
                    return spanSizeLookup?.getSpanSize(position) ?: 1
                }
            }
            gridLayoutManager.spanCount = gridLayoutManager.spanCount
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val headerViewsCountCount = getHeaderViewsCount()
        return if (viewType < TYPE_HEADER_VIEW + headerViewsCountCount) {
            ViewHolder(mHeaderViews[viewType - TYPE_HEADER_VIEW])
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < TYPE_DIVIDER) {
            ViewHolder(mFooterViews[viewType - TYPE_FOOTER_VIEW])
        } else {
            innerAdapter.onCreateViewHolder(parent, viewType - TYPE_DIVIDER)
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
        val innerCount = innerAdapter.itemCount
        val headerViewsCountCount = getHeaderViewsCount()
        if (position < headerViewsCountCount) {
            return TYPE_HEADER_VIEW + position
        } else if (headerViewsCountCount <= position && position < headerViewsCountCount + innerCount) {

            val innerItemViewType = innerAdapter.getItemViewType(position - headerViewsCountCount)
            if (innerItemViewType >= Integer.MAX_VALUE / 2) {
                throw IllegalArgumentException("your adapter's return value of getViewTypeCount() must < Integer.MAX_VALUE / 2")
            }
            return innerItemViewType + Integer.MAX_VALUE / 2
        } else {
            return TYPE_FOOTER_VIEW + position - innerCount
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
     * @param context
     * @param parent
     * @param viewType
     * @return
     */
    fun getViewHolder(context: Context, parent: ViewGroup, viewType: Int): CommonViewHolder<T> {
        return CommonViewHolder.getHolder(context, getLayoutIdByType(viewType), parent)
    }

    /**
     * 获取当前Item数据
     * @param position
     * @return
     */
    fun getItem(position: Int): T? {
        return if (list != null && position < list!!.size && position >= 0) {
            list!![position]
        } else null
    }

    /**
     * 还是兼容下DiffUtil
     * @param holder
     * @param position
     * @param t
     * @param bundle
     */
    fun changeItem(holder: CommonViewHolder<T>, position: Int, t: T?, bundle: Bundle) {

    }

    /**
     * 真实的Adapter
     * @param <K>
    </K> */
    private inner class RealAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return getViewHolder(context, parent, viewType)
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
            if (holder is CommonViewHolder<*>) {
                val m = holder as CommonViewHolder<T>
                if (payloads.isEmpty()) {
                    val t = getItem(position)
                    setItemVisible(holder.itemView, t != null)
                    t?: return
                    m.setOnItemClickListener(position, t, onItemClickListener)
                    m.setOnItemLongClickListener(position, t, onItemLongClickListener)
                    bindData(m, position, t)
                } else {
                    val t = getItem(position)
                    changeItem(m, position, t, payloads[0] as Bundle)
                }
            } else {
                throw RuntimeException("Holder must be not null !")
            }
        }

        override fun getItemCount(): Int {
            return if (list != null) list!!.size else 0
        }

        override fun getItemViewType(position: Int): Int {
            return getInnerViewType(position)
        }
    }


    fun getInnerViewType(position: Int): Int {
        return getViewType(position)
    }

    /**
     * 为了方便不想每次都重写，默认设置0
     * @param position
     * @return
     */
    protected open fun getViewType(position: Int): Int {
        return 0
    }

    abstract fun getLayoutIdByType(viewType: Int): Int

    abstract fun bindData(holder: CommonViewHolder<T>, position: Int, t: T)

    companion object {

        private val TYPE_DIVIDER = Integer.MAX_VALUE / 2
        private val TYPE_HEADER_VIEW = Integer.MIN_VALUE
        private val TYPE_FOOTER_VIEW = Integer.MIN_VALUE + 1
    }

}



