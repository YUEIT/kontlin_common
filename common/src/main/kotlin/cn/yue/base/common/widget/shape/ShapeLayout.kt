package cn.yue.base.common.widget.shape

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.yue.base.common.R


/**
 * Description :
 * Created by yue on 2022/1/14
 */

class ShapeFrameLayout(context: Context, attributeSet: AttributeSet)
    : FrameLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeLinearLayout(context: Context, attributeSet: AttributeSet)
    : LinearLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeRelativeLayout(context: Context, attributeSet: AttributeSet)
    : RelativeLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeTextView(context: Context, attributeSet: AttributeSet)
    : androidx.appcompat.widget.AppCompatTextView(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}


class ShapeDelegate(val view: View, attributeSet: AttributeSet) {

    private var radius: Int = 0
    private var topLeftRadius: Int = 0
    private var topRightRadius: Int = 0
    private var bottomRightRadius: Int = 0
    private var bottomLeftRadius: Int = 0
    private var backColor: Int = 0
    private var startBackColor: Int = 0
    private var endBackColor: Int = 0
    private var strokeWidth: Int = 0
    private var strokeColor: Int = 0

    init {
        val a = view.context.obtainStyledAttributes(attributeSet, R.styleable.ShapeBackground)
        radius = a.getDimensionPixelSize(R.styleable.ShapeBackground_bg_radius, 0)
        topLeftRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_bg_radius_tl, 0)
        topRightRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_bg_radius_tr, 0)
        bottomLeftRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_bg_radius_bl, 0)
        bottomRightRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_bg_radius_br, 0)
        backColor = a.getColor(R.styleable.ShapeBackground_bg_color, 0)
        startBackColor = a.getColor(R.styleable.ShapeBackground_bg_start_color, 0)
        endBackColor = a.getColor(R.styleable.ShapeBackground_bg_end_color, 0)
        strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeBackground_bg_stroke_width, 0)
        strokeColor = a.getColor(R.styleable.ShapeBackground_bg_stroke_color, 0)
        a.recycle()
    }

    fun setStrokeWidth(strokeWidth: Int) {
        this.strokeWidth = strokeWidth
    }

    fun setStrokeColor(strokeColor: Int) {
        this.strokeColor = strokeColor
    }

    fun setBackColor(backColor: Int) {
        this.backColor = backColor
    }

    fun setStartBackColor(startBackColor: Int) {
        this.startBackColor = startBackColor
    }

    fun setEndBackColor(endBackColor: Int) {
        this.endBackColor = endBackColor
    }

    fun update() {
        try {
            val drawable = GradientDrawable()
            if (backColor != 0) {
                drawable.setColor(backColor)
            } else if (startBackColor != 0 && endBackColor != 0) {
                drawable.colors = intArrayOf(startBackColor, endBackColor)
            }
            if (topLeftRadius != 0 || topRightRadius != 0 || bottomLeftRadius != 0 || bottomRightRadius != 0) {
                drawable.cornerRadii = floatArrayOf(
                    topLeftRadius.toFloat(), topLeftRadius.toFloat(),
                    topRightRadius.toFloat(), topRightRadius.toFloat(),
                    bottomRightRadius.toFloat(), bottomRightRadius.toFloat(),
                    bottomLeftRadius.toFloat(), bottomLeftRadius.toFloat()
                )
            }
            if (radius > 0) {
                drawable.cornerRadius = radius.toFloat()
            }
            if (strokeWidth > 0) {
                drawable.setStroke(strokeWidth, strokeColor)
            }
            view.background = drawable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}