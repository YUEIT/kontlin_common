package cn.yue.base.widget.shape

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import cn.yue.base.R


/**
 * Description :
 * Created by yue on 2022/1/14
 */

class ShapeFrameLayout(context: Context, attributeSet: AttributeSet? = null)
    : FrameLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeLinearLayout(context: Context, attributeSet: AttributeSet? = null)
    : LinearLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeRelativeLayout(context: Context, attributeSet: AttributeSet? = null)
    : RelativeLayout(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeConstraintLayout(context: Context, attributeSet: AttributeSet? = null)
    : ConstraintLayout(context, attributeSet) {
    
    private val shapeDelegate = ShapeDelegate(this, attributeSet)
    
    init {
        shapeDelegate.update()
    }
    
    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}

class ShapeTextView(context: Context, attributeSet: AttributeSet? = null)
    : androidx.appcompat.widget.AppCompatTextView(context, attributeSet) {

    private val shapeDelegate = ShapeDelegate(this, attributeSet)

    init {
        shapeDelegate.update()
    }

    fun getShapeDelegate(): ShapeDelegate {
        return shapeDelegate
    }
}


class ShapeDelegate(val view: View, attributeSet: AttributeSet?) {

    private var radius: Int = 0
    private var topLeftRadius: Int = 0
    private var topRightRadius: Int = 0
    private var bottomRightRadius: Int = 0
    private var bottomLeftRadius: Int = 0
    private var backColor: Int = 0
    private var startBackColor: Int = 0
    private var centerBackColor: Int = 0
    private var endBackColor: Int = 0
    private var strokeWidth: Int = 0
    private var strokeColor: Int = 0
    private var orientation = GradientDrawable.Orientation.TOP_BOTTOM

    init {
        val a = view.context.obtainStyledAttributes(attributeSet, R.styleable.ShapeBackground)
        radius = a.getDimensionPixelSize(R.styleable.ShapeBackground_shape_radius, 0)
        topLeftRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_shape_radius_tl, 0)
        topRightRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_shape_radius_tr, 0)
        bottomLeftRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_shape_radius_bl, 0)
        bottomRightRadius = a.getDimensionPixelOffset(R.styleable.ShapeBackground_shape_radius_br, 0)
        backColor = a.getColor(R.styleable.ShapeBackground_shape_color, 0)
        startBackColor = a.getColor(R.styleable.ShapeBackground_shape_start_color, 0)
        centerBackColor = a.getColor(R.styleable.ShapeBackground_shape_start_color, 0)
        endBackColor = a.getColor(R.styleable.ShapeBackground_shape_end_color, 0)
        strokeWidth = a.getDimensionPixelOffset(R.styleable.ShapeBackground_shape_stroke_width, 0)
        strokeColor = a.getColor(R.styleable.ShapeBackground_shape_stroke_color, 0)
        val angle = a.getFloat(R.styleable.ShapeBackground_shape_angle, -1f).toInt()
        if (angle >= 0) {
            when (angle) {
                0 -> orientation = GradientDrawable.Orientation.LEFT_RIGHT
                45 -> orientation = GradientDrawable.Orientation.BL_TR
                90 -> orientation = GradientDrawable.Orientation.BOTTOM_TOP
                135 -> orientation = GradientDrawable.Orientation.BR_TL
                180 -> orientation = GradientDrawable.Orientation.RIGHT_LEFT
                225 -> orientation = GradientDrawable.Orientation.TR_BL
                270 -> orientation = GradientDrawable.Orientation.TOP_BOTTOM
                315 -> orientation = GradientDrawable.Orientation.TL_BR
            }
        } else {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
        }
        a.recycle()
    }
    
    fun setStrokeWidth(strokeWidth: Int): ShapeDelegate {
        this.strokeWidth = strokeWidth
        return this
    }
    
    fun setStrokeColor(strokeColor: Int): ShapeDelegate {
        this.strokeColor = strokeColor
        return this
    }
    
    fun setBackColor(backColor: Int): ShapeDelegate {
        this.backColor = backColor
        return this
    }
    
    fun setStartBackColor(startBackColor: Int): ShapeDelegate {
        this.startBackColor = startBackColor
        this.backColor = 0
        return this
    }
    
    fun setCenterBackColor(centerBackColor: Int): ShapeDelegate {
        this.centerBackColor = centerBackColor
        this.backColor = 0
        return this
    }
    
    fun setEndBackColor(endBackColor: Int): ShapeDelegate {
        this.endBackColor = endBackColor
        this.backColor = 0
        return this
    }
    
    fun setOrientation(orientation:  GradientDrawable.Orientation): ShapeDelegate {
        this.orientation = orientation
        return this
    }
    
    fun setRadius(radius: Int): ShapeDelegate {
        this.radius = radius
        topLeftRadius = 0
        topRightRadius = 0
        bottomLeftRadius = 0
        bottomRightRadius = 0
        return this
    }

    fun update() {
        try {
            val drawable = GradientDrawable()
            if (backColor != 0) {
                drawable.setColor(backColor)
            } else if (startBackColor != 0 && centerBackColor != 0 && endBackColor != 0) {
                drawable.colors = intArrayOf(startBackColor, centerBackColor, endBackColor)
                drawable.orientation = orientation
            } else if (startBackColor != 0 && endBackColor != 0) {
                drawable.colors = intArrayOf(startBackColor, endBackColor)
                drawable.orientation = orientation
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