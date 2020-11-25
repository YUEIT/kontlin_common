package cn.yue.base.common.utils.file

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import cn.yue.base.common.utils.app.DisplayUtils

object ImageUtils {

    fun getGradientDrawable(color: Int?, radius: Float): Drawable {
        return getGradientDrawable(color, null, null,
                radius, 0f, 0f, 0f, 0f,
                0f, 0)
    }

    fun getGradientDrawable(color: Int?, radius: Float, strokeWidth: Float, strokeColor: Int): Drawable {
        return getGradientDrawable(color, null, null,
                radius, 0f, 0f, 0f, 0f,
                strokeWidth, strokeColor)
    }

    fun getGradientDrawable(color: Int?, startColor: Int?, endColor: Int?,
                            radius: Float, topLeftRadius: Float, topRightRadius: Float,
                            bottomLeftRadius: Float, bottomRightRadius: Float): Drawable {
        return getGradientDrawable(color, startColor, endColor,
                radius, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius,
                0f, 0)
    }

    fun getGradientDrawable(color: Int?, startColor: Int?, endColor: Int?,
                      radius: Float, topLeftRadius: Float, topRightRadius: Float,
                      bottomLeftRadius: Float, bottomRightRadius: Float, strokeWidth: Float,
                      strokeColor: Int) : Drawable {
        val drawable = GradientDrawable()
        try {
            if (startColor != null && endColor != null) {
                drawable.setColors(intArrayOf(startColor, endColor))
            } else if (color != null) {
                drawable.setColor(color)
            }
            if (topLeftRadius != 0f || topRightRadius != 0f || bottomLeftRadius != 0f || bottomRightRadius != 0f) {
                drawable.cornerRadii = floatArrayOf(
                        DisplayUtils.dip2px(topLeftRadius).toFloat(), DisplayUtils.dip2px(topLeftRadius).toFloat(),
                        DisplayUtils.dip2px(topRightRadius).toFloat(), DisplayUtils.dip2px(topRightRadius).toFloat(),
                        DisplayUtils.dip2px(bottomRightRadius).toFloat(), DisplayUtils.dip2px(bottomRightRadius).toFloat(),
                        DisplayUtils.dip2px(bottomLeftRadius).toFloat(), DisplayUtils.dip2px(bottomLeftRadius).toFloat()
                )
            }
            if (radius > 0) {
                drawable.cornerRadius = DisplayUtils.dip2px(radius).toFloat()
            }
            if (strokeWidth > 0) {
                drawable.setStroke(DisplayUtils.dip2px(strokeWidth), strokeColor)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return drawable
    }
}