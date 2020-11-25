package cn.yue.base.common.utils.device

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import cn.yue.base.common.utils.Utils

object ScreenUtils {

    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    @JvmStatic
    val screenWidth: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            return dm.widthPixels
        }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高px
     */
    @JvmStatic
    val screenHeight: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            return dm.heightPixels
        }

    @JvmStatic
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    @JvmStatic
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    val isLandscape: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isPortrait: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

}