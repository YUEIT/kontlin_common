package cn.yue.base.utils.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.WindowCompat
import cn.yue.base.utils.Utils

object BarUtils {

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    @SuppressLint("DiscouragedApi", "InternalInsetResource")
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

    fun getFixStatusBarHeight(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val realStatusBarHeight = getStatusBarHeight()
            if (realStatusBarHeight <= 50) {
                return DisplayUtils.dip2px(30)
            }
            return realStatusBarHeight
        }
        return 0
    }

    /**
     * 设置状态栏样式
     * @param activity
     * @param isFullScreen   是否置顶，全屏，布局在状态栏底部
     * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
     * @param bgColor       状态栏背景色
     */
    fun setStyle(activity: Activity, isFullStatusBar: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val window = activity.window
        setStyle(window, isFullStatusBar, isDarkIcon, bgColor)
    }

    /**
     * 设置状态栏样式
     * @param isFullStatusBar   是否置顶，全屏，布局在状态栏底部
     * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
     * @param bgColor       状态栏背景色
     */
    fun setStyle(window: Window, isFullStatusBar: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
//        val decorView = window.decorView
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

        try {
            val decorView = window.decorView
            if (isFullStatusBar) {
                //setDecorFitsSystemWindows 会将底部导航栏也变成沉浸式的，这里只需要改状态栏足以
                //WindowCompat.setDecorFitsSystemWindows(window, false)
                window.statusBarColor = Color.TRANSPARENT
                val decorFitsFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                val sysUiVis = window.decorView.systemUiVisibility
                decorView.systemUiVisibility = sysUiVis or decorFitsFlags
            } else {
                WindowCompat.setDecorFitsSystemWindows(window, true)
                window.statusBarColor = bgColor
            }
            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightStatusBars = isDarkIcon
            windowInsetsCompat.isAppearanceLightNavigationBars = isDarkIcon
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setFullScreen(activity: Activity) {
        try {
            val window = activity.window
            val decorView = window.decorView
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                window.addFlags(Window.FEATURE_NO_TITLE)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
                // Translucent status bar
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
            }
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val windowInsetsCompat = WindowCompat.getInsetsController(window, decorView)
            windowInsetsCompat.isAppearanceLightStatusBars = true
            windowInsetsCompat.isAppearanceLightNavigationBars = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun marginToStatusBar(view: View) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.topMargin = getFixStatusBarHeight()
        view.layoutParams = layoutParams
    }

    fun marginToTopBar(view: View) {
        val layoutParams = view.layoutParams as MarginLayoutParams
        layoutParams.topMargin = getFixStatusBarHeight() + DisplayUtils.dip2px(40)
        view.layoutParams = layoutParams
    }
}
