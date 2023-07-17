package cn.yue.base.utils.app

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import cn.yue.base.utils.Utils

object DisplayUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    @JvmStatic
    fun dip2px(dpValue: Float): Int {
        val scale = Utils.getContext().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        val scale = Utils.getContext().resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    @JvmStatic
    fun px2dip(pxValue: Float): Int {
        val scale = Utils.getContext().resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp转px（正常字体下，1sp=1dp）
     * @param spValue
     * @return
     */
    @JvmStatic
    fun sp2px(spValue: Float): Int {
        val fontScale = Utils.getContext().resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * sp转dp
     * @param spValue
     * @return
     */
    @JvmStatic
    fun sp2dp(spValue: Float): Int {
        val sp2Px = sp2px(spValue)
        return px2dip(sp2Px.toFloat())
    }

    @JvmStatic
    fun getQToPx(rid: Int): Int {
        return Utils.getContext().resources.getDimension(rid).toInt()
    }

    @JvmStatic
    fun getDrawable(drawable: Int): Drawable? {
        return ContextCompat.getDrawable(Utils.getContext(), drawable)
    }

}
