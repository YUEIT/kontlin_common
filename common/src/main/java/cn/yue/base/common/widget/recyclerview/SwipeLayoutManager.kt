package cn.yue.base.common.widget.recyclerview

import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import java.util.*

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/12/27.
 */

class SwipeLayoutManager : CommonLayoutManager() {

    override fun getOffsetSecond(): Int {
        return if (mOrientation == OrientationHelper.HORIZONTAL) { 0 } else 0
    }


    private val fillCachedView = ArrayList<View>()
    private var lastView: View? = null

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return true
    }


    override fun getWidthMode(): Int {
        //默认情况下如果设置setAutoMeasureEnabled(true)，测量模式为EXACTLY
        return View.MeasureSpec.AT_MOST
    }

    override fun fillLayout(recycler: RecyclerView.Recycler?, layoutState: CommonLayoutManager.LayoutState, state: RecyclerView.State?) {
        var remainingSpace = layoutState.mAvailable
        val layoutChunkResult = mLayoutChunkResult
        layoutState.mOffsetSecond = getOffsetSecond()
        while ((layoutState.mInfinite || remainingSpace > 0) && layoutState.hasMore(state)) {
            layoutChunkResult.resetInternal()
            fillScrollLayout(recycler, layoutState, state, layoutChunkResult) //填充一行
            if (layoutChunkResult.mFinished) {
                break
            }
            layoutState.mOffsetFirst = layoutState.mOffsetFirst + layoutChunkResult.mConsumed * layoutState.mLayoutDirection
            if (!state!!.isPreLayout) {
                layoutState.mAvailable = layoutState.mAvailable - layoutChunkResult.mConsumed
                // we keep a separate remaining space because mAvailable is important for recycling
                remainingSpace -= layoutChunkResult.mConsumed //可用空间将去布局一行的空间，即剩余空间
            }
            //初始布局时mScrollingOffset时为最小值
            if (layoutState.mScrollingOffset != CommonLayoutManager.LayoutState.SCROLLING_OFFSET_NaN) {
                layoutState.mScrollingOffset = layoutState.mScrollingOffset + layoutChunkResult.mConsumed
                if (layoutState.mAvailable < 0) {
                    layoutState.mScrollingOffset = layoutState.mScrollingOffset + layoutState.mAvailable
                }
                recycleByLayoutState(recycler, layoutState)
            }
        }
        lastView = null
    }

    /**
     * 填充一行
     * @param recycler
     * @param layoutState
     * @param state
     * @param layoutChunkResult
     */
    private fun fillScrollLayout(recycler: RecyclerView.Recycler?, layoutState: CommonLayoutManager.LayoutState, state: RecyclerView.State?, layoutChunkResult: CommonLayoutManager.LayoutChunkResult) {
        val remainingSpace = layoutState.mAvailable
        layoutState.mOffsetSecond = getOffsetSecond()
        if ((layoutState.mInfinite || remainingSpace > 0) && layoutState.hasMore(state)) {
            while (layoutState.mOffsetSecond < width && layoutState.hasMore(state)) { //计算出一行所用child数量
                val view: View?
                if (lastView == null) {
                    //view = recycler.getViewForPosition(lastPosition);
                    view = layoutState.next(recycler)
                } else {
                    view = lastView
                    lastView = null
                }
                measureChildWithMargins(view!!, 0, 0)
                if (layoutState.mOffsetSecond + mOrientationHelper!!.getDecoratedMeasurementInOther(view) > width) {
                    lastView = view
                    break
                }
                fillCachedView.add(view)
                layoutState.mOffsetSecond = layoutState.mOffsetSecond + mOrientationHelper!!.getDecoratedMeasurementInOther(view)
            }
            if (layoutState.mItemDirection == CommonLayoutManager.LayoutState.ITEM_DIRECTION_TAIL) {
                layoutState.mOffsetSecond = getOffsetSecond()
                for (i in fillCachedView.indices) {
                    val view = fillCachedView[i]
                    measureChildWithMargins(view, 0, 0)
                    layoutState.mOffsetSecond = layoutState.mOffsetSecond + mOrientationHelper!!.getDecoratedMeasurementInOther(view)
                    addView(view, layoutState, layoutChunkResult)
                    onLayout(view, state, layoutState, layoutChunkResult)
                }
            } else {
                for (i in fillCachedView.indices) {
                    val view = fillCachedView[i]
                    measureChildWithMargins(view, 0, 0)
                    addView(view, layoutState, layoutChunkResult)
                    onLayout(view, state, layoutState, layoutChunkResult)
                    layoutState.mOffsetSecond = layoutState.mOffsetSecond - mOrientationHelper!!.getDecoratedMeasurementInOther(view)
                }
            }

        }
        fillCachedView.clear()
    }

    override fun onLayout(view: View, state: RecyclerView.State?, layoutState: CommonLayoutManager.LayoutState, result: CommonLayoutManager.LayoutChunkResult) {
        result.mConsumed = mOrientationHelper!!.getDecoratedMeasurement(view) //源码为实际高度+marginTop+marginBottom，即item的高度
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int
        if (mOrientation === OrientationHelper.VERTICAL) {
            left = layoutState.mOffsetSecond - mOrientationHelper!!.getDecoratedMeasurementInOther(view)
            right = layoutState.mOffsetSecond
            if (layoutState.mLayoutDirection == CommonLayoutManager.LayoutState.LAYOUT_START) {
                bottom = layoutState.mOffsetFirst
                top = layoutState.mOffsetFirst - result.mConsumed
            } else {
                top = layoutState.mOffsetFirst
                bottom = layoutState.mOffsetFirst + result.mConsumed
            }
        } else {
            top = layoutState.mOffsetSecond - mOrientationHelper!!.getDecoratedMeasurementInOther(view)
            bottom = layoutState.mOffsetSecond
            if (layoutState.mLayoutDirection == CommonLayoutManager.LayoutState.LAYOUT_START) {
                right = layoutState.mOffsetFirst
                left = layoutState.mOffsetFirst - result.mConsumed
            } else {
                left = layoutState.mOffsetFirst
                right = layoutState.mOffsetFirst + result.mConsumed
            }
        }
        layoutDecoratedWithMargins(view, left, top, right, bottom)
    }
}
