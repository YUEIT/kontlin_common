package cn.yue.base.common.utils.constant

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

import cn.yue.base.common.utils.Utils


/**
 * 介绍：尺寸相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class SizeUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 获取到View尺寸的监听
     */
    interface onGetSizeListener {
        fun onGetSize(view: View)
    }

    companion object {

        /**
         * dp转px
         *
         * @param dpValue dp值
         * @return px值
         */
        @JvmStatic
        fun dp2px(dpValue: Float): Int {
            val scale = Utils.getContext().resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }

        /**
         * px转dp
         *
         * @param pxValue px值
         * @return dp值
         */
        @JvmStatic
        fun px2dp(pxValue: Float): Int {
            val scale = Utils.getContext().resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * sp转px
         *
         * @param spValue sp值
         * @return px值
         */
        @JvmStatic
        fun sp2px(spValue: Float): Int {
            val fontScale = Utils.getContext().resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * px转sp
         *
         * @param pxValue px值
         * @return sp值
         */
        @JvmStatic
        fun px2sp(pxValue: Float): Int {
            val fontScale = Utils.getContext().resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 各种单位转换
         *
         * 该方法存在于TypedValue
         *
         * @param unit    单位
         * @param value   值
         * @param metrics DisplayMetrics
         * @return 转换结果
         */
        @JvmStatic
        fun applyDimension(unit: Int, value: Float, metrics: DisplayMetrics): Float {
            when (unit) {
                TypedValue.COMPLEX_UNIT_PX -> return value
                TypedValue.COMPLEX_UNIT_DIP -> return value * metrics.density
                TypedValue.COMPLEX_UNIT_SP -> return value * metrics.scaledDensity
                TypedValue.COMPLEX_UNIT_PT -> return value * metrics.xdpi * (1.0f / 72)
                TypedValue.COMPLEX_UNIT_IN -> return value * metrics.xdpi
                TypedValue.COMPLEX_UNIT_MM -> return value * metrics.xdpi * (1.0f / 25.4f)
            }
            return 0f
        }

        /**
         * 在onCreate中获取视图的尺寸
         *
         * 需回调onGetSizeListener接口，在onGetSize中获取view宽高
         *
         * 用法示例如下所示
         * <pre>
         * SizeUtils.forceGetViewSize(view, new SizeUtils.onGetSizeListener() {
         * Override
         * public void onGetSize(View view) {
         * view.getWidth();
         * }
         * });
        </pre> *
         *
         * @param view     视图
         * @param listener 监听器
         */
        @JvmStatic
        fun forceGetViewSize(view: View, listener: onGetSizeListener?) {
            view.post {
                listener?.onGetSize(view)
            }
        }

        /**
         * 测量视图尺寸
         *
         * @param view 视图
         * @return arr[0]: 视图宽度, arr[1]: 视图高度
         */
        @JvmStatic
        fun measureView(view: View): IntArray {
            var lp: ViewGroup.LayoutParams? = view.layoutParams
            if (lp == null) {
                lp = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width)
            val lpHeight = lp.height
            val heightSpec: Int
            if (lpHeight > 0) {
                heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
            view.measure(widthSpec, heightSpec)
            return intArrayOf(view.measuredWidth, view.measuredHeight)
        }

        /**
         * 获取测量视图宽度
         *
         * @param view 视图
         * @return 视图宽度
         */
        @JvmStatic
        fun getMeasuredWidth(view: View): Int {
            return measureView(view)[0]
        }

        /**
         * 获取测量视图高度
         *
         * @param view 视图
         * @return 视图高度
         */
        @JvmStatic
        fun getMeasuredHeight(view: View): Int {
            return measureView(view)[1]
        }
    }
}
