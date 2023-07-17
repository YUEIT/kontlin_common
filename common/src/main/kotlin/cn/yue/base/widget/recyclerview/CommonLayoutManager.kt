package cn.yue.base.widget.recyclerview

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * 介绍：通用LayoutManager 即简化编写流程
 **/

open class CommonLayoutManager : RecyclerView.LayoutManager {

    private val TAG = "CommonLayoutManager"
    var mOrientation: Int = 0
    private var mOrientationHelper: OrientationHelper? = null
    private var mLayoutState: LayoutState? = null
    protected val mLayoutChunkResult = LayoutChunkResult()

    constructor(mOrientation: Int = OrientationHelper.VERTICAL) {
//        isAutoMeasureEnabled = true
        //设置该值可以兼容viewHolder设置为wrap_content
        if (mOrientation != OrientationHelper.HORIZONTAL && mOrientation != OrientationHelper.VERTICAL) {
            throw IllegalArgumentException("invalid orientation:$mOrientation")
        }
        this.mOrientation = mOrientation
        ensureLayoutState()
    }

    open fun getOffsetSecond(): Int = 0

    /**
     * 这里获取的是相对于屏幕而言，即获取屏幕上的最后一项
     * @return
     */
    private fun getChildClosestToEnd(): View? = getChildAt(childCount - 1)

    /**
     * 这里获取的是相对于屏幕而言，即获取屏幕上的第一项
     * @return
     */
    private fun getChildClosestToStart(): View? = getChildAt(0)

