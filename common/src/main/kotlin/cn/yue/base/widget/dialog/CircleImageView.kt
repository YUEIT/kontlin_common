package cn.yue.base.widget.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat

/**
 * Description :
 * Created by yue on 2019/6/19
 */
class CircleImageView : AppCompatImageView {

    @SuppressLint("WrongConstant")
    constructor(context: Context, color: Int, radius: Float) : super(context) {
        val density = getContext().resources.displayMetrics.density
        val diameter = (radius * density * 2f).toInt()
        val shadowYOffset = (density * offsetY).toInt()
        val shadowXOffset = (density * offsetX).toInt()

        mShadowRadius = (density * shadowRadius).toInt()
        val circle: ShapeDrawable
        val oval = OvalShadow(mShadowRadius, diameter)
        circle = ShapeDrawable(oval)
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, circle.paint)
        circle.paint.setShadowLayer(mShadowRadius.toFloat(), shadowXOffset.toFloat(), shadowYOffset.toFloat(),
                keyShadowColor)
        val padding = mShadowRadius
        // set padding so the inner image sits correctly within the shadow.
        setPadding(padding, padding, padding, padding)
        circle.paint.color = color
        setBackgroundDrawable(circle)
    }

    private val keyShadowColor = 0x1E000000
    private val fillShadowColor = 0x3D000000

    // PX
    private val offsetX = 0f
    private val offsetY = 1.75f
    private val shadowRadius = 2.5f
    private val shadowElevation = 4

    private var mListener: Animation.AnimationListener? = null
    private var mShadowRadius: Int = 0

    private fun elevationSupported(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= 21
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!elevationSupported()) {
            setMeasuredDimension(measuredWidth + mShadowRadius * 2, measuredHeight + mShadowRadius * 2)
        }
    }

    fun setAnimationListener(listener: Animation.AnimationListener) {
        mListener = listener
    }

    public override fun onAnimationStart() {
        super.onAnimationStart()
        if (mListener != null) {
            mListener!!.onAnimationStart(animation)
        }
    }

    public override fun onAnimationEnd() {
        super.onAnimationEnd()
        if (mListener != null) {
            mListener!!.onAnimationEnd(animation)
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    fun setBackgroundColorRes(colorRes: Int) {
        setBackgroundColor(context.resources.getColor(colorRes))
    }

    override fun setBackgroundColor(color: Int) {
        if (background is ShapeDrawable) {
            (background as ShapeDrawable).paint.color = color
        }
    }

    private inner class OvalShadow(shadowRadius: Int, private val mCircleDiameter: Int)
        : OvalShape() {
        private val mRadialGradient: RadialGradient
        private val mShadowPaint: Paint = Paint()

        init {
            mShadowRadius = shadowRadius
            mRadialGradient = RadialGradient((mCircleDiameter / 2).toFloat(), (mCircleDiameter / 2).toFloat(),
                    mShadowRadius.toFloat(), intArrayOf(fillShadowColor, Color.TRANSPARENT),
                    null, Shader.TileMode.CLAMP)
            mShadowPaint.shader = mRadialGradient
        }

        override fun draw(canvas: Canvas, paint: Paint) {
            val viewWidth = this@CircleImageView.width
            val viewHeight = this@CircleImageView.height
            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(),
                    (mCircleDiameter / 2 + mShadowRadius).toFloat(), mShadowPaint)
            canvas.drawCircle((viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(),
                    (mCircleDiameter / 2).toFloat(), paint)
        }
    }
}