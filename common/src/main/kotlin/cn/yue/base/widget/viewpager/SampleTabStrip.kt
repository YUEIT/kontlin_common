package cn.yue.base.widget.viewpager

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener
import cn.yue.base.R
import java.util.*

class SampleTabStrip @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : HorizontalScrollView(context, attrs, defStyle) {

    interface LayoutTabProvider {
        /**
         * 创建一个 tab item
         * @param position
         * @return
         */
        fun createTabView(position: Int): View

        /**
         * viewPager切换时，回调所有的item
         * @param v
         * @param isSelect
         */
        fun changeTabStyle(v: View?, position: Int, currentPosition: Int)
    }

    private val defaultTabLayoutParams: LinearLayout.LayoutParams
    private val expandedTabLayoutParams: LinearLayout.LayoutParams
    private val pageListener: PageListener = PageListener()
    var delegatePageListener: ViewPager.OnPageChangeListener? = null
    private var mAdapterChangeListener: AdapterChangeListener? = null
    private val tabsContainer: LinearLayout
    private var pager: ViewPager? = null
    private var mPagerAdapter: PagerAdapter? = null
    private var mPagerAdapterObserver: DataSetObserver? = null
    private var tabCount = 0
    private var currentPosition = 0
    private var movePosition = 0
    private var movePositionOffset = 0f
    private val indicatorPaint: Paint
    private var indicatorWidth = 0
    private var indicatorHeight = 0
    private var indicatorCap = 1
    private var shouldExpand = false
    private var isTextAllCaps = true
    private var scrollOffsetStart = 0
    private var scrollOffsetEnd = 0
    private var tabPadding = 0
    private var lastScrollX = 0
    private var tabBackground = 0
    private var indicatorBitmap: Bitmap? = null
    private var locale: Locale? = null

