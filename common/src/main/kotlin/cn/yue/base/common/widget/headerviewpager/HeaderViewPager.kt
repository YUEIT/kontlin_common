package cn.yue.base.common.widget.headerviewpager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.yue.base.common.R


/**
 * 介绍：带有头部的ViewPager
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/10/26.
 */

class HeaderViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    private var topOffset = 0      //滚动的最大偏移量

    private val mScroller: Scroller?
    private val mTouchSlop: Int         //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
    private val mMinimumVelocity: Int   //允许执行一个fling手势动作的最小速度值
    private val mMaximumVelocity: Int   //允许执行一个fling手势动作的最大速度值
    private val sysVersion: Int         //当前sdk版本，用于判断api版本
    private var mHeadView: View? = null         //需要被滑出的头部
    private var mHeadHeight: Int = 0        //滑出头部的高度
    var maxY = 0
        private set           //最大滑出的距离，等于 mHeadHeight
    private val minY = 0           //最小的距离， 头部在最顶部
    private var mCurY: Int = 0              //当前已经滚动的距离
    private var mVelocityTracker: VelocityTracker? = null
    private var mDirection: Int = 0
    private var mLastScrollerY: Int = 0
    private var mDisallowIntercept: Boolean = false  //是否允许拦截事件
    private var isClickHead: Boolean = false         //当前点击区域是否在头部
    private var onScrollListener: OnScrollListener? = null   //滚动的监听
    private val mScrollable: HeaderScrollHelper

    private var mDownX: Float = 0.toFloat()  //第一次按下的x坐标
    private var mDownY: Float = 0.toFloat()  //第一次按下的y坐标
    private var mLastY: Float = 0.toFloat()  //最后一次移动的Y坐标
    private var verticalScrollFlag = false   //是否允许垂直滚动
    //解决SwipeRefreshLayout与其滑动冲突
    private var refreshLayout: SwipeRefreshLayout? = null

    /** 头部是否已经固定  */
    val isStickied: Boolean
        get() = mCurY == maxY

    val isHeadTop: Boolean
        get() = mCurY == minY

    interface OnScrollListener {
        fun onScroll(currentY: Int, maxY: Int)
    }

    fun setOnScrollListener(onScrollListener: OnScrollListener) {
        this.onScrollListener = onScrollListener
    }

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.HeaderViewPager)
        topOffset = a.getDimensionPixelSize(a.getIndex(R.styleable.HeaderViewPager_hvp_topOffset), topOffset)
        a.recycle()

        mScroller = Scroller(context)
        mScrollable = HeaderScrollHelper()
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop   //表示滑动的时候，手的移动要大于这个距离才开始移动控件。
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity //允许执行一个fling手势动作的最小速度值
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity //允许执行一个fling手势动作的最大速度值
        sysVersion = Build.VERSION.SDK_INT
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (mHeadView != null && !mHeadView!!.isClickable) {
            mHeadView!!.isClickable = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mHeadView = getChildAt(0)
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, View.MeasureSpec.UNSPECIFIED, 0)
        mHeadHeight = mHeadView!!.measuredHeight
        maxY = mHeadHeight - topOffset
        //让测量高度加上头部的高度
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec) + maxY, View.MeasureSpec.EXACTLY))
    }

    /** @param disallowIntercept 作用同 requestDisallowInterceptTouchEvent
     */
    fun requestHeaderViewPagerDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
        mDisallowIntercept = disallowIntercept
    }

    fun setSwipeRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        this.refreshLayout = refreshLayout
    }

    /**
     * 说明：一旦dispatTouchEvent返回true，即表示当前View就是事件传递需要的 targetView，事件不会再传递给
     * 其他View，如果需要将事件继续传递给子View，可以手动传递
     * 由于dispatchTouchEvent处理事件的优先级高于子View，也高于onTouchEvent,所以在这里进行处理
     * 好处一：当有子View，并且子View可以获取焦点的时候，子View的onTouchEvent会优先处理，如果当前逻辑
     * 在onTouchEnent中，则事件无法到达，逻辑失效
     * 好处二：当子View是拥有滑动事件时，例如ListView，ScrollView等，不需要对子View的事件进行拦截，可以
     * 全部让该父控件处理，在需要的地方手动将事件传递给子View，保证滑动的流畅性，结尾两行代码就是证明：
     * super.dispatchTouchEvent(ev);
     * return true;
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mScrollable.scrollableView == null) {
            return false
        }
        val currentX = ev.x                   //当前手指相对于当前view的X坐标
        val currentY = ev.y                   //当前手指相对于当前view的Y坐标
        val shiftX = Math.abs(currentX - mDownX)   //当前触摸位置与第一次按下位置的X偏移量
        val shiftY = Math.abs(currentY - mDownY)   //当前触摸位置与第一次按下位置的Y偏移量
        val deltaY: Float                                 //滑动的偏移量，即连续两次进入Move的偏移量
        obtainVelocityTracker(ev)                    //初始化速度追踪器
        when (ev.action) {
        //Down事件主要初始化变量
            MotionEvent.ACTION_DOWN -> {
                mDisallowIntercept = false
                verticalScrollFlag = false
                mDownX = currentX
                mDownY = currentY
                mLastY = currentY
                checkIsClickHead(currentY.toInt(), mHeadHeight, scrollY)
                mScroller!!.abortAnimation()
            }
            MotionEvent.ACTION_MOVE -> {
                if (refreshLayout != null) {
                    if (canPtr()) {
                        refreshLayout!!.isEnabled = true
                    } else {
                        refreshLayout!!.isEnabled = false
                    }
                }
                if (!mDisallowIntercept) {
                    deltaY = mLastY - currentY //连续两次进入move的偏移量
                    mLastY = currentY
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        //水平滑动
                        verticalScrollFlag = false
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        //垂直滑动
                        verticalScrollFlag = true
                    }
                    /**
                     * 这里要注意，对于垂直滑动来说，给出以下三个条件
                     * 头部没有固定，允许滑动的View处于第一条可见，当前按下的点在头部区域
                     * 三个条件满足一个即表示需要滚动当前布局，否者不处理，将事件交给子View去处理
                     */
                    if (verticalScrollFlag && (!isStickied || mScrollable.isTop || isClickHead)) {
                        //如果是向下滑，则deltaY小于0，对于scrollBy来说
                        //正值为向上和向左滑，负值为向下和向右滑，这里要注意
                        scrollBy(0, (deltaY + 0.5).toInt())
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (verticalScrollFlag) {
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat()) //1000表示单位，每1000毫秒允许滑过的最大距离是mMaximumVelocity
                    val yVelocity = mVelocityTracker!!.yVelocity  //获取当前的滑动速度
                    mDirection = if (yVelocity > 0) DIRECTION_DOWN else DIRECTION_UP  //下滑速度大于0，上滑速度小于0
                    //根据当前的速度和初始化参数，将滑动的惯性初始化到当前View，至于是否滑动当前View，取决于computeScroll中计算的值
                    //这里不判断最小速度，确保computeScroll一定至少执行一次
                    mScroller!!.fling(0, scrollY, 0, -yVelocity.toInt(), 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE)
                    mLastScrollerY = scrollY
                    invalidate()  //更新界面，该行代码会导致computeScroll中的代码执行
                    //阻止快读滑动的时候点击事件的发生，滑动的时候，将Up事件改为Cancel就不会发生点击了
                    if (shiftX > mTouchSlop || shiftY > mTouchSlop) {
                        if (isClickHead || !isStickied) {
                            val action = ev.action
                            ev.action = MotionEvent.ACTION_CANCEL
                            val dd = super.dispatchTouchEvent(ev)
                            ev.action = action
                            return dd
                        }
                    }
                }
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL -> recycleVelocityTracker()
            else -> {
            }
        }
        //手动将事件传递给子View，让子View自己去处理事件
        super.dispatchTouchEvent(ev)
        //消费事件，返回True表示当前View需要消费事件，就是事件的TargetView
        return true
    }

    private fun checkIsClickHead(downY: Int, headHeight: Int, scrollY: Int) {
        isClickHead = downY + scrollY <= headHeight
    }

    private fun obtainVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val currY = mScroller.currY
            if (mDirection == DIRECTION_UP) {
                // 手势向上划
                if (isStickied) {
                    //这里主要是将快速滚动时的速度对接起来，让布局看起来滚动连贯
                    val distance = mScroller.finalY - currY    //除去布局滚动消耗的时间后，剩余的时间
                    val duration = calcDuration(mScroller.duration, mScroller.timePassed()) //除去布局滚动的距离后，剩余的距离
                    mScrollable.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration)
                    //外层布局已经滚动到指定位置，不需要继续滚动了
                    mScroller.abortAnimation()
                    return
                } else {
                    scrollTo(0, currY)  //将外层布局滚动到指定位置
                    invalidate()        //移动完后刷新界面
                }
            } else {
                // 手势向下划，内部View已经滚动到顶了，需要滚动外层的View
                if (mScrollable.isTop || isClickHead) {
                    val deltaY = currY - mLastScrollerY
                    val toY = scrollY + deltaY
                    scrollTo(0, toY)
                    if (mCurY <= minY) {
                        mScroller.abortAnimation()
                        return
                    }
                }
                //向下滑动时，初始状态可能不在顶部，所以要一直重绘，让computeScroll一直调用
                //确保代码能进入上面的if判断
                invalidate()
            }
            mLastScrollerY = currY
        }
    }

    @SuppressLint("NewApi")
    private fun getScrollerVelocity(distance: Int, duration: Int): Int {
        return if (mScroller == null) {
            0
        } else if (sysVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mScroller.currVelocity.toInt()
        } else {
            distance / duration
        }
    }

    /** 对滑动范围做限制  */
    override fun scrollBy(x: Int, y: Int) {
        var y = y
        val scrollY = scrollY
        var toY = scrollY + y
        if (toY >= maxY) {
            toY = maxY
        } else if (toY <= minY) {
            toY = minY
        }
        y = toY - scrollY
        super.scrollBy(x, y)
    }

    /** 对滑动范围做限制  */
    override fun scrollTo(x: Int, y: Int) {
        var y = y
        if (y >= maxY) {
            y = maxY
        } else if (y <= minY) {
            y = minY
        }
        mCurY = y
        if (onScrollListener != null) {
            onScrollListener!!.onScroll(y, maxY)
        }
        super.scrollTo(x, y)
    }

    private fun calcDuration(duration: Int, timepass: Int): Int {
        return duration - timepass
    }

    /** 是否允许下拉，与PTR结合使用  */
    fun canPtr(): Boolean {
        return verticalScrollFlag && mCurY == minY && mScrollable.isTop
    }

    fun setTopOffset(topOffset: Int) {
        this.topOffset = topOffset
    }

    fun setCurrentScrollableContainer(scrollableContainer: HeaderScrollHelper.ScrollableContainer) {
        mScrollable.setCurrentScrollableContainer(scrollableContainer)
    }

    companion object {

        private val DIRECTION_UP = 1
        private val DIRECTION_DOWN = 2
    }
}
