package cn.yue.base.common.widget.wheel

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.graphics.drawable.LayerDrawable
import android.os.Handler
import android.os.Message
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.annotation.ColorInt
import java.util.*


// TODO: Auto-generated Javadoc

/**
 * Numeric wheel view.
 */
class WheelView : View {

    /** The Constant D.  */

    /** The m context.  */
    private var mContext: Context? = null

    private var itemTextColor: Int = 0

    // Wheel Values
    /** The adapter.  */
    /**
     * Gets wheel adapter.
     *
     * @return the adapter
     */
    /**
     * Sets wheel adapter.
     *
     * @param adapter
     * the new wheel adapter
     */
    // 重绘
    var adapter: AbWheelAdapter? = null
        set(adapter) {
            field = adapter
            invalidateLayouts()
            invalidate()
        }

    /** The current item.  */
    private var currentItem = 0

    // Widths
    /** The items width.  */
    private var itemsWidth = 0

    // Count of visible items
    /** The visible items.  */
    /**
     * Gets count of visible items.
     *
     * @return the count of visible items
     */
    /**
     * Sets count of visible items.
     *
     * @param count
     * the new count
     */
    var visibleItems = DEF_VISIBLE_ITEMS
        set(count) {
            field = count
            invalidate()
        }

    // Item height
    /** The item height.  */
    private var itemHeight = 0

    // Text paints
    /** The items paint.  */
    private var itemsPaint: TextPaint? = null

    /** The value paint.  */
    private var valuePaint: TextPaint? = null

    // Layouts
    /** The items layout.  */
    private var itemsLayout: StaticLayout? = null


    /** The value layout.  */
    private var valueLayout: StaticLayout? = null


    // Scrolling
    /** The is scrolling performed.  */
    private var isScrollingPerformed: Boolean = false

    /** The scrolling offset.  */
    private var scrollingOffset: Int = 0

    // Scrolling animation
    /** The gesture detector.  */
    private var gestureDetector: GestureDetector? = null

    /** The scroller.  */
    private var scroller: Scroller? = null

    /** The last scroll y.  */
    private var lastScrollY: Int = 0

    // Cyclic
    /** The is cyclic.  */
    internal var isCyclic = false

    // Listeners
    /** The changing listeners.  */
    private val changingListeners = LinkedList<AbOnWheelChangedListener>()

    /** The scrolling listeners.  */
    private val scrollingListeners = LinkedList<AbOnWheelScrollListener>()


    /** 中间覆盖条的颜色，如果没有设置centerDrawable时才生效.  */
    /**
     * Gets the center select gradient colors.
     *
     * @return the center select gradient colors
     */
    /**
     * Sets the center select gradient colors.
     *
     * @param centerSelectGradientColors
     * the new center select gradient colors
     */
    var centerSelectGradientColors = intArrayOf(0x10a1e0, 0x10a1e0, 0x10a1e0)

    /** The center select stroke width.  */
    /**
     * Gets the center select stroke width.
     *
     * @return the center select stroke width
     */
    /**
     * Sets the center select stroke width.
     *
     * @param centerSelectStrokeWidth
     * the new center select stroke width
     */
    var centerSelectStrokeWidth = 1

    /** The center select stroke color.  */
    /**
     * Gets the center select stroke color.
     *
     * @return the center select stroke color
     */
    /**
     * Sets the center select stroke color.
     *
     * @param centerSelectStrokeColor
     * the new center select stroke color
     */
    var centerSelectStrokeColor = -0x101011

    /** Shadows drawables.  */
    private var topShadow: GradientDrawable? = null

    /** The bottom shadow.  */
    private var bottomShadow: GradientDrawable? = null

    /** Current value.  */
    private var valueTextColor = -0x10000000


    // 轮子的背景 底部的颜色
    /** The bottom gradient colors.  */
    private val bottomGradientColors = intArrayOf(0x00000000, 0x00000000, 0x00000000)
    // private int[] bottomGradientColors = null;
    // 轮子的背景 顶部的颜色
    /** The top gradient colors.  */
    private val topGradientColors = intArrayOf(0x00000000, 0x00000000, 0x00000000)
    // private int[] topGradientColors = null;