    fun getOrientationHelper(): OrientationHelper {
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, mOrientation)
        }
        return mOrientationHelper!!
    }

    private fun getLayoutState(): LayoutState {
        if (mLayoutState == null) {
            mLayoutState = LayoutState()
        }
        return mLayoutState!!
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
    /**
     * 初始化LayoutState和OrientationHelper
     */
    private fun ensureLayoutState() {
        getLayoutState()
        getOrientationHelper()
    }

    private fun resolveIsInfinite(): Boolean {
        return getOrientationHelper().mode == View.MeasureSpec.UNSPECIFIED && getOrientationHelper().end == 0
    }

    private var savedState: SavedState? = null

    override fun onSaveInstanceState(): Parcelable {
        if (savedState != null) {
            return SavedState(savedState!!)
        }
        val state = SavedState()
        if (childCount > 0) {
            ensureLayoutState()
            val refChild = getChildClosestToStart()
            refChild?.let {
                state.position = getPosition(it)
                state.offset = getOrientationHelper().getDecoratedStart(it) - getOrientationHelper().startAfterPadding
            }
        }
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            savedState = state
            requestLayout()
            if (DEBUG) {
                Log.d(TAG, "loaded saved state")
            }
        } else if (DEBUG) {
            Log.d(TAG, "invalid saved state class")
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        /*  按照LinearLayoutManager源码应该是以下步骤：（这里省略了第4项）
            1、检查children和其他变量，获取到anchor的坐标和position，这里代码上主要是从onRestoreInstanceState中获取SavedState对象
               该对象保存了position、offset、LayoutFromEnd(布局方向)
            2、由当前position开始，position++ 布局
            3、由当前position开始，position-- 布局
            4、滚动以满足堆栈从底部的要求？（翻译的，看不懂），可以理解为滚动布局以调整，避免出现一些缝隙
         */
        if (DEBUG) {
            Log.d(TAG, "is pre layout:" + state.isPreLayout)
        }
        var position = 0 //这里指屏幕第一项
        var offset = 0
        if (savedState != null) {
            if (state.itemCount == 0) {
                removeAndRecycleAllViews(recycler)
                return
            }
            position = savedState!!.position
            offset = savedState!!.offset
        }
        ensureLayoutState()
        getLayoutState().mRecycle = false

        detachAndScrapAttachedViews(recycler)
        getLayoutState().mInfinite = resolveIsInfinite()

        updateLayoutStateToFillEnd(position, offset)
        fill(recycler, getLayoutState(), state)

        val endOffset = getLayoutState().mOffsetFirst
        val lastElement = getLayoutState().mCurrentPosition
        updateLayoutStateToFillStart(position, offset)
        getLayoutState().mCurrentPosition += getLayoutState().mItemDirection
        fill(recycler, getLayoutState(), state)
        if (getLayoutState().mAvailable > 0) {
            // start could not consume all it should. add more items towards end
            updateLayoutStateToFillEnd(lastElement, endOffset)
            fill(recycler, getLayoutState(), state)
        }
        if (!state.isPreLayout) {
            getOrientationHelper().onLayoutComplete()
        }
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * 更新布局变量，方向为从当前项往前（即position--）
     * @param itemPosition
     * @param offset
     */
    private fun updateLayoutStateToFillStart(itemPosition: Int, offset: Int) {
        getLayoutState().mAvailable = offset - getOrientationHelper().startAfterPadding
        getLayoutState().mCurrentPosition = itemPosition
        getLayoutState().mItemDirection = LayoutState.ITEM_DIRECTION_HEAD
        getLayoutState().mLayoutDirection = LayoutState.LAYOUT_START
        getLayoutState().mOffsetFirst = offset
        getLayoutState().mOffsetSecond = getOffsetSecond()
        getLayoutState().mScrollingOffset = LayoutState.SCROLLING_OFFSET_NaN
    }

    /**
     * 更新布局变量，方向为从当前项往后（即position++）
     * @param itemPosition
     * @param offset
     */
    private fun updateLayoutStateToFillEnd(itemPosition: Int, offset: Int) {
        getLayoutState().mAvailable = getOrientationHelper().endAfterPadding - offset
        getLayoutState().mItemDirection = LayoutState.ITEM_DIRECTION_TAIL
        getLayoutState().mCurrentPosition = itemPosition
        getLayoutState().mLayoutDirection = LayoutState.LAYOUT_END
        getLayoutState().mOffsetFirst = offset
        getLayoutState().mOffsetSecond = getOffsetSecond()
        getLayoutState().mScrollingOffset = LayoutState.SCROLLING_OFFSET_NaN
    }


    /**
     * 水平滑动
     * @return
     */
    override fun canScrollHorizontally(): Boolean {
        return false
    }

    /**
     * 垂直滑动
     * @return
     */
    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return if (mOrientation == OrientationHelper.VERTICAL) {
            0
        } else scrollBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return if (mOrientation == OrientationHelper.HORIZONTAL) {
            0
        } else scrollBy(dy, recycler, state)
    }

    /**
     * 实际滑动操作
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    private fun scrollBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        if (childCount == 0 || dy == 0) {
            return 0
        }
        getLayoutState().mRecycle = true
        ensureLayoutState()
        val layoutDirection = if (dy > 0) LayoutState.LAYOUT_END else LayoutState.LAYOUT_START
        val absDy = Math.abs(dy)
        updateLayoutState(layoutDirection, absDy, true, state)
        val consumed = getLayoutState().mScrollingOffset + fill(recycler, getLayoutState(), state)

        if (consumed < 0) {
            if (DEBUG) {
                Log.d(TAG, "Don't have any more elements to scroll")
            }
            return 0
        }
        val scrolled = if (absDy > consumed) layoutDirection * consumed else dy
        getOrientationHelper().offsetChildren(-scrolled)
        if (DEBUG) {
            Log.d(TAG, "scroll dy: $dy scrolled: $scrolled")
        }
        getLayoutState().mLastScrollDelta = scrolled
        return scrolled
    }

    /**
     * 滑动时，更新布局变量
     * @param layoutDirection
     * @param requiredSpace
     * @param canUseExistingSpace
     * @param state
     */
    fun updateLayoutState(layoutDirection: Int, requiredSpace: Int,
                          canUseExistingSpace: Boolean, state: RecyclerView.State?) {
        // If parent provides a hint, don't measure unlimited.
        getLayoutState().mInfinite = false
        getLayoutState().mLayoutDirection = layoutDirection
        var scrollingOffset: Int = 0
        if (layoutDirection == LayoutState.LAYOUT_END) { //列表从下往上移动
            // get the first child in the direction we are going
            val child = getChildClosestToEnd()
            // the direction in which we are traversing children
            child?.let {
                getLayoutState().mItemDirection = LayoutState.ITEM_DIRECTION_TAIL //布局在尾部
                getLayoutState().mCurrentPosition = getPosition(child) + getLayoutState().mItemDirection
                getLayoutState().mOffsetFirst = getOrientationHelper().getDecoratedEnd(child)
                getLayoutState().mOffsetSecond = getDecoratedOtherEnd(child)
                // calculate how much we can scroll without adding new children (independent of layout)
                scrollingOffset = getOrientationHelper().getDecoratedEnd(child) - getOrientationHelper().endAfterPadding
            }
        } else {
            val child = getChildClosestToStart()
            child?.let {
                getLayoutState().mItemDirection = LayoutState.ITEM_DIRECTION_HEAD
                getLayoutState().mCurrentPosition = getPosition(child) + getLayoutState().mItemDirection
                getLayoutState().mOffsetFirst = getOrientationHelper().getDecoratedStart(child)
                getLayoutState().mOffsetSecond = getDecoratedOtherEnd(child)
                scrollingOffset = -getOrientationHelper().getDecoratedStart(child) + getOrientationHelper().startAfterPadding
            }
        }
        getLayoutState().mAvailable = requiredSpace
        if (canUseExistingSpace) {
            getLayoutState().mAvailable -= scrollingOffset
        }
        getLayoutState().mScrollingOffset = scrollingOffset
    }

    private fun getDecoratedOtherEnd(child: View): Int {
        val params = child.layoutParams as RecyclerView.LayoutParams
        return if (mOrientation == OrientationHelper.HORIZONTAL) {
            getDecoratedRight(child) + params.rightMargin
        } else {
            getDecoratedBottom(child) + params.bottomMargin
        }
    }

    /**
     * 回收child，添加新的child
     * @param recycler
     * @param layoutState
     * @param state
     * @return
     */
    private fun fill(recycler: RecyclerView.Recycler?, layoutState: LayoutState, state: RecyclerView.State?): Int {
        val start = layoutState.mAvailable
        if (layoutState.mScrollingOffset != LayoutState.SCROLLING_OFFSET_NaN) {
            // TODO ugly bug fix. should not happen
            if (layoutState.mAvailable < 0) {
                layoutState.mScrollingOffset += layoutState.mAvailable
            }
            recycleByLayoutState(recycler, layoutState)
        }
        fillLayout(recycler, layoutState, state)
        return start - layoutState.mAvailable
    }

    open fun fillLayout(recycler: RecyclerView.Recycler?, layoutState: LayoutState, state: RecyclerView.State?) {
        var remainingSpace = layoutState.mAvailable
        val layoutChunkResult = mLayoutChunkResult
        while ((layoutState.mInfinite || remainingSpace > 0) && layoutState.hasMore(state)) {
            layoutChunkResult.resetInternal()
            val view = layoutState.next(recycler)
            val params = view!!.layoutParams as RecyclerView.LayoutParams
            measureChildWithMargins(view, 0, 0)
            // Consume the available space if the view is not removed OR changed
            if (params.isItemRemoved || params.isItemChanged) {
                layoutChunkResult.mIgnoreConsumed = true
            }
            addView(view, layoutState, layoutChunkResult)
            onLayout(view, state, layoutState, layoutChunkResult)
            if (layoutChunkResult.mFinished) {
                break
            }
            layoutState.mOffsetFirst += layoutChunkResult.mConsumed * layoutState.mLayoutDirection
            /**
             * Consume the available space if:
             * * layoutChunk did not request to be ignored
             * * OR we are laying out scrap children
             * * OR we are not doing pre-layout
             */
            if (!layoutChunkResult.mIgnoreConsumed || !state!!.isPreLayout) {
                layoutState.mAvailable -= layoutChunkResult.mConsumed
                // we keep a separate remaining space because mAvailable is important for recycling
                remainingSpace -= layoutChunkResult.mConsumed
            }
            //初始布局时mScrollingOffset时为最小值
            if (layoutState.mScrollingOffset != LayoutState.SCROLLING_OFFSET_NaN) {
                layoutState.mScrollingOffset += layoutChunkResult.mConsumed
                if (layoutState.mAvailable < 0) {
                    layoutState.mScrollingOffset += layoutState.mAvailable
                }
                recycleByLayoutState(recycler, layoutState)
            }
        }
    }

    /**
     * 布局填充
     * @param view
     * @param layoutState
     * @param result
     */
    fun addView(view: View?, layoutState: LayoutState, result: LayoutChunkResult) {
        if (view == null) {
            // if we are laying out views in scrap, this may return null which means there is
            // no more items to layout.
            result.mFinished = true
            return
        }
        if (layoutState.mLayoutDirection != LayoutState.LAYOUT_START) {
            addView(view)
        } else {
            addView(view, 0)
        }
        result.mFocusable = view.isFocusable
    }

    /**
     * 具体布局填充，自定义时重写
     * @param view
     * @param state
     * @param layoutState
     * @param result
     */
    open fun onLayout(view: View, state: RecyclerView.State?, layoutState: LayoutState, result: LayoutChunkResult) {
        result.mConsumed = getOrientationHelper().getDecoratedMeasurement(view) //源码为实际高度+marginTop+marginBottom，即item的高度
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (mOrientation == OrientationHelper.VERTICAL) {
            left = paddingLeft
            right = left + getOrientationHelper().getDecoratedMeasurementInOther(view)
            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                bottom = layoutState.mOffsetFirst
                top = layoutState.mOffsetFirst - result.mConsumed
            } else {
                top = layoutState.mOffsetFirst
                bottom = layoutState.mOffsetFirst + result.mConsumed
            }
        } else {
            top = paddingTop
            bottom = top + getOrientationHelper().getDecoratedMeasurementInOther(view)

            if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
                right = layoutState.mOffsetFirst
                left = layoutState.mOffsetFirst - result.mConsumed
            } else {
                left = layoutState.mOffsetFirst
                right = layoutState.mOffsetFirst + result.mConsumed
            }
        }
        // We calculate everything with View's bounding box (which includes decor and margins)
        // To calculate correct layout position, we subtract margins.
        layoutDecoratedWithMargins(view, left, top, right, bottom)
        if (DEBUG) {
            Log.d(TAG, "laid out child at position " + getPosition(view) + ", l:" + left +
                    ", t:" + top + ", r:" + right + ", b:" + bottom)
        }
    }

    /**
     * 回收child
     * @param recycler
     * @param layoutState
     */
    fun recycleByLayoutState(recycler: RecyclerView.Recycler?, layoutState: LayoutState) {
        if (!layoutState.mRecycle || layoutState.mInfinite) {
            return
        }
        if (layoutState.mLayoutDirection == LayoutState.LAYOUT_START) {
            recycleViewsFromEnd(recycler, layoutState.mScrollingOffset)
        } else {
            recycleViewsFromStart(recycler, layoutState.mScrollingOffset)
        }
    }

    /**
     * 列表自上而下，回收最下面的child，规则是从屏幕内消失
     * @param recycler
     * @param dt
     */
    private fun recycleViewsFromEnd(recycler: RecyclerView.Recycler?, dt: Int) {
        val childCount = childCount
        if (dt < 0) {
            if (DEBUG) {
                Log.d(TAG, "Called recycle from end with a negative value. This might happen" + " during layout changes but may be sign of a bug")
            }
            return
        }
        val limit = getOrientationHelper().end - dt
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (getOrientationHelper().getDecoratedStart(child) < limit || getOrientationHelper().getTransformedStartWithDecoration(child) < limit) {
                // stop here
                recycleChildren(recycler, childCount - 1, i)
                return
            }
        }
    }

    /**
     * 列表自下而上移动，回收最上面的child
     * @param recycler
     * @param dt
     */
    private fun recycleViewsFromStart(recycler: RecyclerView.Recycler?, dt: Int) {
        if (dt < 0) {
            if (DEBUG) {
                Log.d(TAG, "Called recycle from start with a negative value. This might happen" + " during layout changes but may be sign of a bug")
            }
            return
        }
        // ignore padding, ViewGroup may not clip children.
        val limit = dt
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (getOrientationHelper().getDecoratedEnd(child) > limit || getOrientationHelper().getTransformedEndWithDecoration(child) > limit) {
                // stop here
                recycleChildren(recycler, 0, i)
                return
            }
        }
    }

    /**
     * 回收child
     * @param recycler
     * @param startIndex
     * @param endIndex
     */
    private fun recycleChildren(recycler: RecyclerView.Recycler?, startIndex: Int, endIndex: Int) {
        if (startIndex == endIndex) {
            return
        }
        if (DEBUG) {
            Log.d(TAG, "Recycling " + Math.abs(startIndex - endIndex) + " items")
        }
        if (endIndex > startIndex) {
            for (i in endIndex - 1 downTo startIndex) {
                removeAndRecycleViewAt(i, recycler!!)
            }
        } else {
            for (i in startIndex downTo endIndex + 1) {
                removeAndRecycleViewAt(i, recycler!!)
            }
        }
    }

    /**
     * 填充布局结果反馈
     */
    class LayoutChunkResult {
        var mConsumed: Int = 0
        var mFinished: Boolean = false
        var mIgnoreConsumed: Boolean = false
        var mFocusable: Boolean = false

        fun resetInternal() {
            mConsumed = 0
            mFinished = false
            mIgnoreConsumed = false
            mFocusable = false
        }
    }

    /**
     * 保存状态
     */
    class SavedState() : Parcelable {

        internal var position: Int = 0

        internal var offset: Int = 0

        internal constructor(`in`: Parcel) : this() {
            position = `in`.readInt()
            offset = `in`.readInt()
        }

        constructor(other: SavedState) : this() {
            position = other.position
            offset = other.offset
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(position)
            dest.writeInt(offset)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(0)
                }

            }
        }
    }

    /**
     * 填充布局时的临时状态的帮助类
     */
    class LayoutState {

        /**
         * We may not want to recycle children in some cases (e.g. layout)
         */
        var mRecycle = true

        /**
         * 布局时，用于确定child的位置（同mOrientation设置的方向上）
         */
        var mOffsetFirst: Int = 0

        /**
         * 布局时，用于确定child的位置（二级）
         */
        var mOffsetSecond: Int = 0

        /**
         * 布局可用距离，为布局前等于整个recyclerView的高度，每添加一个item，可用距离减去item的高度；
         * 滑动处理是可以理解为“滑动后”距离完全可见还需多少距离（该值是负值），即mAvailable = |dy| - mScrollingOffset
         */
        var mAvailable: Int = 0

        /**
         * 布局时，当前位置
         */
        var mCurrentPosition: Int = 0

        /**
         * 填充布局时的方向，ITEM_DIRECTION_HEAD（头部），ITEM_DIRECTION_TAIL（尾部）
         */
        var mItemDirection: Int = 0

        /**
         * 列表移动方向 LAYOUT_START or LAYOUT_END
         */
        var mLayoutDirection: Int = 0

        /**
         * 最后一个可见View “滑动前”距离完全可见还需多少距离（该值为正值）
         */
        var mScrollingOffset: Int = 0

        /**
         * The most recent [.scrollBy]
         * amount.
         */
        var mLastScrollDelta: Int = 0

        /**
         * 不限制填充child的数量，慎用！
         */
        var mInfinite: Boolean = false

        fun hasMore(state: RecyclerView.State?): Boolean {
            if (state == null) {
                return false
            }
            return mCurrentPosition >= 0 && mCurrentPosition < state.itemCount
        }

        /**
         * 从各级缓存中获取当前child，并将当前position在当前方向后移
         * （这里有个坑，这项操作除了会获取当前child，还会做一些处理，调用该方法获取了position的数据，
         * 这时如果获取的数据是来自于Scrap中的，那么下次如果继续调用该方法获取的数据却是来自recycledViewPool中的，
         * 此时页面会有闪屏的现象）
         */
        fun next(recycler: RecyclerView.Recycler?): View? {
            try {
                val view = recycler!!.getViewForPosition(mCurrentPosition)
                mCurrentPosition += mItemDirection
                return view
            } catch (e: IndexOutOfBoundsException) {
                return null
            }

        }

        companion object {

            const val TAG = "LayoutState"

            const val LAYOUT_START = -1

            const val LAYOUT_END = 1

            const val INVALID_LAYOUT = Integer.MIN_VALUE

            const val ITEM_DIRECTION_HEAD = -1

            const val ITEM_DIRECTION_TAIL = 1

            const val SCROLLING_OFFSET_NaN = Integer.MIN_VALUE
        }
    }

    companion object {
        private const val DEBUG = true
    }
}