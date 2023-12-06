package cn.yue.base.widget.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import cn.yue.base.R

class RoundImageView(context: Context, attrs: AttributeSet? = null)
    : AppCompatImageView(context, attrs) {

    private val typeRound = 0
    private val typeCircle = 1
    /**
     * 圆角大小的默认值
     */
    private val borderRadiusDefault = 10f
    /**
     * 图片的类型，圆形or圆角
     */
    private var type = typeRound
    /**
     * 圆角的大小
     */
    private var mBorderRadius: Float = 0f
    //左上角圆角大小
    private var mTopLeftRadius = 0f

    //右上角圆角大小
    private var mTopRightRadius = 0f

    //左下角圆角大小
    private var mBottomLeftRadius = 0f

    //右下角圆角大小
    private var mBottomRightRadius = 0f
    private val mBitmapPaint = Paint()
    private val mForePaint = Paint()
    private val mBorderPaint = Paint()
    /**
     * 圆角的半径
     */
    private var mRadius: Float = 0f
    private var mBorderColor: Int = 0
    private var mBorderWidth: Int = 0
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private var mMatrix: Matrix = Matrix()
    private var mWidth: Int = 0
    private var mRoundRect = RectF()
    private var mRoundPath = Path()
    private var mRoundBorderRect = RectF()
    private var mRoundBorderPath = Path()
    /**
     * View的foreground需要M，这里自定义一个
     */
    private var mForeground: Drawable? = null

    init {
        mBitmapPaint.isAntiAlias = true
        mForePaint.isAntiAlias = true
        val a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        mBorderRadius = a.getDimension(R.styleable.RoundImageView_border_radius, borderRadiusDefault)// 默认为10dp
        mTopLeftRadius = a.getDimension(R.styleable.RoundImageView_border_radius_tl, 0f)
        mBottomLeftRadius = a.getDimension(R.styleable.RoundImageView_border_radius_bl, 0f)
        mTopRightRadius = a.getDimension(R.styleable.RoundImageView_border_radius_tr, 0f)
        mBottomRightRadius = a.getDimension(R.styleable.RoundImageView_border_radius_br, 0f)
        type = a.getInt(R.styleable.RoundImageView_view_type, typeRound)
        mBorderColor = a.getColor(R.styleable.RoundImageView_border_color, Color.TRANSPARENT)
        mBorderWidth = a.getDimensionPixelOffset(R.styleable.RoundImageView_border_width, 0)
        mForeground = a.getDrawable(R.styleable.RoundImageView_foreground)
        a.recycle()
        setScaleType(scaleType)
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != ScaleType.CENTER_CROP && scaleType != ScaleType.FIT_XY) {
            super.setScaleType(ScaleType.CENTER_CROP)
        } else {
            super.setScaleType(scaleType)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //如果类型是圆形，则强制改变view的宽高一致，以小值为准
        if (type == typeCircle) {
            mWidth = Math.min(measuredWidth, measuredHeight)
            mRadius = mWidth / 2f
            setMeasuredDimension(mWidth, mWidth)
        }
    }

    /**
     * 涉及缩放  支持其他模式，参考ImageView 中configureBounds中的操作添加
     */
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

        if (ScaleType.FIT_XY == scaleType) {
            scaleX = width.toFloat() / bmp.width.toFloat()
            scaleY = height.toFloat() / bmp.height.toFloat()
            mMatrix.setScale(scaleX, scaleY)
        }
        if (ScaleType.CENTER_CROP == scaleType) {
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
        mBorderPaint.color = mBorderColor
        mBorderPaint.style = Paint.Style.STROKE
        mBorderPaint.isAntiAlias = true
        mBorderPaint.strokeWidth = mBorderWidth.toFloat()
    }

    private fun setForePaint() {
        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mForeground != null) {
            mForeground
        } else {
            ColorDrawable(Color.TRANSPARENT)
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

    private fun setUpPaint() {
        setBitmapPaint()
        setBorderPaint()
        setForePaint()
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            return
        }
        if (type == typeRound) {
            setRoundPath()
            canvas.drawPath(mRoundPath, mBitmapPaint)
            canvas.drawPath(mRoundPath, mForePaint)
            if (mBorderWidth > 0) {
                canvas.drawPath(mRoundBorderPath, mBorderPaint)
            }
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint)
            canvas.drawCircle(mRadius, mRadius, mRadius, mForePaint)
            if (mBorderWidth > 0) {
                canvas.drawCircle(
                    mRadius,
                    mRadius,
                    mRadius - (mBorderWidth / 2),
                    mBorderPaint
                )
            }
            // drawSomeThing(canvas);
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        //super.onDrawForeground(canvas);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 圆角图片的范围
        if (type == typeRound) {
            mRoundRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
            mRoundBorderRect = RectF(mBorderWidth / 2f,
                mBorderWidth / 2f,
                w.toFloat() - (mBorderWidth / 2),
                h.toFloat() - (mBorderWidth / 2))
        }
        setUpPaint()
    }

    private fun setRoundPath() {
        mRoundPath.reset()
        mRoundBorderPath.reset()
        /**
         * 如果四个圆角大小都是默认值0，
         * 则将四个圆角大小设置为mCornerRadius的值
         */
        if (mTopLeftRadius == 0f && mTopRightRadius == 0f
            && mBottomLeftRadius == 0f && mBottomRightRadius == 0f) {
            mRoundPath.addRoundRect(
                mRoundRect, floatArrayOf(
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius
                ),
                Path.Direction.CW
            )
            mRoundBorderPath.addRoundRect(
                mRoundBorderRect, floatArrayOf(
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius,
                    mBorderRadius, mBorderRadius
                ),
                Path.Direction.CW
            )
        } else {
            mRoundPath.addRoundRect(
                mRoundRect, floatArrayOf(
                    mTopLeftRadius, mTopLeftRadius,
                    mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius,
                    mBottomLeftRadius, mBottomLeftRadius
                ),
                Path.Direction.CW
            )
            mRoundBorderPath.addRoundRect(
                mRoundBorderRect, floatArrayOf(
                    mTopLeftRadius, mTopLeftRadius,
                    mTopRightRadius, mTopRightRadius,
                    mBottomRightRadius, mBottomRightRadius,
                    mBottomLeftRadius, mBottomLeftRadius
                ),
                Path.Direction.CW
            )
        }
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
        if (w <= 0 || h <= 0) {
            w = measuredWidth
            h = measuredHeight
            if (w <= 0 || h <= 0) {
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

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (width > 0 && height > 0) {
            setUpPaint()
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (width > 0 && height > 0) {
            setUpPaint()
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (width > 0 && height > 0) {
            setUpPaint()
        }
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        setUpPaint()
    }

    private val stateInstance = "state_instance"
    private val stateType = "state_type"
    private val stateBorderRadius = "state_border_radius"
    private val stateTopLeftRadius = "state_border_radius_tl"
    private val stateTopRightRadius = "state_border_radius_tr"
    private val stateBottomLeftRadius = "state_border_radius_bl"
    private val stateBottomRightRadius = "state_border_radius_br"
    private val stateBorderWidth = "state_border_width"
    private val stateBorderColor = "state_border_color"

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(stateInstance, super.onSaveInstanceState())
        bundle.putInt(stateType, type)
        bundle.putFloat(stateBorderRadius, mBorderRadius)
        bundle.putFloat(stateTopLeftRadius, mTopLeftRadius)
        bundle.putFloat(stateTopRightRadius, mTopRightRadius)
        bundle.putFloat(stateBottomLeftRadius, mBottomLeftRadius)
        bundle.putFloat(stateBottomRightRadius, mBottomRightRadius)
        bundle.putInt(stateBorderWidth, mBorderWidth)
        bundle.putInt(stateBorderColor, mBorderColor)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(stateInstance))
            this.type = state.getInt(stateType)
            this.mBorderRadius = state.getFloat(stateBorderRadius)
            mTopLeftRadius = state.getFloat(stateTopLeftRadius)
            mTopRightRadius = state.getFloat(stateTopRightRadius)
            mBottomLeftRadius = state.getFloat(stateBottomLeftRadius)
            mBottomRightRadius = state.getFloat(stateBottomRightRadius)
            mBorderWidth = state.getInt(stateBorderWidth)
            mBorderColor = state.getInt(stateBorderColor)
        } else {
            super.onRestoreInstanceState(state)
        }

    }

    fun setBorderRadius(borderRadius: Float) {
        val pxVal = dp2px(borderRadius)
        if (this.mBorderRadius != pxVal) {
            this.mBorderRadius = pxVal
            invalidate()
        }
    }

    fun setBorderStyle(width: Int, color: Int) {
        mBorderColor = color
        mBorderWidth = width
        setBorderPaint()
        invalidate()
    }

    fun setBorderColor(color: Int) {
        mBorderColor = color
        setBorderPaint()
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

    private fun dp2px(dpVal: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dpVal, resources.displayMetrics)
    }

}
