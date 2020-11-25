package cn.yue.base.common.binding.view

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.databinding.BindingAdapter
import cn.yue.base.common.utils.app.DisplayUtils

/**
 * Description :
 * Created by yue on 2020/10/25
 */
object ViewAdapter {

    @BindingAdapter(value = ["onClickListener"])
    @JvmStatic
    fun setOnClickListener(view: View, onClickListener: (() -> Unit)?) {
        if (onClickListener != null) {
            view.setOnClickListener {
                onClickListener.invoke()
            }
        } else {
            view.setOnClickListener(null)
        }
    }

    @BindingAdapter(value = ["onLongClickListener"])
    @JvmStatic
    fun setOnLongClickListener(view: View, onLongClickListener: (() -> Unit)?) {
        if (onLongClickListener != null) {
            view.setOnClickListener {
                onLongClickListener.invoke()
            }
        } else {
            view.setOnLongClickListener(null)
        }
    }

    @BindingAdapter(value = ["requestFocus"])
    @JvmStatic
    fun requestFocus(view: View, requestFocus: Boolean) {
        if (requestFocus) {
            view.isFocusableInTouchMode = true
            view.requestFocus()
        } else {
            view.clearFocus()
        }
    }

    @BindingAdapter("onFocusChangeCommand")
    @JvmStatic
    fun setOnFocusChangeListener(view: View, focusChangeListener: (v: View, hasFocus: Boolean) -> Unit) {
        view.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            focusChangeListener.invoke(v, hasFocus)
        }
    }

    @BindingAdapter(value = ["selected"])
    @JvmStatic
    fun setSelected(view: View, selected: Boolean) {
        view.isClickable = true
        view.isFocusable = true
        view.isSelected = selected
    }

    @BindingAdapter(value = ["color", "startColor", "endColor",
        "radius", "topLeftRadius", "topRightRadius", "bottomLeftRadius", "bottomRightRadius",
        "strokeWidth", "strokeColor"],
            requireAll = false)
    @JvmStatic
    fun setBackground(view: View, color: String?, startColor: String?, endColor: String?,
                      radius: Float, topLeftRadius: Float, topRightRadius: Float,
                      bottomLeftRadius: Float, bottomRightRadius: Float, strokeWidth: Float,
                      strokeColor: String?) {
        try {
            val drawable = GradientDrawable()
            if (!TextUtils.isEmpty(startColor) && !TextUtils.isEmpty(endColor)) {
                drawable.setColors(intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor)))
            } else if (!TextUtils.isEmpty(color)) {
                drawable.setColor(Color.parseColor(color))
            }
            if (topLeftRadius != 0f || topRightRadius != 0f || bottomLeftRadius != 0f || bottomRightRadius != 0f) {
                drawable.cornerRadii = floatArrayOf(
                        dip2px(topLeftRadius), dip2px(topLeftRadius),
                        dip2px(topRightRadius), dip2px(topRightRadius),
                        dip2px(bottomRightRadius), dip2px(bottomRightRadius),
                        dip2px(bottomLeftRadius), dip2px(bottomLeftRadius)
                )
            }
            if (radius > 0) {
                drawable.cornerRadius = dip2px(radius)
            }
            if (strokeWidth > 0) {
                drawable.setStroke(dip2px(strokeWidth).toInt(), Color.parseColor(strokeColor))
            }
            view.background = drawable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dip2px(dp: Float): Float {
        return if (dp == 0f) 0f else DisplayUtils.dip2px(dp).toFloat()
    }

    @BindingAdapter(value = ["visibility"])
    @JvmStatic
    fun setVisibility(view: View, visibility: Boolean) {
        view.visibility = if (visibility) View.VISIBLE else View.GONE
    }

}