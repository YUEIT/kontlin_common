package cn.yue.base.widget.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Description :
 * Created by yue on 2023/7/27
 */
class ViewPager2NestedHost(context: Context, attributeSet: AttributeSet? = null)
	: FrameLayout(context, attributeSet) {
	
	private var mInitialX = 0f
	private var mInitialY = 0f
	
	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		when (ev.action) {
			MotionEvent.ACTION_DOWN -> {
				mInitialX = ev.x
				mInitialY = ev.y
			}
			MotionEvent.ACTION_MOVE -> {
				//如果是子view已经滚到底或滚到顶了
				if (!canScrollVertically(1) || !canScrollVertically(-1)) {
					if (Math.abs(ev.x - mInitialX) < Math.abs(ev.y - mInitialY)) {
						requestDisallowInterceptTouchEvent(true);
					}
				}
			}
		}
		return super.onInterceptTouchEvent(ev)
	}
	
}