    init {
        isFillViewport = true
        setWillNotDraw(false)
        tabsContainer = LinearLayout(context)
        tabsContainer.orientation = LinearLayout.HORIZONTAL
        tabsContainer.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(tabsContainer)
        val a = context.obtainStyledAttributes(attrs, R.styleable.SampleTabStrip)
        tabPadding = a.getDimensionPixelSize(R.styleable.SampleTabStrip_stsTabPadding, tabPadding)
        tabBackground = a.getResourceId(R.styleable.SampleTabStrip_stsTabBackground, tabBackground)
        shouldExpand = a.getBoolean(R.styleable.SampleTabStrip_stsShouldExpand, shouldExpand)
        scrollOffsetStart = a.getDimensionPixelSize(R.styleable.SampleTabStrip_stsScrollOffsetStart, scrollOffsetStart)
        scrollOffsetEnd = a.getDimensionPixelSize(R.styleable.SampleTabStrip_stsScrollOffsetEnd, scrollOffsetEnd)
        isTextAllCaps = a.getBoolean(R.styleable.SampleTabStrip_stsTextAllCaps, isTextAllCaps)
        indicatorWidth = a.getDimensionPixelSize(R.styleable.SampleTabStrip_stsIndicatorWidth, indicatorWidth)
        indicatorHeight = a.getDimensionPixelOffset(R.styleable.SampleTabStrip_stsIndicatorHeight, indicatorHeight)
        indicatorCap = a.getInt(R.styleable.SampleTabStrip_stsIndicatorCap, 1)
        val indicatorResource = a.getResourceId(R.styleable.SampleTabStrip_stsIndicatorSrc, 0)
        if (indicatorResource != 0) {
            indicatorBitmap = (ResourcesCompat.getDrawable(resources, indicatorResource, null) as BitmapDrawable).bitmap
        }
        val indicatorColor = a.getColor(R.styleable.SampleTabStrip_stsIndicatorColor, Color.TRANSPARENT)
        indicatorPaint = Paint()
        indicatorPaint.isAntiAlias = true
        indicatorPaint.style = Paint.Style.FILL
        if (indicatorColor != 0) {
            indicatorPaint.color = indicatorColor
        }
        tabsContainer.setPadding(scrollOffsetStart, 0, scrollOffsetEnd, 0)
        a.recycle()
        defaultTabLayoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        expandedTabLayoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f)
        if (locale == null) {
            locale = resources.configuration.locale
        }
    }

    fun setViewPager(pager: ViewPager) {
        this.pager = pager
        checkNotNull(pager.adapter) { "ViewPager does not have adapter instance." }
        pager.addOnPageChangeListener(pageListener!!)
        notifyDataSetChanged()
    }

    fun setViewPagerAutoRefresh(viewPager: ViewPager?) {
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
            pager!!.addOnPageChangeListener(pageListener!!)
            val adapter = viewPager.adapter
            if (adapter != null) {
                // Now we'll populate ourselves from the pager adapter, adding an observer if
                // autoRefresh is enabled
                setPagerAdapter(adapter, true)
            }

            // Add a listener so that we're notified of any adapter changes
            if (mAdapterChangeListener == null) {
                mAdapterChangeListener = AdapterChangeListener()
            }
            mAdapterChangeListener!!.setAutoRefresh(true)
            viewPager.addOnAdapterChangeListener(mAdapterChangeListener!!)
        }
    }

    fun setPagerAdapter(adapter: PagerAdapter?, addObserver: Boolean) {
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

    fun populateFromPagerAdapter() {
        tabsContainer.removeAllViews()
        if (mPagerAdapter != null) {
            currentPosition = pager!!.currentItem
            movePosition = pager!!.currentItem
            val adapterCount = mPagerAdapter!!.count
            tabCount = adapterCount
            for (i in 0 until adapterCount) {
                if (pager!!.adapter is LayoutTabProvider) {
                    addTab(i, (pager!!.adapter as LayoutTabProvider?)!!.createTabView(i))
                }
            }
            updateTabStyles()
            scrollToCenter(currentPosition)
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                @SuppressLint("NewApi")
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        viewTreeObserver.removeGlobalOnLayoutListener(this)
                    } else {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    currentPosition = pager!!.currentItem
                    scrollToCenter(currentPosition)
                }
            })
        }
    }

    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener?) {
        delegatePageListener = listener
    }

    fun notifyDataSetChanged() {
        tabsContainer.removeAllViews()
        tabCount = pager!!.adapter!!.count
        for (i in 0 until tabCount) {
            if (pager!!.adapter is LayoutTabProvider) {
                addTab(i, (pager!!.adapter as LayoutTabProvider?)!!.createTabView(i))
            }
        }
        updateTabStyles()
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            @SuppressLint("NewApi")
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                } else {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
                currentPosition = pager!!.currentItem
                scrollToCenter(currentPosition)
            }
        })
    }

    private fun addTab(position: Int, tab: View) {
        tab.isFocusable = true
        tab.setOnClickListener {
            pager!!.currentItem = position
            onItemClickListener?.invoke(position)
        }
        tab.setPadding(tabPadding, 0, tabPadding, 0)
        tabsContainer.addView(tab, position, if (shouldExpand) expandedTabLayoutParams else defaultTabLayoutParams)
    }

    fun getTab(position: Int): View {
        return tabsContainer.getChildAt(position)
    }

    private fun updateTabStyles() {
        for (i in 0 until tabCount) {
            val v = tabsContainer.getChildAt(i)
            v.setBackgroundResource(tabBackground)
            if (pager!!.adapter is LayoutTabProvider) {
                (pager!!.adapter as LayoutTabProvider?)!!.changeTabStyle(v, i, currentPosition)
            }
        }
    }

    private fun scrollToChild(position: Int, positionOffset: Float, beforeDragScrollX: Int) {
        try {
            if (tabCount == 0 && position >= tabsContainer.childCount) {
                return
            }
            val itemView = if (currentPosition <= position) {
                if (position + 1 >= tabsContainer.childCount) {
                    tabsContainer.getChildAt(position)
                } else {
                    tabsContainer.getChildAt(position + 1)
                }
            } else {
                tabsContainer.getChildAt(position)
            }
            if (scrollX <= 0 && itemView.left < width / 2) {
                return
            }
            if (scrollX >= (tabsContainer.width - width)
                && (tabsContainer.width - itemView.right) < width / 2
            ) {
                return
            }
            val scrollCenter = itemView.left - width / 2 + itemView.width / 2
            val newScrollX = if (currentPosition <= position) {
                //向右滑动

                val offset = (positionOffset * itemView.width * 5).toInt()
                val tempX = beforeDragScrollX + offset
                if (tempX > scrollCenter) {
                    scrollCenter
                } else {
                    tempX
                }
            } else {
                //向左滑动
                val offset = ((itemView.width - positionOffset * itemView.width) * 5).toInt()
                val tempX = beforeDragScrollX - offset
                if (tempX < scrollCenter) {
                    scrollCenter
                } else {
                    tempX
                }
            }
            if (newScrollX != lastScrollX) {
                lastScrollX = newScrollX
                scrollTo(newScrollX, 0)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private fun scrollToVisible(position: Int) {
        if (tabCount == 0 && position >= tabsContainer.childCount) {
            return
        }
        val sMarginLeft = tabsContainer.getChildAt(position).left
        val sMarginRight = tabsContainer.getChildAt(position).right
        val tabWidth = tabsContainer.getChildAt(position).width
        val marginParentLeft = sMarginLeft - scrollX
        val marginParentRight = width - (sMarginLeft - scrollX + tabWidth)
        if (marginParentLeft  >= 0  && marginParentRight >= 0) {
            return
        }
        var offset = 0
        if (marginParentLeft < 0) {
            offset = sMarginLeft
        } else if (marginParentRight < 0) {
            offset = sMarginLeft - width + tabWidth
        }
        val newScrollX = offset
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX
            scrollTo(newScrollX, 0)
        }
    }

    fun scrollToCenter(position: Int) {
        try {
            if (tabCount == 0 && position >= tabsContainer.childCount) {
                return
            }
            val itemView = tabsContainer.getChildAt(position)
            val center = width / 2
            if (scrollX <= 0 && itemView.left < center) {
                return
            }
            if (scrollX >= (tabsContainer.width - width) && (tabsContainer.width - itemView.right) < center) {
                return
            }
            val newScrollX = itemView.left - width / 2 + itemView.width / 2
            if (newScrollX != lastScrollX) {
                lastScrollX = newScrollX
                scrollTo(newScrollX, 0)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    private var onItemClickListener: ((position: Int) -> Unit)? = null

    fun setOnItemClickListener(onItemClickListener: ((position: Int) -> Unit)) {
        this.onItemClickListener = onItemClickListener
    }

    private var indicatorRectF: RectF = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isInEditMode || tabCount == 0 || indicatorHeight == 0 || indicatorWidth == 0) {
            return
        }

        // default: line below current tab
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
            indicatorRectF.left = (currentCenter - indicatorWidth / 2)
            indicatorRectF.top = (measuredHeight - indicatorHeight.toFloat() - defaultMarginBottom)
            indicatorRectF.right = (currentCenter + indicatorWidth / 2)
            indicatorRectF.bottom = (measuredHeight.toFloat() - defaultMarginBottom)
        } else {
            indicatorRectF.left = moveCenter - indicatorWidth / 2
            indicatorRectF.top = measuredHeight - indicatorHeight.toFloat()  - defaultMarginBottom
            indicatorRectF.right = moveCenter + indicatorWidth / 2
            indicatorRectF.bottom = measuredHeight.toFloat() - defaultMarginBottom
        }
        if (indicatorBitmap != null) {
            canvas.drawBitmap(indicatorBitmap!!, null, indicatorRectF, indicatorPaint)
        } else {
            if (indicatorCap == 1) {
                canvas.drawRoundRect(
                    indicatorRectF,
                    indicatorHeight / 2f,
                    indicatorHeight / 2f,
                    indicatorPaint
                )
            } else {
                canvas.drawRect(
                    indicatorRectF,
                    indicatorPaint
                )
            }
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

    private inner class AdapterChangeListener internal constructor() : OnAdapterChangeListener {
        private var mAutoRefresh = false
        override fun onAdapterChanged(viewPager: ViewPager,
                                      oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
            if (pager === viewPager) {
                setPagerAdapter(newAdapter, mAutoRefresh)
            }
        }

        fun setAutoRefresh(autoRefresh: Boolean) {
            mAutoRefresh = autoRefresh
        }
    }

    private inner class PageListener : ViewPager.OnPageChangeListener {

        private var scrollState = 0
        private var beforeDragScrollX = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            movePosition = position
            movePositionOffset = positionOffset
            invalidate()
            if (position < tabsContainer.childCount && scrollState != 2) {
                scrollToChild(position, positionOffset, beforeDragScrollX)
            }
            if (delegatePageListener != null) {
                delegatePageListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            scrollState = state
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToCenter(pager!!.currentItem)
            }
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                beforeDragScrollX = scrollX
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

    fun setShouldExpand(shouldExpand: Boolean) {
        this.shouldExpand = shouldExpand
        requestLayout()
    }

    fun getShouldExpand(): Boolean {
        return shouldExpand
    }

    fun setAllCaps(textAllCaps: Boolean) {
        isTextAllCaps = textAllCaps
    }

    fun getTabPaddingLeftRight(): Int {
        return tabPadding
    }

    fun setTabPaddingLeftRight(paddingPx: Int) {
        tabPadding = paddingPx
        updateTabStyles()
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        currentPosition = savedState.currentPosition
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.currentPosition = currentPosition
        return savedState
    }

    internal class SavedState : BaseSavedState {
        var currentPosition = 0

        constructor(superState: Parcelable?) : super(superState) {}
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
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

}