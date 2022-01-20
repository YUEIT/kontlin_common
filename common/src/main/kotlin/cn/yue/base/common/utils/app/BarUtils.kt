package cn.yue.base.common.utils.app

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.WindowCompat
import cn.yue.base.common.utils.Utils

object BarUtils {

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    fun getStatusBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = windowManager.currentWindowMetrics
            // Gets all excluding insets
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.statusBars()
            )
            return insets.top + insets.bottom
        } else {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
    }

    /**
     * 设置状态栏样式
     * @param activity
     * @param isFullScreen   是否置顶，全屏，布局在状态栏底部
     * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
     * @param bgColor       状态栏背景色
     */
    fun setStyle(activity: Activity, isFullScreen: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val window = activity.window
        setStyle(window, isFullScreen, isDarkIcon, bgColor)
    }

    /**
     * 设置状态栏样式
     * @param isFullScreen   是否置顶，全屏，布局在状态栏底部
     * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
     * @param bgColor       状态栏背景色
     */
    fun setStyle(window: Window, isFullScreen: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val decorView = window.decorView
//        if (isFullScreen) {
//            if (isDarkIcon) {
//                decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            } else {
//                decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            }
//        } else {
//            if (isDarkIcon) {
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            } else {
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            }
//        }
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.statusBarColor = bgColor
        if (isFullScreen) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = Color.TRANSPARENT
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            window.statusBarColor = bgColor
        }
        val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
        windowInsetsCompat?.isAppearanceLightStatusBars = isDarkIcon
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

}
