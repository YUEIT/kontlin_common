package cn.yue.base.widget.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutDecoration : RecyclerView.ItemDecoration {

    var mDivider: Drawable? = null

    private var mOrientation: Int = 0

    private var mPaint: Paint? = null
    private var dividerSize: Int = 0

    constructor(context: Context, orientation: Int) {
        val a = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
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

    private fun setOrientation(orientation: Int) {
        if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }

    }


    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            //RecyclerView v = new RecyclerView(parent.getContext());
            val params = child
                    .layoutParams as RecyclerView.LayoutParams

            if (mPaint != null) {
                c.drawRect(child.left.toFloat(), child.bottom.toFloat(), child.right.toFloat(), (child.bottom + dividerSize).toFloat(), mPaint!!)//画底下分割线
            } else {
                mDivider?.let {
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + it.intrinsicHeight
                    it.setBounds(left, top, right, bottom)
                    it.draw(c)
                }
            }
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            if (mPaint != null) {
                //画底下分割线
                c.drawRect(child.right.toFloat(), child.top.toFloat(),
                        (child.right + dividerSize).toFloat(), child.bottom.toFloat(), mPaint!!)
            } else {
                mDivider?.let {
                    val left = child.right + params.rightMargin
                    val right = left + it.intrinsicHeight
                    it.setBounds(left, top, right, bottom)
                    it.draw(c)
                }
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            if (mPaint != null) {
                outRect.set(0, 0, 0, dividerSize)
            } else {
                mDivider?.let {
                    outRect.set(0, 0, 0, it.intrinsicHeight)
                }
            }
        } else {
            if (mPaint != null) {
                outRect.set(0, 0, dividerSize, 0)
            } else {
                mDivider?.let {
                    outRect.set(0, 0, it.intrinsicWidth, 0)
                }
            }
        }
    }

}
