package cn.yue.base.utils.debug

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import cn.yue.base.utils.Utils
import cn.yue.base.utils.app.DisplayUtils
import java.lang.ref.WeakReference
import java.lang.reflect.Field

object ToastUtils {

    private var gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
    private var xOffset = 0
    private var yOffset = DisplayUtils.dip2px(100)
    private var sViewWeakReference: WeakReference<View>? = null
    private var sToast: Toast? = null
    private val sHandler = Handler(Looper.getMainLooper())

    fun setGravity(mGravity: Int, mXOffset: Int, mYOffset: Int) {
        gravity = mGravity
        xOffset = mXOffset
        yOffset = mYOffset
    }

    fun setView(@LayoutRes layoutId: Int) {
        val inflate = Utils.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sViewWeakReference = WeakReference(inflate.inflate(layoutId, null))
    }

    fun setView(view: View?) {
        sViewWeakReference = view?.let { WeakReference(it) }
    }

    fun getView(): View? {
        sViewWeakReference?.apply {
            val view = get()
            if (view != null) {
                return view
            }
        }
        return getToast().view
    }

    /**
     * 安全地显示短时吐司
     *
     * @param text 文本
     */
    @JvmStatic
    fun showShortToastSafe(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            return
        }
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
    fun showLongToastSafe(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            return
        }
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
    fun showShortToast(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            return
        }
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
    fun showLongToast(text: CharSequence?) {
        if (text.isNullOrEmpty()) {
            return
        }
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
    private fun showToast(text: CharSequence, duration: Int) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            sHandler.post { showToast(text, duration) }
            return
        }
        cancelToast()
        // android 11 setView方法失效
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            sViewWeakReference?.apply {
                val view: View? = get()
                if (view != null) {
                    getToast().view = view
                    getToast().duration = duration
                }
            }
        }
        sToast = Toast.makeText(Utils.getContext(), text, duration)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getToast().setGravity(gravity, xOffset, yOffset)
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                hook(getToast())
            }
        }
        getToast().show()
    }

    private fun getToast(): Toast {
        if (sToast == null) {
            sToast = Toast(Utils.getContext())
        }
        return sToast!!
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

    /**
     * 7.1.1版本问题
     * 来自腾讯解决方案
     */
    private var sField_TN: Field? = null
    private var sField_TN_Handler: Field? = null
    @SuppressLint("SoonBlockedPrivateApi")
    private fun initHook() {
        try {
            sField_TN = Toast::class.java.getDeclaredField("mTN")
            if (sField_TN != null) {
                sField_TN!!.isAccessible = true
                sField_TN_Handler = sField_TN!!.type.getDeclaredField("mHandler")
                sField_TN_Handler!!.isAccessible = true
            }
        } catch (e: Exception) {
        }
    }

    private fun hook(toast: Toast) {
        try {
            if (sField_TN == null || sField_TN_Handler == null) {
                initHook()
            }
            val tn = sField_TN!![toast]
            val preHandler = sField_TN_Handler!![tn] as Handler
            sField_TN_Handler!![tn] = SafelyHandlerWrapper(preHandler)
        } catch (e: Exception) {
        }
    }

    private class SafelyHandlerWrapper(private val impl: Handler) : Handler() {
        override fun dispatchMessage(msg: Message) {
            try {
                super.dispatchMessage(msg)
            } catch (e: Exception) {
            }
        }

        override fun handleMessage(msg: Message) {
            impl.handleMessage(msg) //需要委托给原Handler执行
        }

    }
}