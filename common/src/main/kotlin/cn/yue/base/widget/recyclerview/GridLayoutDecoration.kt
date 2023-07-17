package cn.yue.base.widget.recyclerview

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
    var mDivider: Drawable? = null
    private var mPaint: Paint? = null
    private var dividerSize: Int = 0

    constructor(context: Context) {
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
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

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
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
                mDivider?.let {
                    val right = child.right + params.rightMargin + it.intrinsicWidth
                    val bottom = top + it.intrinsicHeight
                    it.setBounds(left, top, right, bottom)
                    it.draw(c)
                }
            }
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
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
                mDivider?.let {
                    val right = left + it.intrinsicWidth
                    it.setBounds(left, top, right, bottom)
                    it.draw(c)
                }
            }
        }
    }

    /**
     * 判断是否是最后一列
     */
    private fun isLastColumn(parent: RecyclerView, pos: Int, spanCount: Int, mChildCount: Int): Boolean {
        var childCount = mChildCount
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            // 如果是最后一列，则不需要绘制右边
            if ((pos + 1) % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            } else {
                childCount -= childCount % spanCount
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
    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, mChildCount: Int): Boolean {
        var childCount = mChildCount
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            childCount -= childCount % spanCount
            // 如果是最后一行，则不需要绘制底部
            if (pos >= childCount)
                return true
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount -= childCount % spanCount
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
        mDivider?.let {
            if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                outRect.set(0, 0, it.intrinsicWidth, 0)
            } else if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
                outRect.set(0, 0, 0, it.intrinsicHeight)
            } else {
                outRect.set(0, 0, it.intrinsicWidth,
                        it.intrinsicHeight)
            }
        }
    }

}