    /** The top stroke width.  */
    private val topStrokeWidth = 1

    /** The top stroke color.  */
    private val topStrokeColor = 0x00000000

    /** 值的文字大小.  */
    private var valueTextSize = 20


    /** Top and bottom items offset.  */
    private var itemOffset = valueTextSize / 5

    /** 行间距.  */
    private var additionalItemHeight = 80

    /** 屏幕宽度.  */
    private var screenWidth = 0

    /** 屏幕高度.  */
    private var screenHeight = 0

    var data: List<*>?
        get() = if (this.adapter != null) {
            this.adapter!!.data
        } else null
        set(list) {
            if (this.adapter != null) {
                this.adapter!!.data = list!!
                invalidateLayouts()
                invalidate()
            }
        }

    var itemTextSize = 35
        set(itemTextSize) {
            field = AbViewUtils.resizeTextSize(screenWidth, screenHeight, itemTextSize)
        }

    val itemsTextColor: Int
        get() = if (itemTextColor != 0) {
            itemTextColor
        } else ITEMS_TEXT_COLOR

    /**
     * Returns the max item length that can be present.
     *
     * @return the max length
     */
    private val maxTextLength: Int
        get() {
            val adapter = adapter ?: return 0

            val adapterLength = adapter.maximumLength
            return if (adapterLength > 0) {
                adapterLength
            } else {
                0
            }
        }

    var isDrawShadows = false// 是否绘制上下阴影

    var isDrawCenterRect = true// 是否绘制中间举行

    private var touchable = true

