package cn.yue.base.common.utils.debug

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

import cn.yue.base.common.utils.Utils


/**
 * 介绍：吐司相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ToastUtils {

    private var sToast: Toast? = null
    private val sHandler = Handler(Looper.getMainLooper())
    private var isJumpWhenMore: Boolean = false

    /**
     * 吐司初始化
     *
     * @param isJumpWhenMore 当连续弹出吐司时，是要弹出新吐司还是只修改文本内容
     *
     * `true`: 弹出新吐司<br></br>`false`: 只修改文本内容
     *
     * 如果为`false`的话可用来做显示任意时长的吐司
     */
    @JvmStatic
    fun init(isJumpWhenMore: Boolean) {
        ToastUtils.isJumpWhenMore = isJumpWhenMore
    }

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    @JvmStatic
    fun showShortToastSafe(text: CharSequence) {
        sHandler.post { showToast(text, Toast.LENGTH_SHORT) }
    }

    /**
     * 安全地显示短时吐司
     *
     * @param resId 资源Id
     */
    @JvmStatic
    fun showShortToastSafe(@StringRes resId: Int) {
        sHandler.post { showToast(resId, Toast.LENGTH_SHORT) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param text 文本
     */
    @JvmStatic
    fun showLongToastSafe(text: CharSequence) {
        sHandler.post { showToast(text, Toast.LENGTH_LONG) }
    }

    /**
     * 安全地显示长时吐司
     *
     * @param resId 资源Id
     */
    @JvmStatic
    fun showLongToastSafe(@StringRes resId: Int) {
        sHandler.post { showToast(resId, Toast.LENGTH_LONG) }
    }

    /**
     * 显示短时吐司
     *
     * @param text 文本
     */
    @JvmStatic
    fun showShortToast(text: CharSequence) {
        showToast(text, Toast.LENGTH_SHORT)
    }

    /**
     * 显示短时吐司
     *
     * @param resId 资源Id
     */
    @JvmStatic
    fun showShortToast(@StringRes resId: Int) {
        showToast(resId, Toast.LENGTH_SHORT)
    }

    /**
     * 显示长时吐司
     *
     * @param text 文本
     */
    @JvmStatic
    fun showLongToast(text: CharSequence) {
        showToast(text, Toast.LENGTH_LONG)
    }

    /**
     * 显示长时吐司
     *
     * @param resId 资源Id
     */
    @JvmStatic
    fun showLongToast(@StringRes resId: Int) {
        showToast(resId, Toast.LENGTH_LONG)
    }

    /**
     * 显示吐司
     *
     * @param resId    资源Id
     * @param duration 显示时长
     */
    fun showToast(@StringRes resId: Int, duration: Int) {
        showToast(Utils.getContext().resources.getText(resId).toString(), duration)
    }


    /**
     * 显示吐司
     *
     * @param text     文本
     * @param duration 显示时长
     */
    fun showToast(text: CharSequence, duration: Int) {
        if (isJumpWhenMore) cancelToast()
        if (sToast == null) {
            sToast = Toast.makeText(Utils.getContext(), text, duration)
        } else {
            sToast!!.setText(text)
            sToast!!.duration = duration
        }
        sToast!!.show()
    }

    /**
     * 取消吐司显示
     */
    @JvmStatic
    fun cancelToast() {
        if (sToast != null) {
            sToast!!.cancel()
            sToast = null
        }
    }
}