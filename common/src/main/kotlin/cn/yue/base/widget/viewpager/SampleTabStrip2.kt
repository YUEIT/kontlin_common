package cn.yue.base.widget.viewpager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import cn.yue.base.R
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.base.utils.device.ScreenUtils


/**
 * Description :
 * Created by yue on 2023/7/28
 */

class SampleTabStrip2(context: Context, attrs: AttributeSet? = null)
	: RecyclerView(context, attrs) {
	
	interface LayoutTabProvider {
		fun getItemCount(): Int
		fun createTabView(): View
		fun bindTabView(view: View, position: Int, selectPosition: Int)
	}
	
	private var layoutTabProvider: LayoutTabProvider? = null
	private val pageListener = PageListener()
	private var pager: ViewPager2? = null
	private var tabAdapter: Adapter<*>? = null
	private var currentPosition = 0
	private var shouldExpand = false
	private var indicatorHeight = DisplayUtils.dip2px(2f)
	private var indicatorWidth = DisplayUtils.dip2px(50f)
	private var indicatorMarginBottom = DisplayUtils.dip2px(2f)
	private var movePosition = 0
	private var movePositionOffset = 0f
	private var indicatorPaint = Paint()
	
	init {
		setWillNotDraw(false)
		val a = context.obtainStyledAttributes(attrs, R.styleable.SampleTabStrip2)
		shouldExpand = a.getBoolean(R.styleable.SampleTabStrip2_sts2ShouldExpand, shouldExpand)
		indicatorWidth = a.getDimensionPixelSize(R.styleable.SampleTabStrip2_sts2IndicatorWidth, indicatorWidth.toInt()).toFloat()
		indicatorHeight = a.getDimensionPixelOffset(R.styleable.SampleTabStrip2_sts2IndicatorHeight, indicatorHeight.toInt()).toFloat()
		indicatorMarginBottom = a.getDimensionPixelOffset(R.styleable.SampleTabStrip2_sts2IndicatorMarginBottom, indicatorMarginBottom.toInt()).toFloat()
		indicatorPaint = Paint()
		indicatorPaint.isAntiAlias = true
		indicatorPaint.style = Paint.Style.FILL
		indicatorPaint.color = a.getColor(R.styleable.SampleTabStrip_stsIndicatorColor, Color.TRANSPARENT)
		a.recycle()
		layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
	}
	
	fun setViewPager(viewPager: ViewPager2?) {
		if (viewPager != null) {
			pager = viewPager
			pager!!.unregisterOnPageChangeCallback(pageListener)
			pager!!.registerOnPageChangeCallback(pageListener)
			val adapter = viewPager.adapter
			if (adapter != null) {
				layoutTabProvider = adapter as LayoutTabProvider?
				tabAdapter = TabAdapter()
				setAdapter(tabAdapter)
			}
		}
	}
	
	internal inner class TabAdapter : Adapter<ViewHolder>() {
		
		override fun onCreateViewHolder(
			parent: ViewGroup,
			viewType: Int
		): ViewHolder {
			return ViewHolder(layoutTabProvider!!.createTabView())
		}
		
		override fun onBindViewHolder(holder: ViewHolder, position: Int) {
			if (shouldExpand) {
				val width: Int = measuredWidth / layoutTabProvider!!.getItemCount()
				val layoutParams = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
				holder.itemView.layoutParams = layoutParams
			} else {
				val layoutParams = LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				)
				holder.itemView.layoutParams = layoutParams
			}
			layoutTabProvider!!.bindTabView(holder.itemView, position, currentPosition)
			holder.itemView.setOnClickListener{
				pager!!.currentItem = position
			}
		}
		
		override fun getItemCount(): Int {
			return layoutTabProvider!!.getItemCount()
		}
	}
	
	internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	
	private fun scrollToChild(position: Int) {
		if (tabAdapter!!.itemCount > 0 && position < tabAdapter!!.itemCount) {
			val linearLayoutManager = layoutManager as LinearLayoutManager
			val childWidth = linearLayoutManager.getChildAt(position)?.width ?: 0
			val offsetX = (ScreenUtils.screenWidth - childWidth) / 2
			linearLayoutManager.scrollToPositionWithOffset(position, offsetX)
			tabAdapter!!.notifyDataSetChanged()
		}
	}
	
	private inner class PageListener : OnPageChangeCallback() {
		
		/**
		 * @param position【 假设0 -> 1 向后滑为 0 】【 假设1 -> 0 向前滑 0 】
		 * @param positionOffset 0 ~ 1 向后滑动为0->1； 向前滑为1->0
		 */
		override fun onPageScrolled(
			position: Int,
			positionOffset: Float,
			positionOffsetPixels: Int
		) {
			movePositionOffset = positionOffset
			movePosition = position
			invalidate()
			if (positionOffset == 0f) {
				scrollToChild(currentPosition)
			}
		}
		
		override fun onPageScrollStateChanged(state: Int) {
//			if (state == ViewPager.SCROLL_STATE_IDLE) {
//				scrollToChild(pager!!.currentItem)
//			}
		}
		
		override fun onPageSelected(position: Int) {
			currentPosition = position
//			if (position < tabAdapter!!.itemCount) {
//				scrollToChild(position)
//			}
		}
	}
	
	fun getTabCount(): Int {
		return tabAdapter?.itemCount ?: 0
	}
	
	fun setShouldExpand(shouldExpand: Boolean) {
		this.shouldExpand = shouldExpand
		requestLayout()
	}
	
	fun getShouldExpand(): Boolean {
		return shouldExpand
	}
	
	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		if (isInEditMode || childCount == 0 || indicatorHeight == 0f || indicatorWidth == 0f) {
			return
		}
		
		// default: line below current tab
		val linearLayoutManager = layoutManager as LinearLayoutManager?
		
		val moveTab = linearLayoutManager?.findViewByPosition(movePosition) ?: return
		val moveLeft = moveTab.left.toFloat()
		val moveRight = moveTab.right.toFloat()
		val moveCenter = (moveLeft + moveRight) / 2

		val nextPosition = movePosition + 1
		if (nextPosition < (adapter?.itemCount ?: 0)) {
			val nextTab = linearLayoutManager.findViewByPosition(nextPosition) ?: return
			val nextLeft = nextTab.left.toFloat()
			val nextRight = nextTab.right.toFloat()
			val nextCenter = (nextLeft + nextRight) / 2
			val currentCenter = moveCenter + (nextCenter - moveCenter) * movePositionOffset
			canvas.drawRect(currentCenter - indicatorWidth / 2,
				measuredHeight - indicatorHeight - indicatorMarginBottom,
				currentCenter + indicatorWidth / 2,
				measuredHeight.toFloat() - indicatorMarginBottom,
				indicatorPaint
			)
		} else {
			canvas.drawRect(moveCenter - indicatorWidth / 2,
				measuredHeight - indicatorHeight  - indicatorMarginBottom,
				moveCenter + indicatorWidth / 2,
				measuredHeight.toFloat() - indicatorMarginBottom,
				indicatorPaint
			)
		}
	}
	
	public override fun onRestoreInstanceState(state: Parcelable) {
		val savedState = state as SavedState
		super.onRestoreInstanceState(savedState.superState)
		currentPosition = savedState.currentPosition
		requestLayout()
	}
	
	public override fun onSaveInstanceState(): Parcelable {
		val superState = super.onSaveInstanceState()
		val savedState = SavedState(superState)
		savedState.currentPosition = currentPosition
		return savedState
	}
	
	internal class SavedState : BaseSavedState {
		var currentPosition = 0
		
		constructor(superState: Parcelable?) : super(superState) {}
		
		private constructor(parcel: Parcel) : super(parcel) {
			currentPosition = parcel.readInt()
		}
		
		override fun writeToParcel(dest: Parcel, flags: Int) {
			super.writeToParcel(dest, flags)
			dest.writeInt(currentPosition)
		}
		
		override fun describeContents(): Int {
			return 0
		}
		
		companion object CREATOR : Parcelable.Creator<SavedState> {
			override fun createFromParcel(parcel: Parcel): SavedState {
				return SavedState(parcel)
			}
			
			override fun newArray(size: Int): Array<SavedState?> {
				return arrayOfNulls(size)
			}
		}
	}
}