    // gesture listener
    /** The gesture listener.  */
    private val gestureListener = object : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            if (isScrollingPerformed) {
                scroller!!.forceFinished(true)
                clearMessages()
                return true
            }
            return false
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            startScrolling()
            doScroll((-distanceY).toInt())
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            lastScrollY = currentItem * getItemHeight() + scrollingOffset
            val maxY = if (isCyclic) 0x7FFFFFFF else adapter!!.itemsCount * getItemHeight()
            val minY = if (isCyclic) -maxY else 0
            scroller!!.fling(0, lastScrollY, 0, (-velocityY).toInt() / 2, 0, 0, minY, maxY)
            setNextMessage(MESSAGE_SCROLL)
            return true
        }
    }

    // Messages
    /** The message scroll.  */
    private val MESSAGE_SCROLL = 0

    /** The message justify.  */
    private val MESSAGE_JUSTIFY = 1

    // animation handler
    /** The animation handler.  */
    private val animationHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            scroller!!.computeScrollOffset()
            var currY = scroller!!.currY
            val delta = lastScrollY - currY
            lastScrollY = currY
            if (delta != 0) {
                doScroll(delta)
            }

            // scrolling is not finished when it comes to final Y
            // so, finish it manually
            if (Math.abs(currY - scroller!!.finalY) < MIN_DELTA_FOR_SCROLLING) {
                currY = scroller!!.finalY
                scroller!!.forceFinished(true)
            }
            if (!scroller!!.isFinished) {
                this.sendEmptyMessage(msg.what)
            } else if (msg.what == MESSAGE_SCROLL) {
                justify()
            } else {
                finishScrolling()
            }
        }
    }

    /**
     * Constructor.
     *
     * @param context
     * the context
     * @param attrs
     * the attrs
     * @param defStyle
     * the def style
     */
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initData(context)
    }

    /**
     * Constructor.
     *
     * @param context
     * the context
     * @param attrs
     * the attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initData(context)
    }

    /**
     * Constructor.
     *
     * @param context
     * the context
     */
    constructor(context: Context) : super(context) {
        initData(context)
    }

    /**
     * Initializes class data.
     *
     * @param context
     * the context
     */
    private fun initData(context: Context) {
        mContext = context
        gestureDetector = GestureDetector(context, gestureListener)
        gestureDetector!!.setIsLongpressEnabled(false)
        scroller = Scroller(context)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
    }

    /**
     * Set the the specified scrolling interpolator.
     *
     * @param interpolator
     * the interpolator
     */
    fun setInterpolator(interpolator: Interpolator) {
        scroller!!.forceFinished(true)
        scroller = Scroller(context, interpolator)
    }


    /**
     * Adds wheel changing listener.
     *
     * @param listener
     * the listener
     */
    fun addChangingListener(listener: AbOnWheelChangedListener) {
        changingListeners.add(listener)
    }

    /**
     * Removes wheel changing listener.
     *
     * @param listener
     * the listener
     */
    fun removeChangingListener(listener: AbOnWheelChangedListener) {
        changingListeners.remove(listener)
    }

    /**
     * Notifies changing listeners.
     *
     * @param oldValue
     * the old wheel value
     * @param newValue
     * the new wheel value
     */
    protected fun notifyChangingListeners(oldValue: Int, newValue: Int) {
        for (listener in changingListeners) {
            listener.onChanged(this, oldValue, newValue)
        }
    }

    /**
     * Adds wheel scrolling listener.
     *
     * @param listener
     * the listener
     */
    fun addScrollingListener(listener: AbOnWheelScrollListener) {
        scrollingListeners.add(listener)
    }

    /**
     * Removes wheel scrolling listener.
     *
     * @param listener
     * the listener
     */
    fun removeScrollingListener(listener: AbOnWheelScrollListener) {
        scrollingListeners.remove(listener)
    }

    /**
     * Notifies listeners about starting scrolling.
     */
    protected fun notifyScrollingListenersAboutStart() {
        for (listener in scrollingListeners) {
            listener.onScrollingStarted(this)
        }
    }

    /**
     * Notifies listeners about ending scrolling.
     */
    protected fun notifyScrollingListenersAboutEnd() {
        for (listener in scrollingListeners) {
            listener.onScrollingFinished(this)
        }
    }

    /**
     * Gets current value.
     *
     * @return the current value
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index
     * the item index
     * @param animated
     * the animation flag
     */
    @JvmOverloads
    fun setCurrentItem(index: Int, animated: Boolean = false) {
        var index = index
        if (this.adapter == null || this.adapter!!.itemsCount == 0) {
            return  // throw?
        }
        if (index < 0 || index >= this.adapter!!.itemsCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += this.adapter!!.itemsCount
                }
                index %= this.adapter!!.itemsCount
            } else {
                return  // throw?
            }
        }
        if (index != currentItem) {
            if (animated) {
                scroll(index - currentItem, SCROLLING_DURATION)
            } else {
                invalidateLayouts()

                val old = currentItem
                currentItem = index

                notifyChangingListeners(old, currentItem)

                invalidate()
            }
        }
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown
     * the last one
     *
     * @return true if wheel is cyclic
     */
    fun isCyclic(): Boolean {
        return isCyclic
    }

    /**
     * Set wheel cyclic flag.
     *
     * @param isCyclic
     * the flag to set
     */
    fun setCyclic(isCyclic: Boolean) {
        this.isCyclic = isCyclic

        invalidate()
        invalidateLayouts()
    }

    /**
     * Invalidates layouts.
     */
    private fun invalidateLayouts() {
        itemsLayout = null
        valueLayout = null
        scrollingOffset = 0
    }

    fun setItemTextColor(@ColorInt color: Int) {
        this.itemTextColor = color
    }

    /**
     * Initializes resources.
     */
    private fun initResourcesIfNecessary() {
        if (itemsPaint == null) {
            itemsPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            itemsPaint!!.textSize = valueTextSize.toFloat()
        }

        if (valuePaint == null) {
            valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
            valuePaint!!.textSize = valueTextSize.toFloat()
        }

        /*
		 * Android中提供了Shader类专门用来渲染图像以及一些几何图形， Shader下面包括几个直接子类，分别是BitmapShader、
		 * ComposeShader、LinearGradient、 RadialGradient、SweepGradient。
		 * BitmapShader主要用来渲染图像， LinearGradient 用来进行梯度渲染，RadialGradient
		 * 用来进行环形渲染， SweepGradient 用来进行梯度渲染，ComposeShader则是一个
		 * 混合渲染，可以和其它几个子类组合起来使用。
		 */

        // 上边界渐变层
        if (topShadow == null) {
            topShadow = GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS)
        }
        // 下边界渐变层
        if (bottomShadow == null) {
            bottomShadow = GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS)
        }

        if (this.background == null) {
            // 原来用颜色渐变实现setBackgroundDrawable(layerDrawable);
            // 底部的颜色
            val mGradientDrawable1 = GradientDrawable(Orientation.TOP_BOTTOM, topGradientColors)
            val mGradientDrawable2 = GradientDrawable(Orientation.BOTTOM_TOP, bottomGradientColors)

            mGradientDrawable1.setStroke(topStrokeWidth, topStrokeColor)
            mGradientDrawable1.shape = GradientDrawable.RECTANGLE
            mGradientDrawable2.shape = GradientDrawable.RECTANGLE
            mGradientDrawable1.gradientType = GradientDrawable.LINEAR_GRADIENT
            mGradientDrawable2.gradientType = GradientDrawable.LINEAR_GRADIENT

            val mDrawables = arrayOfNulls<GradientDrawable>(2)
            mDrawables[0] = mGradientDrawable1
            mDrawables[1] = mGradientDrawable2

            val layerDrawable = LayerDrawable(mDrawables)
            layerDrawable.setLayerInset(0, 0, 0, 0, 0) // 第一个参数0代表数组的第1个元素
            layerDrawable.setLayerInset(1, 4, 1, 4, 1) // 第一个参数1代表数组的第2个元素
            setBackgroundDrawable(layerDrawable)
        }

    }

    /**
     * Calculates desired height for layout.
     *
     * @param layout
     * the source layout
     * @return the desired layout height
     */
    private fun getDesiredHeight(layout: Layout?): Int {
        if (layout == null) {
            return 0
        }

        var desired = getItemHeight() * this.visibleItems

        // Check against our minimum height
        desired = Math.max(desired, suggestedMinimumHeight)

        return desired
    }

    /**
     * Returns text item by index.
     *
     * @param index
     * the item index
     * @return the item or null
     */
    private fun getTextItem(index: Int): String? {
        var index = index
        if (this.adapter == null || this.adapter!!.itemsCount == 0) {
            return null
        }
        val count = this.adapter!!.itemsCount
        if ((index < 0 || index >= count) && !isCyclic) {
            return null
        } else {
            while (index < 0) {
                index = count + index
            }
        }

        index %= count
        return calculateShowStr(this.adapter!!.getItem(index))
    }

    private fun calculateShowStr(s: String): String {
        if (AbGraphical.getStringWidth(s, valuePaint!!) < width) {
            return s
        }
        val enWidth = (AbGraphical.getStringWidth("a", valuePaint!!) as Int).toFloat()
        val cnWidth = (AbGraphical.getStringWidth("哈", valuePaint!!) as Int).toFloat()
        var countWith = 0f
        var num = s.length
        for (i in 0 until s.length) {
            val temp = s.substring(i, i + 1)
            val chinese = "[\u0391-\uFFE5]"
            if (temp.matches(chinese.toRegex())) {
                countWith += cnWidth
            } else {
                countWith += enWidth
            }
            if (countWith >= width) {
                num = i
                break
            }
        }
        return s.substring(0, num)
    }

    /**
     * Builds text depending on current value.
     *
     * @param useCurrentValue
     * the use current value
     * @return the text
     */
    private fun buildText(useCurrentValue: Boolean): String {
        val itemsText = StringBuilder()
        val addItems = this.visibleItems / 2 + 1

        for (i in currentItem - addItems..currentItem + addItems) {
            if (useCurrentValue || i != currentItem) {
                val text = getTextItem(i)
                if (text != null) {
                    itemsText.append(text)
                }
            }
            if (i < currentItem + addItems) {
                itemsText.append("\n")
            }
        }

        return itemsText.toString()
    }

    /**
     * Returns height of wheel item.
     *
     * @return the item height
     */
    private fun getItemHeight(): Int {
        if (itemHeight != 0) {
            return itemHeight
        } else if (itemsLayout != null && itemsLayout!!.lineCount > 2) {
            itemHeight = itemsLayout!!.getLineTop(2) - itemsLayout!!.getLineTop(1)
            return itemHeight
        }

        return height / this.visibleItems
    }

    /**
     * Calculates control width and creates text layouts.
     *
     * @param widthSize
     * the input layout width
     * @param mode
     * the layout mode
     * @return the calculated control width
     */
    private fun calculateLayoutWidth(widthSize: Int, mode: Int): Int {
        initResourcesIfNecessary()

        var width = widthSize

        val maxLength = maxTextLength
        if (maxLength > 0) {
            // 一个字符宽度
            val textWidth = (AbGraphical.getStringWidth("0", valuePaint!!) as Int).toFloat()
            itemsWidth = (maxLength * textWidth).toInt()
        } else {
            itemsWidth = 0
        }

        var recalculate = false
        if (mode == View.MeasureSpec.EXACTLY) {
            width = widthSize
            recalculate = true
        } else {
            width = itemsWidth + 2 * PADDING

            // Check against our minimum width
            width = Math.max(width, suggestedMinimumWidth)

            if (mode == View.MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize
                recalculate = true
            }
        }

        if (recalculate) {
            // recalculate width
            val pureWidth = width - 2 * PADDING
            if (pureWidth <= 0) {
                itemsWidth = 0
            }
            itemsWidth = pureWidth
        }

        if (itemsWidth > 0) {
            createLayouts(itemsWidth)
        }

        return width
    }

    /**
     * Creates layouts.
     *
     * @param widthItems
     * width of items layout
     * width of label layout
     */
    private fun createLayouts(widthItems: Int) {

        //选中Item
        if (!isScrollingPerformed && (valueLayout == null || valueLayout!!.width > widthItems)) {
            val text = if (adapter != null) calculateShowStr(adapter!!.getItem(currentItem)) else null
            valueLayout = StaticLayout(text ?: "",
                    valuePaint,
                    widthItems,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    additionalItemHeight.toFloat(),
                    false)
        } else if (isScrollingPerformed) {
            valueLayout = null
        } else {
            valueLayout!!.increaseWidthTo(widthItems)
        }

        //Item
        if (itemsLayout == null || itemsLayout!!.width > widthItems) {
            val text = if (adapter != null) adapter!!.getItem(currentItem) else null
            itemsLayout = StaticLayout(buildText(isScrollingPerformed),
                    itemsPaint,
                    widthItems,
                    Layout.Alignment.ALIGN_CENTER,
                    1.0f,
                    additionalItemHeight.toFloat(),
                    false)
        } else {
            itemsLayout!!.increaseWidthTo(widthItems)
        }
    }

    /**
     * 描述：TODO.
     *
     * @param widthMeasureSpec
     * the width measure spec
     * @param heightMeasureSpec
     * the height measure spec
     * @see View.onMeasure
     * @author: zhaoqp
     * @date：2013-6-17 上午9:04:47
     * @version v1.0
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = calculateLayoutWidth(widthSize, widthMode)

        var height: Int
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = getDesiredHeight(itemsLayout)

            if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize)
            }
        }
        setMeasuredDimension(width, height)
    }

    /**
     * 描述：TODO.
     *
     * @param canvas
     * the canvas
     * @see View.onDraw
     * @author: zhaoqp
     * @date：2013-6-17 上午9:04:47
     * @version v1.0
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (itemsLayout == null) {
            if (itemsWidth == 0) {
                calculateLayoutWidth(width, View.MeasureSpec.EXACTLY)
            } else {
                createLayouts(itemsWidth)
            }
        }

        if (itemsWidth > 0) {
            canvas.save()
            // Skip padding space and hide a part of top and bottom items
            canvas.translate(PADDING.toFloat(), (-itemOffset).toFloat())
            drawItems(canvas)
            drawValue(canvas)
            canvas.restore()
        }
        if (isDrawCenterRect) {
            drawCenterRect(canvas)
        }
        if (isDrawShadows) {
            drawShadows(canvas)
        }

    }

    /**
     * Draws shadows on top and bottom of control.
     *
     * @param canvas
     * the canvas for drawing
     */
    private fun drawShadows(canvas: Canvas) {
        if (topShadow != null) {
            topShadow!!.setBounds(0, 0, width, height / this.visibleItems)
            topShadow!!.draw(canvas)
        }

        if (bottomShadow != null) {
            bottomShadow!!.setBounds(0, height - height / this.visibleItems, width, height)
            bottomShadow!!.draw(canvas)
        }

    }

    /**
     * Draws value and label layout.
     *
     * @param canvas
     * the canvas for drawing
     */
    private fun drawValue(canvas: Canvas) {
        valuePaint!!.color = valueTextColor
        valuePaint!!.drawableState = drawableState
        val bounds = Rect()
        itemsLayout!!.getLineBounds(this.visibleItems / 2, bounds)
        // draw current value
        if (valueLayout != null) {
            canvas.save()
            canvas.translate(0f, (bounds.top + scrollingOffset + 40).toFloat())
            valueLayout!!.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawItems(canvas: Canvas) {
        canvas.save()
        val top = itemsLayout!!.getLineTop(1) - 40
        canvas.translate(0f, (-top + scrollingOffset).toFloat())
        itemsPaint!!.color = itemsTextColor
        itemsPaint!!.drawableState = drawableState
        itemsLayout!!.draw(canvas)
        canvas.restore()
    }

    /**
     * Draws rect for current value.
     *
     * @param canvas
     * the canvas for drawing
     */
    private fun drawCenterRect(canvas: Canvas) {
        val itemHeight = getItemHeight()
        val itemWidth = width
        //设置divider
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = centerSelectStrokeColor
        for (i in 1 until DEF_VISIBLE_ITEMS) {
            val rect = Rect()
            rect.bottom = itemHeight * i
            canvas.drawLine(0f, (itemHeight * i).toFloat(), itemWidth.toFloat(), (itemHeight * i).toFloat(), paint)
        }
    }

    fun setTouchable(touchable: Boolean) {
        this.touchable = touchable
    }

    /**
     * 描述：TODO.
     *
     * @param event
     * the event
     * @return true, if successful
     * @see View.onTouchEvent
     * @author: zhaoqp
     * @date：2013-6-17 上午9:04:47
     * @version v1.0
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val adapter = adapter ?: return true

        if (touchable && !gestureDetector!!.onTouchEvent(event) && event.action == MotionEvent.ACTION_UP) {
            justify()
        }
        return true
    }

    /**
     * Scrolls the wheel.
     *
     * @param delta
     * the scrolling value
     */
    private fun doScroll(delta: Int) {
        scrollingOffset += delta

        var count = scrollingOffset / getItemHeight()
        var pos = currentItem - count
        if (isCyclic && this.adapter!!.itemsCount > 0) {
            // fix position by rotating
            while (pos < 0) {
                pos += this.adapter!!.itemsCount
            }
            pos %= this.adapter!!.itemsCount
        } else if (isScrollingPerformed) {
            //
            if (pos < 0) {
                count = currentItem
                pos = 0
            } else if (pos >= this.adapter!!.itemsCount) {
                count = currentItem - this.adapter!!.itemsCount + 1
                pos = this.adapter!!.itemsCount - 1
            }
        } else {
            // fix position
            pos = Math.max(pos, 0)
            pos = Math.min(pos, this.adapter!!.itemsCount - 1)
        }

        val offset = scrollingOffset
        if (pos != currentItem) {
            setCurrentItem(pos, false)
        } else {
            invalidate()
        }

        // update offset
        scrollingOffset = offset - count * getItemHeight()
        if (scrollingOffset > height) {
            scrollingOffset = scrollingOffset % height + height
        }
    }

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message
     * the message to set
     */
    private fun setNextMessage(message: Int) {
        clearMessages()
        animationHandler.sendEmptyMessage(message)
    }

    /**
     * Clears messages from queue.
     */
    private fun clearMessages() {
        animationHandler.removeMessages(MESSAGE_SCROLL)
        animationHandler.removeMessages(MESSAGE_JUSTIFY)
    }

    /**
     * Justifies wheel.
     */
    private fun justify() {
        if (this.adapter == null) {
            return
        }

        lastScrollY = 0
        var offset = scrollingOffset
        val itemHeight = getItemHeight()
        val needToIncrease = if (offset > 0) currentItem < this.adapter!!.itemsCount else currentItem > 0
        if ((isCyclic || needToIncrease) && Math.abs(offset.toFloat()) > itemHeight.toFloat() / 2) {
            if (offset < 0)
                offset += itemHeight + MIN_DELTA_FOR_SCROLLING
            else
                offset -= itemHeight + MIN_DELTA_FOR_SCROLLING
        }
        if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
            scroller!!.startScroll(0, 0, 0, offset, SCROLLING_DURATION)
            setNextMessage(MESSAGE_JUSTIFY)
        } else {
            finishScrolling()
        }
    }

    /**
     * Starts scrolling.
     */
    private fun startScrolling() {
        if (!isScrollingPerformed) {
            isScrollingPerformed = true
            notifyScrollingListenersAboutStart()
        }
    }

    /**
     * Finishes scrolling.
     */
    internal fun finishScrolling() {
        if (isScrollingPerformed) {
            notifyScrollingListenersAboutEnd()
            isScrollingPerformed = false
        }
        invalidateLayouts()
        invalidate()
    }

    /**
     * Scroll the wheel.
     *
     * @param itemsToScroll
     * the items to scroll
     * @param time
     * scrolling duration
     */
    fun scroll(itemsToScroll: Int, time: Int) {
        scroller!!.forceFinished(true)
        lastScrollY = scrollingOffset
        val offset = itemsToScroll * getItemHeight()
        scroller!!.startScroll(0, lastScrollY, 0, offset - lastScrollY, time)
        setNextMessage(MESSAGE_SCROLL)
        startScrolling()
    }

    /**
     * Sets the value text size.
     *
     * @param textSize
     * the new value text size
     */
    fun setValueTextSize(textSize: Int) {
        this.valueTextSize = AbViewUtils.resizeTextSize(screenWidth, screenHeight, textSize)
        this.itemOffset = valueTextSize / 5
    }


    /**
     * Sets the value text color.
     *
     * @param valueTextColor
     * the new value text color
     */
    fun setValueTextColor(valueTextColor: Int) {
        this.valueTextColor = valueTextColor
    }


    /**
     * Sets the additional item height.
     *
     * @param additionalItemHeight
     * the new additional item height
     */
    fun setAdditionalItemHeight(additionalItemHeight: Int) {
        this.additionalItemHeight = additionalItemHeight
    }

    companion object {

        /** The tag.  */
        private val TAG = "WheelView"

        /** Scrolling duration.  */
        private val SCROLLING_DURATION = 400

        /** Minimum delta for scrolling.  */
        private val MIN_DELTA_FOR_SCROLLING = 1

        /** Items text color.  */
        private val ITEMS_TEXT_COLOR = -0x666667

        /** Top and bottom shadows colors.  */
        // private static int[] SHADOWS_COLORS = new int[] { 0xFF111111, 0x00AAAAAA,
        // 0x00AAAAAA };
        private val SHADOWS_COLORS = intArrayOf(0x00000000, 0x00000000, 0x00000000)

        /** Left and right padding value.  */
        private val PADDING = 5

        /** Default count of visible items.  */
        private val DEF_VISIBLE_ITEMS = 5
    }

}
