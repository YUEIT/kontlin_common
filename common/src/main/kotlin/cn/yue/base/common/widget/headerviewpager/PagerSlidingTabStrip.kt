package cn.yue.base.common.widget.headerviewpager

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import java.util.Locale

import cn.yue.base.common.R


/**
 * 介绍：viewPager 带文字的指示器
 * 邮箱：luobiao@imcoming.cn
 * 时间：2016/10/26.
 */

class PagerSlidingTabStrip @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : HorizontalScrollView(context, attrs, defStyle) {
    // @formatter:on

    private val defaultTabLayoutParams: LinearLayout.LayoutParams
    private val expandedTabLayoutParams: LinearLayout.LayoutParams

    private val pageListener = PageListener()
    var delegatePageListener: ViewPager.OnPageChangeListener? = null

    private var mAdapterChangeListener: AdapterChangeListener? = null

    private val tabsContainer: LinearLayout
    private var pager: ViewPager? = null

    private var mPagerAdapter: PagerAdapter? = null
    private var mPagerAdapterObserver: DataSetObserver? = null

    private var tabCount: Int = 0

    private var currentPosition = 0
    private var currentPositionOffset = 0f

    private val rectPaint: Paint
    private val dividerPaint: Paint

    private var indicatorColor = -0x99999a
    private var underlineColor = 0x1A000000
    private var dividerColor = 0x1A000000

    private var shouldExpand = false
    var isTextAllCaps = true
        private set

    private var scrollOffset = 52
    private var indicatorHeight = 8
    private var indicatorWidth = 0
    private var underlineHeight = 2
    private var dividerPadding = 12
    private var tabPadding = 10
    private var dividerWidth = 1

    private var tabTextSize = 12
    private var tabTextColor = -0x99999a
    private var tabTextSelectColor = -0x1000000
    private var tabTypeface: Typeface? = null
    private var tabTypefaceStyle = Typeface.NORMAL

    private var lastScrollX = 0

    var tabBackground = 0

    private var locale: Locale? = null

    var textSize: Int
        get() = tabTextSize
        set(textSizePx) {
            this.tabTextSize = textSizePx
            updateTabStyles()
        }

    var textColor: Int
        get() = tabTextColor
        set(textColor) {
            this.tabTextColor = textColor
            updateTabStyles()
        }

    var tabPaddingLeftRight: Int
        get() = tabPadding
        set(paddingPx) {
            this.tabPadding = paddingPx
            updateTabStyles()
        }

    interface IconTabProvider {
        fun getPageIconResId(position: Int): Int
    }

    interface LayoutTabProvider {

        fun createTabView(position: Int): View

        fun changeTabStyle(v: View, isSelect: Boolean)

    }


