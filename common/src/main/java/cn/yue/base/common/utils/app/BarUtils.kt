package cn.yue.base.common.utils.app

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout

/**
 * 介绍：状态栏相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class BarUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    class StatusBarView : View {
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        constructor(context: Context) : super(context) {}
    }

    companion object {

        val DEFAULT_STATUS_BAR_ALPHA = 112

        /**
         * 设置状态栏样式
         * @param activity
         * @param isFillUpTop   是否置顶，全屏，布局在状态栏底部
         * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
         * @param bgColor       状态栏背景色
         */
        fun setStyle(activity: Activity, isFillUpTop: Boolean, isDarkIcon: Boolean, bgColor: Int) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return
            }
            val window = activity.window
            val decorView = window.decorView
            if (isFillUpTop) {
                if (isDarkIcon) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            } else {
                if (isDarkIcon) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = bgColor
        }

        /**
         * 设置状态栏样式
         * @param isFillUpTop   是否置顶，全屏，布局在状态栏底部
         * @param isDarkIcon    状态栏内的时间等ICON，文字颜色为暗色系
         * @param bgColor       状态栏背景色
         */
        fun setStyle(window: Window, isFillUpTop: Boolean, isDarkIcon: Boolean, bgColor: Int) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return
            }
            val decorView = window.decorView
            if (isFillUpTop) {
                if (isDarkIcon) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            } else {
                if (isDarkIcon) {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = bgColor
        }

        /**
         * 设置状态栏颜色
         *
         * @param activity       需要设置的activity
         * @param color          状态栏颜色值
         * @param statusBarAlpha 状态栏透明度
         */

        @JvmStatic
        fun setColor(activity: Activity, color: Int, statusBarAlpha: Int = DEFAULT_STATUS_BAR_ALPHA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.statusBarColor = calculateStatusColor(color, statusBarAlpha)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                val decorView = activity.window.decorView as ViewGroup
                val count = decorView.childCount
                if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
                    decorView.getChildAt(count - 1).setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
                } else {
                    val statusView = createStatusBarView(activity, color, statusBarAlpha)
                    decorView.addView(statusView)
                }
                setRootView(activity)
            }
        }

        /**
         * 为滑动返回界面设置状态栏颜色
         *
         * @param activity       需要设置的activity
         * @param color          状态栏颜色值
         * @param statusBarAlpha 状态栏透明度
         */
        @JvmStatic
        fun setColorForSwipeBack(activity: Activity, color: Int, statusBarAlpha: Int = DEFAULT_STATUS_BAR_ALPHA) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
                contentView.setPadding(0, getStatusBarHeight(activity), 0, 0)
                contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
                setTransparentForWindow(activity)
            }
        }

        /**
         * 设置状态栏纯色 不加半透明效果
         *
         * @param activity 需要设置的 activity
         * @param color    状态栏颜色值
         */
        @JvmStatic
        fun setColorNoTranslucent(activity: Activity, color: Int) {
            setColor(activity, color, 0)
        }

        /**
         * 设置状态栏颜色(5.0以下无半透明效果,不建议使用)
         *
         * @param activity 需要设置的 activity
         * @param color    状态栏颜色值
         */
        @Deprecated("")
        @JvmStatic
        fun setColorDiff(activity: Activity, color: Int) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 生成一个状态栏大小的矩形
            val decorView = activity.window.decorView as ViewGroup
            val count = decorView.childCount
            if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
                decorView.getChildAt(count - 1).setBackgroundColor(color)
            } else {
                val statusView = createStatusBarView(activity, color)
                decorView.addView(statusView)
            }
            setRootView(activity)
        }

        /**
         * 使状态栏半透明
         *
         *
         * 适用于图片作为背景的界面,此时需要图片填充到状态栏
         *
         * @param activity       需要设置的activity
         * @param statusBarAlpha 状态栏透明度
         */
        @JvmStatic
        fun setTranslucent(activity: Activity, statusBarAlpha: Int = DEFAULT_STATUS_BAR_ALPHA) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            setTransparent(activity)
            addTranslucentView(activity, statusBarAlpha)
        }

        /**
         * 针对根布局是 CoordinatorLayout, 使状态栏半透明
         *
         *
         * 适用于图片作为背景的界面,此时需要图片填充到状态栏
         *
         * @param activity       需要设置的activity
         * @param statusBarAlpha 状态栏透明度
         */
        @JvmStatic
        fun setTranslucentForCoordinatorLayout(activity: Activity, statusBarAlpha: Int) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            transparentStatusBar(activity)
            addTranslucentView(activity, statusBarAlpha)
        }

        /**
         * 设置状态栏全透明
         *
         * @param activity 需要设置的activity
         */
        @JvmStatic
        fun setTransparent(activity: Activity) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            transparentStatusBar(activity)
            setRootView(activity)
        }

        /**
         * 使状态栏透明(5.0以上半透明效果,不建议使用)
         *
         *
         * 适用于图片作为背景的界面,此时需要图片填充到状态栏
         *
         * @param activity 需要设置的activity
         */
        @Deprecated("")
        @JvmStatic
        fun setTranslucentDiff(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 设置状态栏透明
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                setRootView(activity)
            }
        }

        /**
         * 为DrawerLayout 布局设置状态栏颜色,纯色
         *
         * @param activity     需要设置的activity
         * @param drawerLayout DrawerLayout
         * @param color        状态栏颜色值
         */
        @JvmStatic
        fun setColorNoTranslucentForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout, color: Int) {
            setColorForDrawerLayout(activity, drawerLayout, color, 0)
        }

        /**
         * 为DrawerLayout 布局设置状态栏变色
         *
         * @param activity       需要设置的activity
         * @param drawerLayout   DrawerLayout
         * @param color          状态栏颜色值
         * @param statusBarAlpha 状态栏透明度
         */
        @JvmStatic
        fun setColorForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout, color: Int,
                                    statusBarAlpha: Int = DEFAULT_STATUS_BAR_ALPHA) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
            // 生成一个状态栏大小的矩形
            // 添加 statusBarView 到布局中
            val contentLayout = drawerLayout.getChildAt(0) as ViewGroup
            if (contentLayout.childCount > 0 && contentLayout.getChildAt(0) is StatusBarView) {
                contentLayout.getChildAt(0).setBackgroundColor(calculateStatusColor(color, statusBarAlpha))
            } else {
                val statusBarView = createStatusBarView(activity, color)
                contentLayout.addView(statusBarView, 0)
            }
            // 内容布局不是 LinearLayout 时,设置padding top
            if (contentLayout !is LinearLayout && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1)
                        .setPadding(contentLayout.paddingLeft, getStatusBarHeight(activity) + contentLayout.paddingTop,
                                contentLayout.paddingRight, contentLayout.paddingBottom)
            }
            // 设置属性
            val drawer = drawerLayout.getChildAt(1) as ViewGroup
            drawerLayout.fitsSystemWindows = false
            contentLayout.fitsSystemWindows = false
            contentLayout.clipToPadding = true
            drawer.fitsSystemWindows = false

            addTranslucentView(activity, statusBarAlpha)
        }

        /**
         * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
         *
         * @param activity     需要设置的activity
         * @param drawerLayout DrawerLayout
         * @param color        状态栏颜色值
         */
        @JvmStatic
        fun setColorForDrawerLayoutDiff(activity: Activity, drawerLayout: DrawerLayout, color: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // 生成一个状态栏大小的矩形
                val contentLayout = drawerLayout.getChildAt(0) as ViewGroup
                if (contentLayout.childCount > 0 && contentLayout.getChildAt(0) is StatusBarView) {
                    contentLayout.getChildAt(0).setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA))
                } else {
                    // 添加 statusBarView 到布局中
                    val statusBarView = createStatusBarView(activity, color)
                    contentLayout.addView(statusBarView, 0)
                }
                // 内容布局不是 LinearLayout 时,设置padding top
                if (contentLayout !is LinearLayout && contentLayout.getChildAt(1) != null) {
                    contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0)
                }
                // 设置属性
                val drawer = drawerLayout.getChildAt(1) as ViewGroup
                drawerLayout.fitsSystemWindows = false
                contentLayout.fitsSystemWindows = false
                contentLayout.clipToPadding = true
                drawer.fitsSystemWindows = false
            }
        }

        /**
         * 为 DrawerLayout 布局设置状态栏透明
         *
         * @param activity       需要设置的activity
         * @param drawerLayout   DrawerLayout
         * @param statusBarAlpha 透明度
         */
        @JvmStatic
        fun setTranslucentForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout, statusBarAlpha: Int = DEFAULT_STATUS_BAR_ALPHA) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            setTransparentForDrawerLayout(activity, drawerLayout)
            addTranslucentView(activity, statusBarAlpha)
        }

        /**
         * 为 DrawerLayout 布局设置状态栏透明
         *
         * @param activity     需要设置的activity
         * @param drawerLayout DrawerLayout
         */
        @JvmStatic
        fun setTransparentForDrawerLayout(activity: Activity, drawerLayout: DrawerLayout) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }

            val contentLayout = drawerLayout.getChildAt(0) as ViewGroup
            // 内容布局不是 LinearLayout 时,设置padding top
            if (contentLayout !is LinearLayout && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0)
            }

            // 设置属性
            val drawer = drawerLayout.getChildAt(1) as ViewGroup
            drawerLayout.fitsSystemWindows = false
            contentLayout.fitsSystemWindows = false
            contentLayout.clipToPadding = true
            drawer.fitsSystemWindows = false
        }

        /**
         * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
         *
         * @param activity     需要设置的activity
         * @param drawerLayout DrawerLayout
         */
        @Deprecated("")
        @JvmStatic
        fun setTranslucentForDrawerLayoutDiff(activity: Activity, drawerLayout: DrawerLayout) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 设置状态栏透明
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                // 设置内容布局属性
                val contentLayout = drawerLayout.getChildAt(0) as ViewGroup
                contentLayout.fitsSystemWindows = true
                contentLayout.clipToPadding = true
                // 设置抽屉布局属性
                val vg = drawerLayout.getChildAt(1) as ViewGroup
                vg.fitsSystemWindows = false
                // 设置 DrawerLayout 属性
                drawerLayout.fitsSystemWindows = false
            }
        }

        /**
         * 为头部是 ImageView 的界面设置状态栏全透明
         *
         * @param activity       需要设置的activity
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTransparentForImageView(activity: Activity, needOffsetView: View) {
            setTranslucentForImageView(activity, 0, needOffsetView)
        }

        /**
         * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
         *
         * @param activity       需要设置的activity
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTranslucentForImageView(activity: Activity, needOffsetView: View) {
            setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView)
        }

        /**
         * 为头部是 ImageView 的界面设置状态栏透明
         *
         * @param activity       需要设置的activity
         * @param statusBarAlpha 状态栏透明度
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTranslucentForImageView(activity: Activity, statusBarAlpha: Int, needOffsetView: View?) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }
            setTransparentForWindow(activity)
            addTranslucentView(activity, statusBarAlpha)
            if (needOffsetView != null) {
                val layoutParams = needOffsetView.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, getStatusBarHeight(activity), 0, 0)
            }
        }

        /**
         * 为 fragment 头部是 ImageView 的设置状态栏透明
         *
         * @param activity       fragment 对应的 activity
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTranslucentForImageViewInFragment(activity: Activity, needOffsetView: View) {
            setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView)
        }

        /**
         * 为 fragment 头部是 ImageView 的设置状态栏透明
         *
         * @param activity       fragment 对应的 activity
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTransparentForImageViewInFragment(activity: Activity, needOffsetView: View) {
            setTranslucentForImageViewInFragment(activity, 0, needOffsetView)
        }

        /**
         * 为 fragment 头部是 ImageView 的设置状态栏透明
         *
         * @param activity       fragment 对应的 activity
         * @param statusBarAlpha 状态栏透明度
         * @param needOffsetView 需要向下偏移的 View
         */
        @JvmStatic
        fun setTranslucentForImageViewInFragment(activity: Activity, statusBarAlpha: Int, needOffsetView: View) {
            setTranslucentForImageView(activity, statusBarAlpha, needOffsetView)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                clearPreviousSetting(activity)
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////

        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun clearPreviousSetting(activity: Activity) {
            val decorView = activity.window.decorView as ViewGroup
            val count = decorView.childCount
            if (count > 0 && decorView.getChildAt(count - 1) is StatusBarView) {
                decorView.removeViewAt(count - 1)
                val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
                rootView.setPadding(0, 0, 0, 0)
            }
        }

        /**
         * 添加半透明矩形条
         *
         * @param activity       需要设置的 activity
         * @param statusBarAlpha 透明值
         */
        private fun addTranslucentView(activity: Activity, statusBarAlpha: Int) {
            val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            if (contentView.childCount > 1) {
                contentView.getChildAt(1).setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0))
            } else {
                contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha))
            }
        }

        /**
         * 生成一个和状态栏大小相同的彩色矩形条
         *
         * @param activity 需要设置的 activity
         * @param color    状态栏颜色值
         * @return 状态栏矩形条
         */
        private fun createStatusBarView(activity: Activity, color: Int): StatusBarView {
            // 绘制一个和状态栏一样高的矩形
            val statusBarView = StatusBarView(activity)
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
            statusBarView.layoutParams = params
            statusBarView.setBackgroundColor(color)
            return statusBarView
        }

        /**
         * 生成一个和状态栏大小相同的半透明矩形条
         *
         * @param activity 需要设置的activity
         * @param color    状态栏颜色值
         * @param alpha    透明值
         * @return 状态栏矩形条
         */
        private fun createStatusBarView(activity: Activity, color: Int, alpha: Int): StatusBarView {
            // 绘制一个和状态栏一样高的矩形
            val statusBarView = StatusBarView(activity)
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
            statusBarView.layoutParams = params
            statusBarView.setBackgroundColor(calculateStatusColor(color, alpha))
            return statusBarView
        }

        /**
         * 设置根布局参数
         */
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private fun setRootView(activity: Activity) {
            val rootView = (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0) as ViewGroup
            rootView.fitsSystemWindows = true
            rootView.clipToPadding = true
        }

        /**
         * 设置透明
         */
        private fun setTransparentForWindow(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.statusBarColor = Color.TRANSPARENT
                activity.window
                        .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window
                        .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }

        /**
         * 使状态栏透明
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun transparentStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }

        /**
         * 创建半透明矩形 View
         *
         * @param alpha 透明值
         * @return 半透明 View
         */
        private fun createTranslucentStatusBarView(activity: Activity, alpha: Int): StatusBarView {
            // 绘制一个和状态栏一样高的矩形
            val statusBarView = StatusBarView(activity)
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity))
            statusBarView.layoutParams = params
            statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
            return statusBarView
        }

        /**
         * 获取状态栏高度
         *
         * @param context context
         * @return 状态栏高度
         */
        @JvmStatic
        fun getStatusBarHeight(context: Context): Int {
            var result = -1
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        /**
         * 计算状态栏颜色
         *
         * @param color color值
         * @param alpha alpha值
         * @return 最终的状态栏颜色
         */
        private fun calculateStatusColor(color: Int, alpha: Int): Int {
            val a = 1 - alpha / 255f
            var red = color shr 16 and 0xff
            var green = color shr 8 and 0xff
            var blue = color and 0xff
            red = (red * a + 0.5).toInt()
            green = (green * a + 0.5).toInt()
            blue = (blue * a + 0.5).toInt()
            return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
        }

        /*--------------------------------old--------------------------------*/

        /**
         * 设置透明状态栏（api大于19方可使用）
         *
         * 可在Activity的onCreat()中调用
         *
         * 需在顶部控件布局中加入以下属性让内容出现在状态栏之下
         *
         * android:clipToPadding="true"
         *
         * android:fitsSystemWindows="true"
         *
         * @param activity activity
         */
        @JvmStatic
        fun setTransparentStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                //透明状态栏
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                //透明导航栏
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            }
        }

        /**
         * 隐藏状态栏
         *
         * 也就是设置全屏，一定要在setContentView之前调用，否则报错
         *
         * 此方法Activity可以继承AppCompatActivity
         *
         * 启动的时候状态栏会显示一下再隐藏，比如QQ的欢迎界面
         *
         * 在配置文件中Activity加属性android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
         *
         * 如加了以上配置Activity不能继承AppCompatActivity，会报错
         *
         * @param activity activity
         */
        @JvmStatic
        fun hideStatusBar(activity: Activity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        /**
         * 判断状态栏是否存在
         *
         * @param activity activity
         * @return `true`: 存在<br></br>`false`: 不存在
         */
        @JvmStatic
        fun isStatusBarExists(activity: Activity): Boolean {
            val params = activity.window.attributes
            return params.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != WindowManager.LayoutParams.FLAG_FULLSCREEN
        }

        /**
         * 获取ActionBar高度
         *
         * @param activity activity
         * @return ActionBar高度
         */
        @JvmStatic
        fun getActionBarHeight(activity: Activity): Int {
            val tv = TypedValue()
            return if (activity.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                TypedValue.complexToDimensionPixelSize(tv.data, activity.resources.displayMetrics)
            } else 0
        }

        /**
         * 显示通知栏
         *
         * 需添加权限 `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR"/>`
         *
         * @param context        上下文
         * @param isSettingPanel `true`: 打开设置<br></br>`false`: 打开通知
         */
        @JvmStatic
        fun showNotificationBar(context: Context, isSettingPanel: Boolean) {
            val methodName = if (Build.VERSION.SDK_INT <= 16)
                "expand"
            else
                if (isSettingPanel) "expandSettingsPanel" else "expandNotificationsPanel"
            invokePanels(context, methodName)
        }

        /**
         * 隐藏通知栏
         *
         * 需添加权限 `<uses-permission android:name="android.permission.EXPAND_STATUS_BAR"/>`
         *
         * @param context 上下文
         */
        @JvmStatic
        fun hideNotificationBar(context: Context) {
            val methodName = if (Build.VERSION.SDK_INT <= 16) "collapse" else "collapsePanels"
            invokePanels(context, methodName)
        }

        /**
         * 反射唤醒通知栏
         *
         * @param context    上下文
         * @param methodName 方法名
         */
        @SuppressLint("WrongConstant")
        private fun invokePanels(context: Context, methodName: String) {
            try {
                val service = context.getSystemService("statusbar")
                val statusBarManager = Class.forName("android.app.StatusBarManager")
                val expand = statusBarManager.getMethod(methodName)
                expand.invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
