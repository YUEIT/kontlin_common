package cn.yue.base.common.activity

import android.app.Activity
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
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.R
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
        if (!hasCache) {
            initView(savedInstanceState)
            initOther()
        }
    }

    open fun initOther() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cacheView == null || !needCache()) {
            cacheView = if (getLayoutId() == 0) {
                null
            } else {
                inflater.inflate(getLayoutId(), container, false)
            }
            hasCache = false
        } else {
            hasCache = true
            val v = cacheView?.parent
            if (v != null && v is ViewGroup) {
                v.removeView(cacheView)
            }
        }
        return cacheView
    }

    /**
     * true 避免当前Fragment被replace后回退回来重走onCreateView，导致重复初始化View和数据
     */
    open fun needCache(): Boolean {
        return true
    }

    /**
     * 是否有缓存，避免重新走initView方法
     */
    private var hasCache = false

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val isShow = showsDialog
        showsDialog = false
        super.onActivityCreated(savedInstanceState)
        showsDialog = isShow
        if (dialog == null) {
            return
        }
        dialog?.apply {
            view?.let {
                check(it.parent == null) {
                    "DialogFragment can not be attached to a container view"
                }
                setContentView(it)
            }
            activity?.let {
                setOwnerActivity(it)
            }
            setCancelable(isCancelable)
            // 使用静态内部类取代，防止message中持有fragment的引用而造成内存泄漏
            setOnCancelListener(onCancelListener)
            setOnDismissListener(onDismissListener)
            if (savedInstanceState != null) {
                val dialogState = savedInstanceState.getBundle("android:savedDialogState")
                if (dialogState != null) {
                    onRestoreInstanceState(dialogState)
                }
            }
        }
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
        if (waitDialog != null && waitDialog!!.isShowing()) {
            waitDialog!!.cancel()
        }
    }

    fun setFragmentBackResult(resultCode: Int, data: Bundle?) {
        var intent: Intent? = null
        if (intent != null) {
            intent = Intent()
            intent.putExtras(data!!)
        }
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

    //--------------------------------------------------------------------------------------------------------------
    fun finishFragment() {
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult() {
        setFragmentBackResult(Activity.RESULT_OK)
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult(data: Bundle?) {
        setFragmentBackResult(Activity.RESULT_OK, data)
        mActivity.onBackPressed()
    }

    fun finishAll() {
        mActivity.supportFinishAfterTransition()
        mActivity.overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    @JvmOverloads
    fun finishAllWithResult(resultCode: Int, data: Intent? = null) {
        mActivity.setResult(resultCode, data)
        finishAll()
    }

    fun finishAllWithResult(data: Bundle?) {
        val intent = Intent()
        intent.putExtras(data!!)
        finishAllWithResult(Activity.RESULT_OK, intent)
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