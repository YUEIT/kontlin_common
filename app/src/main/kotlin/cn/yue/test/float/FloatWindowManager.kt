

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import cn.yue.test.R
import cn.yue.test.float.SettingsCompat


/**
悬浮Manager
 */
class FloatWindowManager {

    private var mWindowManager: WindowManager? = null
    private var layoutY = 0
    private var layoutX = 0
    private var animator: ValueAnimator? = null
    private var mContentView: View? = null
    /**
     * 是否有悬浮框权限
     *
     * @return
     */
    fun requestPermission(context: Context): Boolean {
        return SettingsCompat.canDrawOverlays(context,
            isShowDialog = false,
            isShowPermission = false
        )
    }

    /**
     * 加载 悬浮窗   没有内容
     */
    @Synchronized
    private fun initView(context: Context) {
        val contentView = View.inflate(context, R.layout.layout_float, null)
        mContentView = contentView
        val tvClose = contentView.findViewById<TextView>(R.id.tv_close)
        tvClose.setOnClickListener {
            closeFloatWindow()
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
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        layoutY = displayMetrics.heightPixels / 2
        layoutX = displayMetrics.widthPixels - contentView!!.measuredWidth
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams.x = layoutX
        mLayoutParams.y = layoutY
        mWindowManager!!.addView(contentView, mLayoutParams)
        setListener(contentView)
    }

    fun showFloatWindow(context: Context) {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initView(context)
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
            var isMove = false //是否在移动
            var startTime: Long = 0
            var finalMoveX = 0 //最后通过动画将mView的X轴坐标移动到finalMoveX
            var downMove = false

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x.toInt()
                        startY = event.y.toInt()
                        startTime = System.currentTimeMillis()
                        isMove = false
                        downMove = false
                        return false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //当移动距离大于2时候，刷新界面。
                        if (Math.abs(startX - event.x) > 2 || Math.abs(startY - event.y) > 2) {
                            downMove = true
                            mLayoutParams.x = (event.rawX - startX).toInt()
                            mLayoutParams.y = (event.rawY - startY).toInt()
                            updateViewLayout(contentView, mLayoutParams) //更新mView 的位置
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val curTime = System.currentTimeMillis()
                        isMove = curTime - startTime > 100
                        if (isMove) {
                            //判断mView是在Window中的位置，以中间为界
                            finalMoveX =
                                if (mLayoutParams.x + contentView.measuredWidth / 2 >= mWindowManager!!.defaultDisplay.width / 2) {
                                    mWindowManager!!.defaultDisplay.width - contentView.measuredWidth
                                } else {
                                    0
                                }
                            //使用动画移动mView
                            animator =
                                ValueAnimator.ofInt(mLayoutParams.x, finalMoveX).setDuration(
                                    Math.abs(mLayoutParams.x - finalMoveX).toLong()
                                )
                            animator?.addUpdateListener(AnimatorUpdateListener { animation: ValueAnimator? ->
                                if (animation != null) {
                                    moveX = animation.animatedValue as Int
                                    mLayoutParams.x = animation.animatedValue as Int
                                    updateViewLayout(contentView, mLayoutParams)
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


    private fun updateViewLayout(contentView: View, mLayoutParams: WindowManager.LayoutParams) {
        try {
            mWindowManager?.updateViewLayout(contentView, mLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeFloatWindow() {
        try {
            mWindowManager?.removeView(mContentView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}