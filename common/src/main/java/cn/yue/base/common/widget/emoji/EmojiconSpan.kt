package cn.yue.base.common.widget.emoji

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan

import java.lang.ref.WeakReference

class EmojiconSpan(private val mContext: Context, private val mResourceId: Int, size: Int, private val mTextSize: Int) : DynamicDrawableSpan(DynamicDrawableSpan.ALIGN_BASELINE) {

    private val mSize: Int

    private var mHeight: Int = 0

    private var mWidth: Int = 0

    private var mTop: Int = 0

    private var mDrawable: Drawable? = null

    private var mDrawableRef: WeakReference<Drawable>? = null

    private fun getCachedDrawable(): Drawable {
            if (mDrawableRef == null || mDrawableRef!!.get() == null) {
                mDrawableRef = WeakReference<Drawable>(drawable)
            }
            return mDrawableRef!!.get()!!
        }

    init {
        mSize = size
        mHeight = mSize
        mWidth = mHeight
    }

    override fun getDrawable(): Drawable? {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.resources.getDrawable(mResourceId)
                mHeight = (mSize * 1.25).toInt()
                mWidth = mHeight * mDrawable!!.intrinsicWidth / mDrawable!!.intrinsicHeight
                mTop = (mTextSize - mHeight) / 2
                mDrawable!!.setBounds(0, mTop, mWidth, mTop + mHeight)
            } catch (e: Exception) {
                // swallow
            }

        }
        return mDrawable
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        val b = getCachedDrawable()
        canvas.save()

        var transY = bottom - b.bounds.bottom
        if (mVerticalAlignment == DynamicDrawableSpan.ALIGN_BASELINE) {
            transY = top + (bottom - top) / 2 - (b.bounds.bottom - b.bounds.top) / 2 - mTop
        }

        canvas.translate(x, transY.toFloat())
        b.draw(canvas)
        canvas.restore()
    }
}