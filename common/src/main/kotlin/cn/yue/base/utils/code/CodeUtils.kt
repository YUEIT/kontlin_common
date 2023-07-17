package cn.yue.base.utils.code

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.viewpager.widget.ViewPager.OnPageChangeListener

/**
 * Description :
 * Created by yue on 2023/6/27
 */

abstract class SimpleAnimationListener : AnimatorListener {
	
	override fun onAnimationStart(animation: Animator) {
	
	}
	
	override fun onAnimationEnd(animation: Animator) {
	
	}
	
	override fun onAnimationCancel(animation: Animator) {
	
	}
	
	override fun onAnimationRepeat(animation: Animator) {
	
	}
}

abstract class SimpleTextWatch : TextWatcher {
	
	override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
	
	}
	
	override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
	
	}
	
	override fun afterTextChanged(s: Editable?) {
	
	}
}

abstract class SimpleOnPageChangeListener : OnPageChangeListener {
	
	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
	
	}
	
	override fun onPageSelected(position: Int) {
	
	}
	
	override fun onPageScrollStateChanged(state: Int) {
	
	}
}

abstract class SimpleOnScrollListener : OnScrollListener() {
	
	override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
		super.onScrollStateChanged(recyclerView, newState)
	}
	
	override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
		super.onScrolled(recyclerView, dx, dy)
	}
}