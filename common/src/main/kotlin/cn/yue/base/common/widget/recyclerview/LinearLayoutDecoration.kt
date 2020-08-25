package cn.yue.base.common.widget.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutDecoration : RecyclerView.ItemDecoration {

    protected var mDivider: Drawable? = null

    private var mOrientation: Int = 0

    private var mPaint: Paint? = null
    private var dividerSize: Int = 0

    constructor(context: Context, orientation: Int) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        a.recycle()
        setOrientation(orientation)
    }

    constructor(context: Context, orientation: Int, dividerSize: Int, dividerColor: Int) {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = dividerColor
        this.dividerSize = dividerSize
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView) {

        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }

    }


    fun drawVertical(c: Canvas?, parent: RecyclerView?) {
        val left = parent!!.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            //RecyclerView v = new RecyclerView(parent.getContext());
            val params = child
                    .layoutParams as RecyclerView.LayoutParams

            if (mPaint != null) {
                c!!.drawRect(child.left.toFloat(), child.bottom.toFloat(), child.right.toFloat(), (child.bottom + dividerSize).toFloat(), mPaint!!)//画底下分割线
            } else {
                val top = child.bottom + params.bottomMargin
                val bottom = top + mDivider!!.intrinsicHeight
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c!!)
            }
        }
    }

    fun drawHorizontal(c: Canvas?, parent: RecyclerView?) {
        val top = parent!!.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            if (mPaint != null) {
                c!!.drawRect(child.right.toFloat(), child.top.toFloat(), (child.right + dividerSize).toFloat(), child.bottom.toFloat(), mPaint!!)//画底下分割线
            } else {
                val left = child.right + params.rightMargin
                val right = left + mDivider!!.intrinsicHeight
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(c!!)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (mOrientation == VERTICAL_LIST) {
            if (mPaint != null) {
                outRect.set(0, 0, 0, dividerSize)
            } else {
                outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
            }
        } else {
            if (mPaint != null) {
                outRect.set(0, 0, dividerSize, 0)
            } else {
                outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
            }
        }
    }

    companion object {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL

        val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }
}
