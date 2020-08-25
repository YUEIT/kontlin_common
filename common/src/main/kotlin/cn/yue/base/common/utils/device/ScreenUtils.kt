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
    // 创建了一张白纸
    // 给白纸设置宽高
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
    // 创建了一张白纸
    // 给白纸设置宽高
    @JvmStatic
    val screenHeight: Int
        get() {
            val windowManager = Utils.getContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(dm)
            return dm.heightPixels
        }

    /**
     * 设置屏幕为横屏
     *
     * 还有一种就是在Activity中加属性android:screenOrientation="landscape"
     *
     * 不设置Activity的android:configChanges时，切屏会重新调用各个生命周期，切横屏时会执行一次，切竖屏时会执行两次
     *
     * 设置Activity的android:configChanges="orientation"时，切屏还是会重新调用各个生命周期，切横、竖屏时只会执行一次
     *
     * 设置Activity的android:configChanges="orientation|keyboardHidden|screenSize"（4.0以上必须带最后一个参数）时
     * 切屏不会重新调用各个生命周期，只会执行onConfigurationChanged方法
     *
     * @param activity activity
     */
    @JvmStatic
    fun setLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * 设置屏幕为竖屏
     *
     * @param activity activity
     */
    @JvmStatic
    fun setPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 判断是否横屏
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    val isLandscape: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    /**
     * 判断是否竖屏
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    val isPortrait: Boolean
        get() = Utils.getContext().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

}