    init {

        isFillViewport = true
        setWillNotDraw(false)

        tabsContainer = LinearLayout(context)
        tabsContainer.orientation = LinearLayout.HORIZONTAL
        tabsContainer.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        addView(tabsContainer)

        val dm = resources.displayMetrics

        scrollOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset.toFloat(), dm).toInt()
        indicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight.toFloat(), dm).toInt()
        underlineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight.toFloat(), dm).toInt()
        dividerPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding.toFloat(), dm).toInt()
        tabPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding.toFloat(), dm).toInt()
        dividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth.toFloat(), dm).toInt()
        tabTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize.toFloat(), dm).toInt()

        // get system attrs (android:textSize and android:textColor)

        var a = context.obtainStyledAttributes(attrs, ATTRS)

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize)
        tabTextColor = a.getColor(1, tabTextColor)

        a.recycle()

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip)

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor)
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor)
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor)
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight)
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight)
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding)
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding)
        tabBackground = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackground)
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand)
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset)
        isTextAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, isTextAllCaps)
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextDefaultColor, tabTextColor)
        tabTextSelectColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextSelectColor, tabTextSelectColor)
        indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorWidth, indicatorWidth)

        a.recycle()

        rectPaint = Paint()
        rectPaint.isAntiAlias = true
        rectPaint.style = Paint.Style.FILL

        dividerPaint = Paint()
        dividerPaint.isAntiAlias = true
        dividerPaint.strokeWidth = dividerWidth.toFloat()

        defaultTabLayoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
        expandedTabLayoutParams = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, 1.0f)

        if (locale == null) {
            locale = resources.configuration.locale
        }
    }

    fun setViewPager(pager: ViewPager?) {
        this.pager = pager

        if (pager!!.adapter == null) {
            throw IllegalStateException("ViewPager does not have adapter instance.")
        }

        pager.setOnPageChangeListener(pageListener)

        notifyDataSetChanged()
    }

    fun setPageListenerNull() {
        pager!!.setOnPageChangeListener(null)
    }

    fun setViewPagerAutoRefresh(viewPager: ViewPager?, autoRefresh: Boolean) {
        if (autoRefresh) {
            if (pager != null) {
                if (pageListener != null) {
                    pager!!.removeOnPageChangeListener(pageListener)
                }
                if (mAdapterChangeListener != null) {
                    pager!!.removeOnAdapterChangeListener(mAdapterChangeListener!!)
                }
            }
            if (viewPager != null) {
                pager = viewPager
                pager!!.addOnPageChangeListener(pageListener)
                val adapter = viewPager.adapter
                if (adapter != null) {
                    // Now we'll populate ourselves from the pager adapter, adding an observer if
                    // autoRefresh is enabled
                    setPagerAdapter(adapter, autoRefresh)
                }

                // Add a listener so that we're notified of any adapter changes
                if (mAdapterChangeListener == null) {
                    mAdapterChangeListener = AdapterChangeListener()
                }
                mAdapterChangeListener!!.setAutoRefresh(autoRefresh)
                viewPager.addOnAdapterChangeListener(mAdapterChangeListener!!)
            }
        } else {
            setViewPager(viewPager)
        }
    }

    internal fun setPagerAdapter(adapter: PagerAdapter?, addObserver: Boolean) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter!!.unregisterDataSetObserver(mPagerAdapterObserver!!)
        }

        mPagerAdapter = adapter

        if (addObserver && adapter != null) {
            // Register our observer on the new adapter
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = PagerAdapterObserver()
            }
            adapter.registerDataSetObserver(mPagerAdapterObserver!!)
        }

        // Finally make sure we reflect the new adapter
        populateFromPagerAdapter()
    }

    internal fun populateFromPagerAdapter() {
        tabsContainer.removeAllViews()

        if (mPagerAdapter != null) {
            val adapterCount = mPagerAdapter!!.count
            tabCount = adapterCount
            for (i in 0 until adapterCount) {
                if (pager!!.adapter is LayoutTabProvider) {
                    addTab(i, (pager!!.adapter as LayoutTabProvider).createTabView(i))
                } else if (pager!!.adapter is IconTabProvider) {
                    addIconTab(i, (pager!!.adapter as IconTabProvider).getPageIconResId(i))
                } else {
                    addTextTab(i, pager!!.adapter!!.getPageTitle(i)!!.toString())
                }

            }
            updateTabStyles()

            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

                @SuppressLint("NewApi")
                override fun onGlobalLayout() {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        viewTreeObserver.removeGlobalOnLayoutListener(this)
                    } else {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                    currentPosition = pager!!.currentItem
                    scrollToChild(currentPosition, 0)
                }
            })

        }
    }


    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        this.delegatePageListener = listener
    }

    fun notifyDataSetChanged() {

        tabsContainer.removeAllViews()

        tabCount = pager!!.adapter!!.count

        for (i in 0 until tabCount) {
            if (pager!!.adapter is LayoutTabProvider) {
                addTab(i, (pager!!.adapter as LayoutTabProvider).createTabView(i))
            } else if (pager!!.adapter is IconTabProvider) {
                addIconTab(i, (pager!!.adapter as IconTabProvider).getPageIconResId(i))
            } else {
                addTextTab(i, pager!!.adapter!!.getPageTitle(i)!!.toString())
            }

        }

        updateTabStyles()

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            @SuppressLint("NewApi")
            override fun onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }

                currentPosition = pager!!.currentItem
                scrollToChild(currentPosition, 0)
            }
        })

    }

    private fun addTextTab(position: Int, title: String) {

        val tab = TextView(context)
        tab.text = title
        tab.gravity = Gravity.CENTER
        tab.setSingleLine()

        addTab(position, tab)
    }

    private fun addIconTab(position: Int, resId: Int) {

        val tab = ImageButton(context)
        tab.setImageResource(resId)

        addTab(position, tab)

    }

    private fun addTab(position: Int, tab: View) {
        tab.isFocusable = true
        tab.setOnClickListener { pager!!.currentItem = position }

        tab.setPadding(tabPadding, 0, tabPadding, 0)
        tabsContainer.addView(tab, position, if (shouldExpand) expandedTabLayoutParams else defaultTabLayoutParams)
    }

    private fun updateTabStyles() {

        for (i in 0 until tabCount) {

            val v = tabsContainer.getChildAt(i)

            v.setBackgroundResource(tabBackground)

            if (v is TextView) {

                val tab = v
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.toFloat())
                tab.setTypeface(tabTypeface, tabTypefaceStyle)
                if (i == currentPosition) {
                    tab.setTextColor(tabTextSelectColor)
                } else {
                    tab.setTextColor(tabTextColor)
                }

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (isTextAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true)
                    } else {
                        tab.text = tab.text.toString().toUpperCase(locale!!)
                    }
                }
            } else if (pager!!.adapter is LayoutTabProvider) {
                (pager!!.adapter as LayoutTabProvider).changeTabStyle(v, i == currentPosition)
            }
        }

    }

    private fun scrollToChild(position: Int, offset: Int) {

        if (tabCount == 0 && position >= tabsContainer.childCount) {
            return
        }

        var newScrollX = tabsContainer.getChildAt(position).left + offset

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            scrollTo(newScrollX, 0)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isInEditMode || tabCount == 0) {
            return
        }

        val height = height

        // draw indicator line

        rectPaint.color = indicatorColor

        // default: line below current tab
        val currentTab = tabsContainer.getChildAt(currentPosition)
        var lineLeft = currentTab.left.toFloat()
        var lineRight = currentTab.right.toFloat()

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            val nextTab = tabsContainer.getChildAt(currentPosition + 1)
            val nextTabLeft = nextTab.left.toFloat()
            val nextTabRight = nextTab.right.toFloat()

            lineLeft = currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft
            lineRight = currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight
        }
        if (indicatorWidth != 0) {
            lineLeft += ((currentTab.measuredWidth - indicatorWidth) / 2).toFloat()
            lineRight -= ((currentTab.measuredWidth - indicatorWidth) / 2).toFloat()
        }
        canvas.drawRect(lineLeft, (height - indicatorHeight).toFloat(), lineRight, height.toFloat(), rectPaint)

        // draw underline

        rectPaint.color = underlineColor
        canvas.drawRect(0f, (height - underlineHeight).toFloat(), tabsContainer.width.toFloat(), height.toFloat(), rectPaint)

        // draw divider

        dividerPaint.color = dividerColor
        for (i in 0 until tabCount - 1) {
            val tab = tabsContainer.getChildAt(i)
            canvas.drawLine(tab.right.toFloat(), dividerPadding.toFloat(), tab.right.toFloat(), (height - dividerPadding).toFloat(), dividerPaint)
        }
    }

    private inner class PagerAdapterObserver internal constructor() : DataSetObserver() {

        override fun onChanged() {
            populateFromPagerAdapter()
        }

        override fun onInvalidated() {
            populateFromPagerAdapter()
        }
    }

    private inner class AdapterChangeListener internal constructor() : ViewPager.OnAdapterChangeListener {
        private var mAutoRefresh: Boolean = false

        override fun onAdapterChanged(viewPager: ViewPager,
                                      oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
            if (pager === viewPager) {
                setPagerAdapter(newAdapter, mAutoRefresh)
            }
        }

        internal fun setAutoRefresh(autoRefresh: Boolean) {
            mAutoRefresh = autoRefresh
        }
    }

    private inner class PageListener : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            currentPosition = position
            currentPositionOffset = positionOffset
            if (position < tabsContainer.childCount) {
                scrollToChild(position, (positionOffset * tabsContainer.getChildAt(position).width).toInt())
                updateTabStyles()
                invalidate()
            }

            if (delegatePageListener != null) {
                delegatePageListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager!!.currentItem, 0)
            }

            if (delegatePageListener != null) {
                delegatePageListener!!.onPageScrollStateChanged(state)
            }
        }

        override fun onPageSelected(position: Int) {
            if (delegatePageListener != null) {
                delegatePageListener!!.onPageSelected(position)
            }

        }

    }

    fun setIndicatorColor(indicatorColor: Int) {
        this.indicatorColor = indicatorColor
        invalidate()
    }

    fun setIndicatorColorResource(resId: Int) {
        this.indicatorColor = resources.getColor(resId)
        invalidate()
    }

    fun getIndicatorColor(): Int {
        return this.indicatorColor
    }

    fun setIndicatorHeight(indicatorLineHeightPx: Int) {
        this.indicatorHeight = indicatorLineHeightPx
        invalidate()
    }

    fun getIndicatorHeight(): Int {
        return indicatorHeight
    }

    fun setIndicatorWidth(indicatorWidth: Int) {
        this.indicatorWidth = indicatorWidth
        invalidate()
    }

    fun setUnderlineColor(underlineColor: Int) {
        this.underlineColor = underlineColor
        invalidate()
    }

    fun setUnderlineColorResource(resId: Int) {
        this.underlineColor = resources.getColor(resId)
        invalidate()
    }

    fun getUnderlineColor(): Int {
        return underlineColor
    }

    fun setDividerColor(dividerColor: Int) {
        this.dividerColor = dividerColor
        invalidate()
    }

    fun setDividerColorResource(resId: Int) {
        this.dividerColor = resources.getColor(resId)
        invalidate()
    }

    fun getDividerColor(): Int {
        return dividerColor
    }

    fun setUnderlineHeight(underlineHeightPx: Int) {
        this.underlineHeight = underlineHeightPx
        invalidate()
    }

    fun getUnderlineHeight(): Int {
        return underlineHeight
    }

    fun setDividerPadding(dividerPaddingPx: Int) {
        this.dividerPadding = dividerPaddingPx
        invalidate()
    }

    fun getDividerPadding(): Int {
        return dividerPadding
    }

    fun setScrollOffset(scrollOffsetPx: Int) {
        this.scrollOffset = scrollOffsetPx
        invalidate()
    }

    fun getScrollOffset(): Int {
        return scrollOffset
    }

    fun setShouldExpand(shouldExpand: Boolean) {
        this.shouldExpand = shouldExpand
        requestLayout()
    }

    fun getShouldExpand(): Boolean {
        return shouldExpand
    }

    fun setAllCaps(textAllCaps: Boolean) {
        this.isTextAllCaps = textAllCaps
    }

    fun setTextColorResource(resId: Int) {
        this.tabTextColor = resources.getColor(resId)
        updateTabStyles()
    }

    fun setTypeface(typeface: Typeface, style: Int) {
        this.tabTypeface = typeface
        this.tabTypefaceStyle = style
        updateTabStyles()
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentPosition = savedState.currentPosition
        requestLayout()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState!!)
        savedState.currentPosition = currentPosition
        return savedState
    }

    internal class SavedState : View.BaseSavedState {
        var currentPosition: Int = 0

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            currentPosition = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(currentPosition)
        }

        companion object {

            @JvmStatic
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {

        // @formatter:off
        private val ATTRS = intArrayOf(android.R.attr.textSize, android.R.attr.textColor)
    }
}

