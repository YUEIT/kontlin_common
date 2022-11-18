package cn.yue.test.nested

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView


/**
 * Description :
 * Created by yue on 2022/9/2
 */

class PriorityNestedScrollView(context: Context, attributeSet: AttributeSet)
    : NestedScrollView(context, attributeSet) {

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        if (consumed != null) {
            val scrollOffset = computeVerticalScrollRange() - computeVerticalScrollOffset()
            if (dy > 0) {
                //向上 到底了，不消耗
                if (scrollOffset > measuredHeight) {
                    scrollBy(0, dy)
                    consumed[1] = dy
                }
            } else {
                //向下 child没到顶，不消耗
                if (computeChildVerticalScrollOffset() <= 0) {
                    scrollBy(0, dy)
                    consumed[1] = dy
                }
            }
        }

        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    @SuppressLint("RestrictedApi")
    override fun computeVerticalScrollRange(): Int {
        val count = childCount
        val parentSpace = height - paddingBottom - paddingTop
        if (count == 0) {
            return parentSpace
        }
        val child = getChildAt(0)
        val lp = child.layoutParams as LayoutParams
        var scrollRange = child.bottom + lp.bottomMargin
        val scrollY = scrollY
        val overscrollBottom = Math.max(0, scrollRange - parentSpace)
        if (scrollY < 0) {
            scrollRange -= scrollY
        } else if (scrollY > overscrollBottom) {
            scrollRange += scrollY - overscrollBottom
        }
        return scrollRange
    }

    @SuppressLint("RestrictedApi")
    override fun computeVerticalScrollOffset(): Int {
        return Math.max(0, super.getScrollY())
    }

    private fun computeChildVerticalScrollOffset(): Int {
        return scrollContainer?.computeScrollOffset() ?: 0
    }

    interface ScrollContainer {
        fun computeScrollOffset(): Int
    }

    private var scrollContainer: ScrollContainer? = null

    fun setScrollContainer(scrollContainer: ScrollContainer) {
        this.scrollContainer = scrollContainer
    }
}