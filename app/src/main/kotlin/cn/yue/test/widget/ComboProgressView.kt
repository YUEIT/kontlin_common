package cn.yue.test.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import cn.yue.test.R

class ComboProgressView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    /**
     * 画笔
     */
    private var mPaint = Paint()

    /**
     * 笔画描边的宽度
     */
    private var mStrokeWidth = 0f

    /**
     * 开始角度(默认从12点钟方向开始)
     */
    private var mStartAngle = 240

    /**
     * 扫描角度(一个圆)
     */
    private var mSweepAngle = 300

    /**
     * 圆心坐标x
     */
    private var mCircleCenterX = 0f

    /**
     * 圆心坐标y
     */
    private var mCircleCenterY = 0f

    /**
     * 弧形 正常颜色
     */
    private var mNormalColor = -0x373738

    /**
     * 着色器
     */
    private var mShader: Shader? = null

    /**
     * 着色器颜色
     */
    private var mShaderColors = intArrayOf(-0xb01554, -0x5722af, -0x172cf1, -0x5722af, -0xb01554)

    /**
     * 半径
     */
    private var mRadius = 0f

    /**
     * 刻度与弧形的间距
     */
    private var mTickPadding = 0f

    /**
     * 刻度间隔的角度大小
     */
    private var mTickSplitAngle = 1

    /**
     * 刻度的角度大小
     */
    private var mBlockAngle = 5

    /**
     * 刻度偏移的角度大小
     */
    private var mTickOffsetAngle = 0

    /**
     * 总刻度数
     */
    private var mTotalTickCount = 0

    /**
     * 度数画笔宽度
     */
    private var mTickStrokeWidth = 0f

    /**
     * 最大进度
     */
    private var mMax = 100

    /**
     * 当前进度
     */
    private var mProgress = 100

    private var isMeasureCircle = false

    init {
        val displayMetrics: DisplayMetrics = getDisplayMetrics()
        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, displayMetrics)
        mTickPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, displayMetrics)
        mTickStrokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, displayMetrics)
        mPaint = Paint()
        mTotalTickCount = (mSweepAngle / (mTickSplitAngle + mBlockAngle))
    }


    private fun getDisplayMetrics(): DisplayMetrics {
        return resources.displayMetrics
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val defaultValue =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200f, getDisplayMetrics())
                .toInt()
        val width = measureHandler(widthMeasureSpec, defaultValue)
        val height = measureHandler(heightMeasureSpec, defaultValue)

        //圆心坐标
        mCircleCenterX = (width + paddingLeft - paddingRight) / 2.0f
        mCircleCenterY = (height + paddingTop - paddingBottom) / 2.0f
        //计算间距
        val padding = Math.max(paddingLeft + paddingRight, paddingTop + paddingBottom)
        //半径=视图宽度-横向或纵向内间距值 - 画笔宽度
        mRadius = (width - padding - mStrokeWidth) / 2.0f

        //默认着色器
        if (mShader == null) {
            mShader = SweepGradient(mCircleCenterX, mCircleCenterX, mShaderColors, null)
        }
        isMeasureCircle = true
        setMeasuredDimension(width, height)
    }

    /**
     * 测量
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private fun measureHandler(measureSpec: Int, defaultSize: Int): Int {
        var result = defaultSize
        val measureMode = MeasureSpec.getMode(measureSpec)
        val measureSize = MeasureSpec.getSize(measureSpec)
        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize
        } else if (measureMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, measureSize)
        }
        return result
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArc(canvas)
    }


    /**
     * 绘制弧形(默认为一个圆)
     * @param canvas
     */
    private fun drawArc(canvas: Canvas) {
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE

        mPaint.strokeWidth = mTickStrokeWidth
        val circleRadius: Float = mRadius
        val tickDiameter = circleRadius * 2
        val tickStartX: Float = mCircleCenterX - circleRadius
        val tickStartY: Float = mCircleCenterY - circleRadius
        val rectF =
            RectF(tickStartX, tickStartY, tickStartX + tickDiameter, tickStartY + tickDiameter)
        val currentBlockIndex = (mProgress / 100f * mTotalTickCount)
        //start 30 -> end 330
        for (i in 0 until mTotalTickCount) {
            if (i < currentBlockIndex) {
                //已选中的刻度
                if (mShader != null) {
                    mPaint.shader = mShader
                }
                //绘制刻度
                canvas.drawArc(
                    rectF,
                    (mStartAngle - i * (mBlockAngle + mTickSplitAngle) - mTickOffsetAngle).toFloat(),
                    mBlockAngle.toFloat(),
                    false,
                    mPaint
                )
            } else {
//                if (mNormalColor != 0) {
//                    //未选中的刻度
//                    mPaint.shader = null
//                    mPaint.color = mNormalColor
//                    //绘制刻度
//                    canvas.drawArc(
//                        rectF,
//                        (i * (mBlockAngle + mTickSplitAngle) + mStartAngle + mTickOffsetAngle).toFloat(),
//                        mBlockAngle.toFloat(),
//                        false,
//                        mPaint
//                    )
//                }
            }
        }
    }

    /**
     * 进度比例
     * @return
     */
    private fun getRatio(): Float {
        return mProgress * 1.0f / mMax
    }

    /**
     * 设置最大进度
     * @param max
     */
    fun setMax(max: Int) {
        if (max > 0) {
            this.mMax = max
            invalidate()
        }
    }

    /**
     * 设置当前进度
     * @param progress
     */
    fun setProgress(progress: Int) {
        var mProgress = progress
        if (progress < 0) {
            mProgress = 0
        } else if (progress > mMax) {
            mProgress = mMax
        }
        this.mProgress = mProgress
        invalidate()
    }

    /**
     * 设置正常颜色
     * @param color
     */
    fun setNormalColor(color: Int) {
        this.mNormalColor = color
        invalidate()
    }


    /**
     * 设置着色器
     * @param shader
     */
    fun setShader(shader: Shader) {
        this.mShader = shader
        invalidate()
    }

    /**
     * 设置进度颜色（通过着色器实现渐变色）
     * @param colors
     */
    fun setProgressColor(vararg colors: Int) {
        if (isMeasureCircle) {
            val shader: Shader = SweepGradient(mCircleCenterX, mCircleCenterX, colors, null)
            setShader(shader)
        } else {
            mShaderColors = colors
        }
    }

    fun getProgress(): Int {
        return mProgress
    }
}