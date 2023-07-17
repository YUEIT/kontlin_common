package cn.yue.base.photo.preview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.ViewConfiguration
import android.view.animation.Interpolator
import android.widget.OverScroller
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import cn.yue.base.common.R
import cn.yue.base.photo.data.MimeType
import cn.yue.base.utils.device.ScreenUtils
import cn.yue.base.utils.file.AndroidQFileUtils.getMediaUriFromName
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

/**
 * Description :
 * Created by yue on 2019/3/11
 */
class PhotoView(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : AppCompatImageView(context, attrs, defStyle) {
    private var mContext: Context? = null

    /**
     * 初始状态下控件的宽高
     */
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 初始状态下图片内容的宽高
     */
    private var mImageWidth = 0
    private var mImageHeight = 0

    /**
     * 图片内容在ImageView中的显示区域（包括缩放后）
     */
    private var mImageRectF: RectF? = null

    /**
     * 控件是否加载成功
     */
    private var isWidgetLoaded = false

    /**
     * 待缩放图片是否已经设置
     */
    private var isImageLoaded = false

    /**
     * 手势监听
     */
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mGestureDetector: GestureDetector? = null

    /**
     * 最小惯性滑动速度
     */
    private var mMinimumVelocity = 0

    /**
     * 最大惯性滑动速度
     */
    private var mMaximumVelocity = 0

    /**
     * 缩放焦点
     */
    private var focusX = 0f
    private var focusY = 0f

    /**
     * 是否单指操作（多指操作都交给缩放）
     */
    protected var isAlwaysSingleTouch = true

    /**
     * 惯性滑动工具
     */
    private var mFlingUtil: FlingUtil? = null

    /**
     * ZoomImageView的状态
     */
    private var mMatrix: Matrix? = null

    /**
     * 当前缩放比例
     */
    private var scale = ORIGINAL_SCALE

    /**
     * 是否触及左边界
     */
    private var isLeftSide = true

    /**
     * 是否触及右边界
     */
    private var isRightSide = true

    /**
     * 单击监听
     */
    private var mOnClickListener: OnClickListener? = null
    private fun init(context: Context) {
        mContext = context
        mMatrix = Matrix()
        mScaleGestureDetector = ScaleGestureDetector(context, mOnScaleGestureListener)
        mGestureDetector = GestureDetector(context, mOnGestureListener)
        val configuration = ViewConfiguration.get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        mFlingUtil = FlingUtil()
    }

    fun loadImage(uri: Uri) {
        loadImage(ScreenUtils.screenWidth, ScreenUtils.screenHeight, null, uri)
    }

    fun loadImage(url: String) {
        loadImage(ScreenUtils.screenWidth, ScreenUtils.screenHeight, url, null)
    }

    private fun loadImage(resizeX: Int, resizeY: Int, url: String?, uri: Uri?) {
        val requestOptions = RequestOptions()
                .override(resizeX, resizeY)
                .placeholder(R.drawable.drawable_default)
                .error(R.drawable.drawable_default)
                .fitCenter()
                .priority(Priority.HIGH)
        var builder: RequestBuilder<Drawable>? = null
        if (!TextUtils.isEmpty(url)) {
            builder = if (MimeType.isServerImage(url!!)) {
                Glide.with(context).load(url)
            } else {
                val localUri = getMediaUriFromName(url.substring(url.lastIndexOf("/") + 1), MimeType.getMimeType(url))
                Glide.with(context).load(localUri)
            }
        } else if (uri != null) {
            builder = Glide.with(context).load(uri)
        }
        if (builder == null) {
            return
        }
        builder.apply(requestOptions)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?,
                                              model: Any,
                                              target: Target<Drawable?>,
                                              isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?,
                                                 model: Any,
                                                 target: Target<Drawable?>,
                                                 dataSource: DataSource,
                                                 isFirstResource: Boolean): Boolean {
                        if (resource == null) {
                            return false
                        }
                        val preWidth = resource.intrinsicWidth
                        val preHeight = resource.intrinsicHeight
                        if (preWidth != mWidth || preHeight != mHeight) {
                            mWidth = preWidth
                            mHeight = preHeight
                            onRender(mWidth, mHeight)
                        }
                        return false
                    }
                })
                .into(this)
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.concat(mMatrix)
        super.onDraw(canvas)
        canvas.restoreToCount(saveCount)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            isAlwaysSingleTouch = true
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP) {
            pointerUp()
        }
        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            pointerUp()
        }
        if (event.pointerCount > 1) {
            mScaleGestureDetector!!.onTouchEvent(event)
            isAlwaysSingleTouch = false
        } else {
            if (!mScaleGestureDetector!!.isInProgress && isAlwaysSingleTouch) {
                mGestureDetector!!.onTouchEvent(event)
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val preWidth = MeasureSpec.getSize(widthMeasureSpec)
        val preHeight = MeasureSpec.getSize(heightMeasureSpec)
        var hasSizeChanged = false
        if (preWidth != mWidth || preHeight != mHeight) {
            hasSizeChanged = true
        }
        mWidth = preWidth
        mHeight = preHeight
        isWidgetLoaded = true
        //图片资源已有并且控件还没有加载 || 控件已经加载但控件尺寸发生变化
        val needUpdate = isImageLoaded && hasSizeChanged
        if (needUpdate) {
            setDrawableToView()
        }
    }

    /**
     * 获得缩放移动后的图片的位置区域
     *
     * @param matrix
     * @return RectF
     */
    private fun getScaledRect(matrix: Matrix?): RectF {
        val rectF = RectF(mImageRectF)
        //转化为缩放后的相对位置
        matrix!!.mapRect(rectF)
        return rectF
    }

    /**
     * 有手指离开，检查当前缩放值，并规范
     */
    private fun pointerUp() {
        if (scale < ORIGINAL_SCALE) {
            reset()
            checkBorder()
        } else if (scale > MAX_SCALE) {
            //超出最大后增加回弹
            val scaleFactor = MAX_SCALE / scale
            scale = MAX_SCALE
            mMatrix!!.postScale(scaleFactor, scaleFactor, focusX, focusY)
            invalidate()
            checkBorder()
        }
    }

    /**
     * 缩放时检查图片边缘，并添加相应的移动做调整
     */
    private fun constrainMatrix(cdx: Float = 0f, cdy: Float = 0f) {
        var dx = cdx
        var dy = cdy
        val rectF = getScaledRect(mMatrix)
        val scaleImageWidth = rectF.width()
        val scaleImageHeight = rectF.height()
        if (scaleImageWidth > mWidth) {
            //right
            if (rectF.right + dx < mWidth) {
                dx = -rectF.right + mWidth
            }
            //left
            if (rectF.left + dx > 0) {
                dx = -rectF.left
            }
        } else {
            //center
            dx = -rectF.left + (mWidth.toFloat() - scaleImageWidth) / 2
        }
        if (scaleImageHeight > mHeight) {
            //bottom
            if (rectF.bottom + dy < mHeight) {
                dy = -rectF.bottom + mHeight
            }
            //top
            if (rectF.top + dy > 0) {
                dy = -rectF.top
            }
        } else {
            //center
            dy = -rectF.top + (mHeight.toFloat() - scaleImageHeight) / 2
        }
        mMatrix!!.postTranslate(dx, dy)
        invalidate()
        checkBorder()
    }

    /**
     * 检查图片边界
     */
    private fun checkBorder() {
        val rectF = getScaledRect(mMatrix)
        isLeftSide = rectF.left >= 0
        isRightSide = rectF.right <= mWidth
    }

    /**
     * 缩放手势监听
     */
    private val mOnScaleGestureListener: OnScaleGestureListener = object : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (!isImageLoaded) {
                return false
            }
            val scaleFactor = detector.scaleFactor
            val wantScale: Float = scale * scaleFactor
            if (wantScale >= MIN_SCALE) {
                scale = wantScale
                focusX = detector.focusX
                focusY = detector.focusY
                mMatrix!!.postScale(scaleFactor, scaleFactor, focusX, focusY)
                invalidate()
                constrainMatrix()
            }
            return true
        }
    }

    /**
     * 简单手势监听
     */
    private val mOnGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            if (!isAlwaysSingleTouch) {
                return true
            }
            forceFinishScroll()
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!isAlwaysSingleTouch) {
                return true
            }
            if (!isImageLoaded) {
                return true
            }
            constrainMatrix(-distanceX, -distanceY)
            checkBorder()
            return false
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (mOnClickListener != null) {
                mOnClickListener!!.onClick(this@PhotoView)
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (!isAlwaysSingleTouch) {
                return true
            }
            if (!isImageLoaded) {
                return true
            }
            val x = e.x
            val y = e.y
            if (scale == ORIGINAL_SCALE) {
                val scaleFactor: Float = MAX_SCALE / scale
                scale = MAX_SCALE
                mMatrix!!.postScale(scaleFactor, scaleFactor, x, y)
                invalidate()
                checkBorder()
            } else {
                reset()
            }
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (!isAlwaysSingleTouch) {
                return true
            }
            if (!isImageLoaded) {
                return true
            }
            if (scale == ORIGINAL_SCALE) {
                return true
            }
            var absVelocityX = Math.abs(velocityX)
            absVelocityX = if (absVelocityX < mMinimumVelocity) {
                0f
            } else {
                Math.max(mMinimumVelocity.toFloat(), Math.min(absVelocityX, mMaximumVelocity.toFloat()))
            }
            var absVelocityY = Math.abs(velocityY)
            absVelocityY = if (absVelocityY < mMinimumVelocity) {
                0f
            } else {
                Math.max(mMinimumVelocity.toFloat(), Math.min(absVelocityY, mMaximumVelocity.toFloat()))
            }
            if (absVelocityX != 0f || absVelocityY != 0f) {
                mFlingUtil!!.fling((if (velocityX > 0) absVelocityX else -absVelocityX).toInt(),
                    (if (velocityY > 0) absVelocityY else -absVelocityY).toInt())
            }
            return true
        }
    }

    /**
     * 强制停止控件的惯性滑动
     */
    private fun forceFinishScroll() {
        mFlingUtil!!.stop()
    }

    /**
     * 惯性滑动工具类
     * 使用fling方法开始滑动
     * 使用stop方法停止滑动
     */
    private inner class FlingUtil : Runnable {
        private var mLastFlingX = 0
        private var mLastFlingY = 0
        private val mScroller: OverScroller
        private var mEatRunOnAnimationRequest = false
        private var mReSchedulePostAnimationCallback = false

        /**
         * RecyclerView使用的惯性滑动插值器
         * f(x) = (x-1)^5 + 1
         */
        private val sQuinticInterpolator = Interpolator {
            var t = it
            t -= 1.0f
            t * t * t * t * t + 1.0f
        }

        override fun run() {
            disableRunOnAnimationRequests()
            val scroller = mScroller
            if (scroller.computeScrollOffset()) {
                val y = scroller.currY
                val dy = y - mLastFlingY
                val x = scroller.currX
                val dx = x - mLastFlingX
                mLastFlingY = y
                mLastFlingX = x
                constrainMatrix(dx.toFloat(), dy.toFloat())
                postOnAnimation()
            }
            enableRunOnAnimationRequests()
        }

        fun fling(velocityX: Int, velocityY: Int) {
            mLastFlingX = 0
            mLastFlingY = 0
            mScroller.fling(0, 0, velocityX, velocityY, Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)
            postOnAnimation()
        }

        fun stop() {
            removeCallbacks(this)
            mScroller.abortAnimation()
        }

        private fun disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false
            mEatRunOnAnimationRequest = true
        }

        private fun enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation()
            }
        }

        fun postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true
            } else {
                removeCallbacks(this)
                ViewCompat.postOnAnimation(this@PhotoView, this)
            }
        }

        init {
            mScroller = OverScroller(context, sQuinticInterpolator)
        }
    }

    fun onRender(width: Int, height: Int) {
        mImageWidth = width
        mImageHeight = height
        isImageLoaded = true
        if (isWidgetLoaded) {
            setDrawableToView()
        }
    }

    private fun setDrawableToView() {
        val imageRatio = mImageWidth.toFloat() / mImageHeight.toFloat()
        val widgetRatio = mWidth.toFloat() / mHeight.toFloat()
        if (imageRatio > widgetRatio) {
            mImageHeight = mWidth * mImageHeight / mImageWidth
            mImageWidth = mWidth
        } else {
            mImageWidth = mHeight * mImageWidth / mImageHeight
            mImageHeight = mHeight
        }
        mImageRectF = RectF((mWidth - mImageWidth).toFloat() / 2,
            (mHeight - mImageHeight).toFloat() / 2,
            (mWidth + mImageWidth).toFloat() / 2,
            (mHeight + mImageHeight).toFloat() / 2)
    }

    /**
     * reset the image
     */
    fun reset() {
        mMatrix!!.reset()
        scale = ORIGINAL_SCALE
        isLeftSide = true
        isRightSide = true
        invalidate()
    }

    private val isOriginalSize: Boolean = scale == ORIGINAL_SCALE

    /**
     * for method 'canScroll'
     *
     * @param direction
     * @return
     */
    fun canScroll(direction: Int): Boolean {
        return !(direction < 0 && isRightSide || direction > 0 && isLeftSide)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        mOnClickListener = listener
    }

    companion object {
        private const val TAG = "PhotoView"

        /**
         * 最小缩放比例
         */
        private const val MIN_SCALE = 0.95f

        /**
         * 最大缩放比例
         */
        private const val MAX_SCALE = 3.0f

        /**
         * 初始比例
         */
        private const val ORIGINAL_SCALE = 1.0f

        /**
         * 下拉退出能达到的最小缩放比例
         */
        private const val MIN_PULL_SCALE = 0.2f

        /**
         * 下拉退出的临界缩放比例
         */
        private const val PULL_FINISH_SCALE = 0.7f
    }

    init {
        init(context)
    }
}