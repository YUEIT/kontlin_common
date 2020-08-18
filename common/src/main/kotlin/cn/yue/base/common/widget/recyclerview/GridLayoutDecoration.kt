package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable

import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 介绍：GridLayoutManager\StaggeredGridLayoutManager 分界线
 */

class GridLayoutDecoration : RecyclerView.ItemDecoration {
    private var mDivider: Drawable? = null
    private var mPaint: Paint? = null
    private var dividerSize: Int = 0

    private val attrs = intArrayOf(android.R.attr.listDivider)

    constructor(context: Context) {
        val a = context.obtainStyledAttributes(attrs)
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    constructor(context: Context, color: Int, @DimenRes dividerSize: Int) {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = color
        this.dividerSize = dividerSize
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }


    private fun getSpanCount(parent: RecyclerView): Int {
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager.spanCount
        }
        return spanCount
    }

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val top = child.bottom + params.bottomMargin
            if (mPaint != null) {
                val right = child.right + params.rightMargin + dividerSize
                val bottom = top + dividerSize
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)//画底下分割线
            } else {
                val right = child.right + params.rightMargin + mDivider!!.intrinsicWidth
                val bottom = top + mDivider!!.intrinsicHeight
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            }
        }
    }

    fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin
            val left = child.right + params.rightMargin
            if (mPaint != null) {
                val right = left + dividerSize
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint!!)//画底下分割线
            } else {
                val right = left + mDivider!!.intrinsicWidth
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c)
            }
        }
    }

    /**
     * 判断是否是最后一列
     */
    private fun isLastColum(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        var childCount = childCount
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            // 如果是最后一列，则不需要绘制右边
            if ((pos + 1) % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager
                    .orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            } else {
                childCount = childCount - childCount % spanCount
                if (pos >= childCount)
                // 如果是最后一列，则不需要绘制右边
                    return true
            }
        }
        return false
    }

    /**
     * 是否是最后一行
     */
    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        var childCount = childCount
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            childCount = childCount - childCount % spanCount
            // 如果是最后一行，则不需要绘制底部
            if (pos >= childCount)
                return true
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager
                    .orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true
            } else {
                // StaggeredGridLayoutManager 且横向滚动
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        val spanCount = getSpanCount(parent)
        val childCount = parent.adapter!!.itemCount
        // 如果是最后一行，则不需要绘制底部
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        } else if (isLastColum(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth,
                    mDivider!!.intrinsicHeight)
        }
    }

}
