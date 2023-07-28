package cn.yue.test.float

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import cn.yue.base.utils.app.DisplayUtils
import cn.yue.test.R

class FloatWindowView(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {
	
	private var layoutY = 0
	private var layoutX = 0
	private var animator: ValueAnimator? = null
	private var mContentView: View? = null
	
	init {
		View.inflate(context, R.layout.layout_float, this)
		val contentView = View.inflate(context, R.layout.layout_float, null)
		mContentView = contentView
		val tvClose = contentView.findViewById<TextView>(R.id.tv_close)
		tvClose.setOnClickListener {
			onCloseListener?.invoke()
		}
		val mLayoutParams = WindowManager.LayoutParams()
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
		} else {
			mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
		}
		mLayoutParams.format = PixelFormat.RGBA_8888 //窗口透明
		mLayoutParams.gravity = Gravity.LEFT or Gravity.TOP //窗口位置
		mLayoutParams.flags =
			WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		
		layoutY = getScreenHeight() / 2
		layoutX = getScreenWidth() - DisplayUtils.dip2px(150)
		mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
		mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
		mLayoutParams.x = layoutX
		mLayoutParams.y = layoutY
		this.layoutParams = mLayoutParams
		setListener(this)
	}
	
	private var onCloseListener: (() -> Unit)? = null
	
	fun setOnCloseListener(block: ()->Unit) {
		onCloseListener = block
	}
	
	private var onUpdateListener: ((contentView: View, mLayoutParams: WindowManager.LayoutParams) -> Unit)? = null
	
	fun setOnUpdateListener(onUpdateListener: ((contentView: View, mLayoutParams: WindowManager.LayoutParams) -> Unit)) {
		this.onUpdateListener = onUpdateListener
	}
	
	/**
	 * 设置 悬浮窗 view 滑动事件
	 */
	private fun setListener(contentView: View) {
		val mLayoutParams = contentView.layoutParams as WindowManager.LayoutParams
		contentView.setOnTouchListener(object : View.OnTouchListener {
			var moveX = 0 //动画平移距离
			var startX = 0
			var startY  = 0 //起始点
			var locationX = 0
			var locationY = 0
			var isMove = false //是否在移动
			var startTime: Long = 0
			var finalMoveX = 0 //最后通过动画将mView的X轴坐标移动到finalMoveX
			var downMove = false
			
			override fun onTouch(v: View?, event: MotionEvent): Boolean {
				
				when (event.action) {
					MotionEvent.ACTION_DOWN -> {
						startX = event.rawX.toInt()
						startY = event.rawY.toInt()
						locationX = mLayoutParams.x
						locationY = mLayoutParams.y
						startTime = System.currentTimeMillis()
						isMove = false
						downMove = false
						return false
					}
					MotionEvent.ACTION_MOVE -> {
						//当移动距离大于2时候，刷新界面。
						val dx = (event.rawX - startX).toInt()
						val dy = (event.rawY - startY).toInt()
						if (Math.abs(startX - event.rawX) > 2 || Math.abs(startY - event.rawY) > 2) {
							downMove = true
							mLayoutParams.x = locationX + dx
							mLayoutParams.y = locationY + dy
							//更新mView 的位置
							updateWindowLayout(contentView, mLayoutParams)
						}
						return true
					}
					MotionEvent.ACTION_UP -> {
						val curTime = System.currentTimeMillis()
						isMove = curTime - startTime > 100
						if (isMove) {
							//判断mView是在Window中的位置，以中间为界
							finalMoveX =
								if (mLayoutParams.x + contentView.measuredWidth / 2 >= getScreenWidth() / 2) {
									getScreenWidth() - contentView.measuredWidth
								} else {
									0
								}
							//使用动画移动mView
							animator =
								ValueAnimator.ofInt(mLayoutParams.x, finalMoveX).setDuration(
									Math.abs(mLayoutParams.x - finalMoveX).toLong()
								)
							animator?.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation: ValueAnimator? ->
								if (animation != null) {
									moveX = animation.animatedValue as Int
									mLayoutParams.x = animation.animatedValue as Int
									updateWindowLayout(contentView, mLayoutParams)
								}
							})
							animator?.start()
						}
						return isMove
					}
				}
				return false
			}
		})
	}
	
	private fun updateWindowLayout(contentView: View, mLayoutParams: WindowManager.LayoutParams) {
		try {
			onUpdateListener?.invoke(contentView, mLayoutParams)
		} catch (e : Exception) {
			e.printStackTrace()
		}
	}
	
	private fun getScreenWidth(): Int {
		val displayMetrics: DisplayMetrics = context.resources.displayMetrics
		return displayMetrics.widthPixels
	}
	
	private fun getScreenHeight(): Int {
		val displayMetrics: DisplayMetrics = context.resources.displayMetrics
		return displayMetrics.heightPixels
	}
	
	override fun onAttachedToWindow() {
		super.onAttachedToWindow()
		Log.d("luo", "onAttachedToWindow: ")
	}
	
	override fun onDetachedFromWindow() {
		super.onDetachedFromWindow()
		Log.d("luo", "onDetachedFromWindow: ")
		
		// 1562 * 600 * 2673
		// 390 * 4
		// 2631 - 400 2231 1800 400
		// 2866
		
		// 390 * 4 * 600
		// 2673
		
		// 1326 1603 600 * 2 + 400 * 2
		// 3309 600 2709 1800 = 900
		
		// (500 * 4) * 600 + 664 * 640
		// 2404
		
		// 4329
		// (400 * 2 * 4) + 1129
		// 3 2
		// 2992
		
	}
}