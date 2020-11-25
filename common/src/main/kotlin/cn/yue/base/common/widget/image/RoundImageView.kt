package cn.yue.base.common.widget.image

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import cn.yue.base.common.R

class RoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : AppCompatImageView(context, attrs) {

    private val typeRound = 0
    private val typeCircle = 1
    /**
     * 圆角大小的默认值
     */
    private val borderRadiusDefault = 10
    /**
     * 图片的类型，圆形or圆角
     */
    private var type = typeRound
    /**
     * 圆角的大小
     */
    private var mBorderRadius: Int = 0
    private val mBitmapPaint: Paint = Paint()
    private val mForePaint = Paint()
    private val mBorderPaint = Paint()
    /**
     * 圆角的半径
     */
    private var mRadius: Int = 0
    private var borderColor: Int = 0
    private var borderWidth: Int = 0
    // 3x3 矩阵，主要用于缩小放大
    private var mMatrix: Matrix = Matrix()
    private var mWidth: Int = 0
    private var mRoundRect: RectF? = null
    private var mForeground: Drawable? = null//View的foreground需要M，这里自定义一个

    init {
        mBitmapPaint.isAntiAlias = true
        mForePaint.isAntiAlias = true
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_border_radius, TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        borderRadiusDefault.toFloat(), resources.displayMetrics).toInt())// 默认为10dp
        type = a.getInt(R.styleable.RoundImageView_view_type, typeRound)
        borderColor = a.getColor(R.styleable.RoundImageView_border_color, Color.TRANSPARENT)
        borderWidth = a.getDimensionPixelOffset(R.styleable.RoundImageView_border_width, 0)
        mForeground = a.getDrawable(R.styleable.RoundImageView_foreground)
        a.recycle()
        scaleType = scaleType
    }


    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != ScaleType.CENTER_CROP && scaleType != ScaleType.FIT_XY) {
            super.setScaleType(ScaleType.CENTER_CROP)
        }
        super.setScaleType(scaleType)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //如果类型是圆形，则强制改变view的宽高一致，以小值为准
        if (type == typeCircle) {
            mWidth = Math.min(measuredWidth, measuredHeight)
            mRadius = mWidth / 2
            setMeasuredDimension(mWidth, mWidth)
        }
    }

    //涉及缩放  支持其他模式，参考ImageView 中configureBounds中的操作添加
    private fun getBitmapShader(drawable: Drawable): BitmapShader {
        val bmp = drawableToBitmap(drawable)
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        val mBitmapShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        var scale = 1.0f
        var scaleX = 1.0f
        var scaleY = 1.0f

        val dwidth = bmp.width
        val dheight = bmp.height
        val vwidth = width - paddingLeft - paddingRight
        val vheight = height - paddingTop - paddingBottom

        if (ImageView.ScaleType.FIT_XY == scaleType) {
            scaleX = width.toFloat() / bmp.width.toFloat()
            scaleY = height.toFloat() / bmp.height.toFloat()
            mMatrix.setScale(scaleX, scaleY)
        }
        if (ImageView.ScaleType.CENTER_CROP == scaleType) {
            var dx = 0f
            var dy = 0f
            if (dwidth * vheight > vwidth * dheight) {
                scale = vheight.toFloat() / dheight.toFloat()
                dx = (vwidth - dwidth * scale) * 0.5f
            } else {
                scale = vwidth.toFloat() / dwidth.toFloat()
                dy = (vheight - dheight * scale) * 0.5f
            }
            mMatrix.setScale(scale, scale)
            mMatrix.postTranslate(Math.round(dx).toFloat(), Math.round(dy).toFloat())
        }
        return mBitmapShader
    }

    private fun setBitmapPaint() {
        val drawable = drawable ?: return
        val bitmapShader = getBitmapShader(drawable)
        // 设置变换矩阵
        bitmapShader.setLocalMatrix(mMatrix)
        // 设置shader
        mBitmapPaint.shader = bitmapShader
    }

    private fun setBorderPaint() {
        mBorderPaint.color = borderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.strokeWidth = borderWidth.toFloat()
    }

    private fun setForePaint() {
        val drawable: Drawable?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mForeground != null) {
            drawable = mForeground
        } else {
            drawable = ColorDrawable(Color.TRANSPARENT)
        }
        if (drawable == null) {
            return
        }
        val bitmapShader = getBitmapShader(drawable)
        // 设置变换矩阵
        bitmapShader.setLocalMatrix(mMatrix)
        // 设置shader
        mForePaint.shader = bitmapShader
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        setBitmapPaint()
        setBorderPaint()
        setForePaint()
        if (type == typeRound) {
            canvas.drawRoundRect(mRoundRect!!, mBorderRadius.toFloat(), mBorderRadius.toFloat(),
                    mBitmapPaint)
            canvas.drawRoundRect(mRoundRect!!, mBorderRadius.toFloat(), mBorderRadius.toFloat(),
                    mForePaint)
            if (borderWidth > 0) {
                canvas.drawRoundRect(mRoundRect!!, mBorderRadius.toFloat(), mBorderRadius.toFloat(),
                        mBorderPaint)
            }
        } else {
            canvas.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mBitmapPaint)
            canvas.drawCircle(mRadius.toFloat(), mRadius.toFloat(), mRadius.toFloat(), mForePaint)
            // drawSomeThing(canvas);
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        //super.onDrawForeground(canvas);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 圆角图片的范围
        if (type == typeRound) mRoundRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    }

    /**
     * drawable转bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        var w = drawable.intrinsicWidth
        var h = drawable.intrinsicHeight
        if (w < 0 || h < 0) {
            w = measuredWidth
            h = measuredHeight
            if (w < 0 || h < 0) {
                w = 1
                h = 1
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

    private val stateInstance = "state_instance"
    private val stateType = "state_type"
    private val stateBorderRadius = "state_border_radius"

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(stateInstance, super.onSaveInstanceState())
        bundle.putInt(stateType, type)
        bundle.putInt(stateBorderRadius, mBorderRadius)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(stateInstance))
            this.type = state.getInt(stateType)
            this.mBorderRadius = state.getInt(stateBorderRadius)
        } else {
            super.onRestoreInstanceState(state)
        }

    }

    fun setBorderRadius(borderRadius: Int) {
        val pxVal = dp2px(borderRadius)
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal
            invalidate()
        }
    }

    fun setBorderStyle(width: Int, color: Int) {
        borderColor = color
        borderWidth = width
        invalidate()
    }


    fun setType(type: Int) {
        if (this.type != type) {
            this.type = type
            if (this.type != typeRound && this.type != typeCircle) {
                this.type = typeCircle
            }
            requestLayout()
        }
    }

    private fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal.toFloat(), resources.displayMetrics).toInt()
    }

}
