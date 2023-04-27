

import android.content.Context
import android.view.View
import android.view.WindowManager
import cn.yue.test.float.FloatWindowView
import cn.yue.test.float.SettingsCompat


/**
悬浮Manager
 */
class FloatWindowManager {

    private var mWindowManager: WindowManager? = null
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
        val windowView = FloatWindowView(context)
        windowView.setOnCloseListener {
            closeFloatWindow()
        }
        windowView.setOnUpdateListener { contentView, mLayoutParams ->
            mWindowManager?.updateViewLayout(contentView, mLayoutParams)
        }
        mWindowManager!!.addView(windowView, windowView.layoutParams)
        mContentView = windowView
    }

    fun showFloatWindow(context: Context) {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initView(context)
    }
    
    fun closeFloatWindow() {
        try {
            mWindowManager?.removeView(mContentView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}