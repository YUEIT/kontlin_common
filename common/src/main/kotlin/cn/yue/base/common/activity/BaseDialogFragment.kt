package cn.yue.base.common.activity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.activity.TransitionAnimation.TRANSITION_BOTTOM
import cn.yue.base.common.activity.TransitionAnimation.TRANSITION_CENTER
import cn.yue.base.common.activity.TransitionAnimation.TRANSITION_LEFT
import cn.yue.base.common.activity.TransitionAnimation.TRANSITION_RIGHT
import cn.yue.base.common.activity.TransitionAnimation.TRANSITION_TOP
import cn.yue.base.common.activity.TransitionAnimation.getWindowEnterStyle
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.activity.rx.RxLifecycleProvider
import cn.yue.base.common.widget.dialog.WaitDialog
import java.lang.ref.WeakReference

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BaseDialogFragment : DialogFragment() {
    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>
    private var cacheView: View? = null
    lateinit var mActivity: FragmentActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleProvider = initLifecycleProvider()
        lifecycle.addObserver(lifecycleProvider)
        setStyle(STYLE_NO_TITLE, 0)
    }

    open fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return RxLifecycleProvider()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEnterStyle()
        initView(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cacheView == null) {
            cacheView = inflater.inflate(getLayoutId(), container, false)
        }
        return cacheView
    }

    /**
     * 获取布局
     */
    abstract fun getLayoutId(): Int

    /**
     * 初始化组件
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 换场动画
     */
    abstract fun initEnterStyle()

    fun setEnterStyle(transition: Int) {
        if (this.dialog == null) {
            return
        }
        setStyle(STYLE_NO_TITLE, 0)
        val window = this.dialog!!.window
        window!!.decorView.setPadding(0, 0, 0, 0)
        val lp = window.attributes
        setWindowLayoutParams(transition, lp)
        lp.gravity = getWindowGravity(transition)
        lp.windowAnimations = getWindowEnterStyle(transition)
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable())
    }

    fun getWindowGravity(transition: Int): Int {
        return when (transition) {
            TRANSITION_BOTTOM -> Gravity.BOTTOM
            TRANSITION_TOP -> Gravity.TOP
            TRANSITION_LEFT -> Gravity.LEFT
            TRANSITION_RIGHT -> Gravity.RIGHT
            TRANSITION_CENTER -> Gravity.CENTER
            else -> Gravity.CENTER
        }
    }

    fun setWindowLayoutParams(transition: Int, layoutParams: ViewGroup.LayoutParams) {
        when (transition) {
            TRANSITION_BOTTOM, TRANSITION_TOP -> {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            TRANSITION_LEFT, TRANSITION_RIGHT -> {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            TRANSITION_CENTER -> {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            else -> {
                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {

            override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
                if (listener == null) {
                    super.setOnCancelListener(null)
                }
            }

            override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
                if (listener == null) {
                    super.setOnDismissListener(null)
                }
            }

            fun setWeakOnCancelListener(listener: DialogInterface.OnCancelListener?) {
                super.setOnCancelListener(listener)
            }

            fun setWeakOnDismissListener(listener: DialogInterface.OnDismissListener?) {
                super.setOnDismissListener(listener)
            }
        }
        dialog.setWeakOnCancelListener(onCancelListener)
        dialog.setWeakOnDismissListener(onDismissListener)
        return dialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private val onCancelListener by lazy { OnCancelListener(this) }

    class OnCancelListener(fragment: BaseDialogFragment) : DialogInterface.OnCancelListener {
        private val fragmentWeakReference = WeakReference<BaseDialogFragment>(fragment)
        override fun onCancel(dialog: DialogInterface) {
            fragmentWeakReference.get()?.onCancel(dialog)
        }
    }

    private val onDismissListener by lazy { OnDismissListener(this) }

    class OnDismissListener(fragment: BaseDialogFragment) : DialogInterface.OnDismissListener {
        private val fragmentWeakReference = WeakReference<BaseDialogFragment>(fragment)
        override fun onDismiss(dialog: DialogInterface) {
            fragmentWeakReference.get()?.onDismiss(dialog)
        }
    }

    private var waitDialog: WaitDialog? = null
    fun showWaitDialog(title: String?) {
        if (waitDialog == null) {
            waitDialog = WaitDialog(mActivity)
        }
        waitDialog?.show(title!!, true, null)
    }

    fun dismissWaitDialog() {
        if (waitDialog?.isShowing() == true) {
            waitDialog!!.cancel()
        }
    }

    fun setFragmentBackResult(resultCode: Int, data: Bundle?) {
        val intent= Intent()
        data?.let { intent.putExtras(it) }
        mActivity.setResult(resultCode, intent)
    }

    fun setFragmentBackResult(resultCode: Int) {
        setFragmentBackResult(resultCode, null)
    }

    open fun onNewIntent(bundle: Bundle?) {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isAdded
                    && fragment is BaseDialogFragment && fragment.isVisible()) {
                fragment.onNewIntent(bundle)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isAdded
                    && fragment.isVisible && fragment.userVisibleHint) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    open fun show(manager: FragmentManager?) {
        try {
            super.show(manager!!, this::class.java.simpleName)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss()
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    fun <T : View> findViewById(resId: Int): T {
        var view: T? = null
        if (cacheView != null) {
            view = cacheView!!.findViewById<T>(resId)
        }
        if (view == null) {
            throw NullPointerException("no found view with ${resId.toString()} in " + this)
        }
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}