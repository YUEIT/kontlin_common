package cn.yue.base.middle.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import cn.yue.base.middle.R

/**
 * Description :
 * Created by yue on 2018/11/13
 */
class PageHintView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var hintIV: ImageView? = null
    private var hintTV: TextView? = null

    private val isNotNullHintTV: Boolean
        get() = hintTV != null

    private val isNotNullHintIV: Boolean
        get() = hintIV != null

    init {
        inflate(context, R.layout.layout_page_hint, this)
        hintIV = findViewById(R.id.hintIV)
        hintTV = findViewById(R.id.hintTV)
        setDefault()
    }

    private fun setDefault() {
        hintTV!!.textSize = 13f
        hintTV!!.setTextColor(-0x5b5b5c)
    }

    fun setHintText(s: String) {
        if (isNotNullHintTV) {
            hintTV!!.text = s
        }
    }

    fun setHintTextColor(@ColorInt color: Int) {
        if (isNotNullHintTV) {
            hintTV!!.setTextColor(color)
        }
    }

    fun setHintTextSize(size: Float) {
        if (isNotNullHintTV) {
            hintTV!!.textSize = size
        }
    }

    fun setHintTextSize(unit: Int, size: Float) {
        if (isNotNullHintTV) {
            hintTV!!.setTextSize(unit, size)
        }
    }

    fun setHintTextTypeface(typeface: Typeface) {
        if (isNotNullHintTV) {
            hintTV!!.typeface = typeface
        }
    }

    fun setHintTextDrawable(left: Drawable, top: Drawable, right: Drawable, bottom: Drawable, padding: Int) {
        if (isNotNullHintTV) {
            hintTV!!.setCompoundDrawables(left, top, right, bottom)
            hintTV!!.compoundDrawablePadding = padding
        }
    }

    fun setHintTextMarginTop(marginTop: Int) {
        if (isNotNullHintTV) {
            val layoutParams = hintTV!!.layoutParams as RelativeLayout.LayoutParams
            layoutParams.topMargin = marginTop
            hintTV!!.layoutParams = layoutParams
        }
    }

    fun setHintImage(@DrawableRes resId: Int) {
        if (isNotNullHintIV) {
            hintIV!!.setImageResource(resId)
        }
    }

    fun setHintImageSize(w: Int, h: Int) {
        if (isNotNullHintIV) {
            val layoutParams = RelativeLayout.LayoutParams(w, h)
            hintIV!!.layoutParams = layoutParams
        }
    }

}
