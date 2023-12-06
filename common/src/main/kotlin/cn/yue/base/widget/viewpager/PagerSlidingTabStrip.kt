package cn.yue.base.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.*
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import cn.yue.base.R

class PagerSlidingTabStrip @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : HorizontalScrollView(context, attrs, defStyle) {

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
    private var movePosition = 0
    private var movePositionOffset = 0f
    private val indicatorPaint: Paint
    private var indicatorWidth = 0
    private var indicatorHeight = 0
    private var indicatorColor = Color.TRANSPARENT

    private var dividerWidth = 0
    private val dividerPaint: Paint
    private var dividerColor = Color.parseColor("#1f000000")
    private var dividerPadding = 12

    private var shouldExpand = false
    private var isTextAllCaps = true
    private var scrollOffset = 52
    private var tabPadding = 10

    private var tabTextSize = 36
    private var tabTextColor = Color.parseColor("#999999")
    private var tabSelectedTextColor = Color.parseColor("#333333")
    private var tabTypeface= Typeface.NORMAL
    private var tabSelectedTypeface = Typeface.BOLD
    private var lastScrollX = 0
    private var tabBackground = 0

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
        val a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip)
        tabTextSize = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTextSize, tabTextSize)
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextColor, tabTextColor)
        dividerWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerWidth, dividerWidth)
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor)
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding)
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPadding, tabPadding)
        tabBackground = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackground)
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand)
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset)
        isTextAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, isTextAllCaps)
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsTextColor, tabTextColor)
        tabSelectedTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsSelectedTextColor, tabSelectedTextColor)
        tabTypeface = a.getInt(R.styleable.PagerSlidingTabStrip_pstsTextStyle, tabTypeface)
        tabSelectedTypeface = a.getInt(R.styleable.PagerSlidingTabStrip_pstsSelectedTextStyle, tabSelectedTypeface)
        indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorWidth, indicatorWidth)
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight)
        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor)
        a.recycle()
        indicatorPaint = Paint()
        indicatorPaint.isAntiAlias = true
        indicatorPaint.style = Paint.Style.FILL
        indicatorPaint.color = indicatorColor
        dividerPaint = Paint()
        dividerPaint.isAntiAlias = true
        dividerPaint.color = dividerColor
        dividerPaint.strokeWidth = dividerWidth.toFloat()
        defaultTabLayoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT)
        expandedTabLayoutParams = LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT, 1.0f)
    }

    fun setViewPager(pager: ViewPager) {
        this.pager = pager
        if (pager.adapter == null) {
            throw IllegalStateException("ViewPager does not have adapter instance.")
        }
        pager.addOnPageChangeListener(pageListener)
        notifyDataSetChanged()
    }

    fun removeOnPageChangeListener() {
        pager?.removeOnPageChangeListener(pageListener)
    }

    fun setViewPagerAutoRefresh(viewPager: ViewPager, autoRefresh: Boolean = true) {
        if (autoRefresh) {
            pager?.let {
                it.removeOnPageChangeListener(pageListener)
                mAdapterChangeListener?.apply {
                    it.removeOnAdapterChangeListener(this)
                }
            }
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
        } else {
            setViewPager(viewPager)
        }
    }

    fun setPagerAdapter(adapter: PagerAdapter, addObserver: Boolean) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter!!.unregisterDataSetObserver(mPagerAdapterObserver!!)
        }
        mPagerAdapter = adapter
        if (addObserver) {
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
        pager?.apply {
            tabCount = adapter?.count ?: 0
            for (i in 0 until tabCount) {
                if (adapter is LayoutTabProvider) {
                    addTab(i, (adapter as LayoutTabProvider).createTabView(i))
                } else if (adapter is IconTabProvider) {
                    addIconTab(i, (adapter as IconTabProvider).getPageIconResId(i))
                } else {
                    addTextTab(i, adapter?.getPageTitle(i).toString())
                }
            }
        }
        updateTabStyles()
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {

            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                pager?.apply {
                    currentPosition = currentItem
                    scrollToChild(currentPosition, 0)
                }
            }
        })
    }


    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        this.delegatePageListener = listener
    }

    fun notifyDataSetChanged() {
        tabsContainer.removeAllViews()
        pager?.apply {
            tabCount = adapter?.count ?: 0
            for (i in 0 until tabCount) {
                if (adapter is LayoutTabProvider) {
                    addTab(i, (adapter as LayoutTabProvider).createTabView(i))
                } else if (adapter is IconTabProvider) {
                    addIconTab(i, (adapter as IconTabProvider).getPageIconResId(i))
                } else {
                    addTextTab(i, adapter?.getPageTitle(i).toString())
                }
            }
        }
        updateTabStyles()
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {

            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                pager?.apply {
                    currentPosition = currentItem
                    scrollToChild(currentPosition, 0)
                }
            }
        })
    }

    private fun addTextTab(position: Int, title: String) {
        val tab = TextView(context)
        tab.text = title
        tab.gravity = Gravity.CENTER
        tab.setSingleLine()
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize.toFloat())
        tab.setTypeface(null, tabTypeface)
        if (isTextAllCaps) {
            tab.isAllCaps = true
        }
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

    fun getTab(position: Int): View {
        return tabsContainer.getChildAt(position)
    }

    private fun updateTabStyles() {
        for (i in 0 until tabCount) {
            val tab = tabsContainer.getChildAt(i)
            tab.setBackgroundResource(tabBackground)
            if (tab is TextView) {
                if (i == currentPosition) {
                    tab.setTextColor(tabSelectedTextColor)
                    tab.setTypeface(null, tabSelectedTypeface)
                } else {
                    tab.setTextColor(tabTextColor)
                    tab.setTypeface(null, tabTypeface)
                }
            } else if (pager!!.adapter is LayoutTabProvider) {
                (pager!!.adapter as LayoutTabProvider).changeTabStyle(tab, i == currentPosition)
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

        // draw indicator line
        if (indicatorHeight > 0 && indicatorWidth > 0) {
            val moveTab = tabsContainer.getChildAt(movePosition)
            val moveLeft = moveTab.left.toFloat()
            val moveRight = moveTab.right.toFloat()
            val moveCenter = (moveLeft + moveRight) / 2

            val nextPosition = movePosition + 1
            val defaultMarginBottom = 5
            if (nextPosition < tabCount) {
                val nextTab = tabsContainer.getChildAt(nextPosition)
                val nextLeft = nextTab.left.toFloat()
                val nextRight = nextTab.right.toFloat()
                val nextCenter = (nextLeft + nextRight) / 2
                val currentCenter = moveCenter + (nextCenter - moveCenter) * movePositionOffset
                canvas.drawRect(
                    currentCenter - indicatorWidth / 2,
                    measuredHeight - indicatorHeight.toFloat() - defaultMarginBottom,
                    currentCenter + indicatorWidth / 2,
                    measuredHeight.toFloat() - defaultMarginBottom,
                    indicatorPaint
                )
            } else {
                canvas.drawRect(
                    moveCenter - indicatorWidth / 2,
                    measuredHeight - indicatorHeight.toFloat() - defaultMarginBottom,
                    moveCenter + indicatorWidth / 2,
                    measuredHeight.toFloat() - defaultMarginBottom,
                    indicatorPaint
                )
            }
        }
        // draw divider
        if (dividerWidth > 0) {
            for (i in 0 until tabCount - 1) {
                val tab = tabsContainer.getChildAt(i)
                canvas.drawLine(
                    tab.right.toFloat(),
                    dividerPadding.toFloat(),
                    tab.right.toFloat(),
                    (height - dividerPadding).toFloat(),
                    dividerPaint
                )
            }
        }
    }

    private inner class PagerAdapterObserver : DataSetObserver() {

        override fun onChanged() {
            populateFromPagerAdapter()
        }

        override fun onInvalidated() {
            populateFromPagerAdapter()
        }
    }

    private inner class AdapterChangeListener : ViewPager.OnAdapterChangeListener {
        private var mAutoRefresh: Boolean = false

        override fun onAdapterChanged(viewPager: ViewPager,
                                      oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
            if (pager === viewPager) {
                newAdapter?.let {
                    setPagerAdapter(it, mAutoRefresh)
                }
            }
        }

        fun setAutoRefresh(autoRefresh: Boolean) {
            mAutoRefresh = autoRefresh
        }
    }

    private inner class PageListener : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            movePosition = position
            movePositionOffset = positionOffset
            invalidate()
            if (position < tabsContainer.childCount) {
                scrollToChild(position, (positionOffset * tabsContainer.getChildAt(position).width).toInt())
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
            currentPosition = position
            if (position < tabsContainer.childCount) {
                updateTabStyles()
                invalidate()
            }
            if (delegatePageListener != null) {
                delegatePageListener!!.onPageSelected(position)
            }
        }

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

            @JvmField
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
}

