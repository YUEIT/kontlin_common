package cn.yue.base.common.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import cn.yue.base.common.R
import cn.yue.base.common.widget.TopBar
import com.trello.rxlifecycle2.android.FragmentEvent
import com.trello.rxlifecycle2.components.support.RxFragment
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseFragment : RxFragment(), View.OnTouchListener, ILifecycleProvider<FragmentEvent> {

    protected var cacheView: View? = null
    protected lateinit var mFragmentManager: FragmentManager
    protected lateinit var mActivity: BaseFragmentActivity
    protected var bundle: Bundle? = null
    protected lateinit var mInflater: LayoutInflater
    protected var mHandler = Handler()
    protected lateinit var topBar: TopBar
    protected var requestTag = UUID.randomUUID().toString()

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract fun getLayoutId(): Int


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (null == context || context !is BaseFragmentActivity) {
            throw RuntimeException("BaseFragment必须与BaseActivity配合使用")
        }
        mActivity = context
        mFragmentManager = childFragmentManager
        mInflater = LayoutInflater.from(mActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = arguments
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(this)
        if (!hasCache) {
            initView(savedInstanceState)
            initOther()
        }
    }

    protected open fun initOther() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cacheView == null || !needCache()) {//如果view没有被初始化或者不需要缓存的情况下，重新初始化控件
            topBar = mActivity.getTopBar()
            initToolBar(topBar)
            cacheView = if (getLayoutId() == 0) null else inflater.inflate(getLayoutId(), container, false)
            hasCache = false
        } else {
            hasCache = true
            val v = cacheView!!.parent as ViewGroup
            v?.removeView(cacheView)
        }
        return cacheView
    }

    /**
     * true 避免当前Fragment被repalce后回退回来重走oncreateview，导致重复初始化View和数据
     * @return
     */
    protected open fun needCache(): Boolean {
        return true
    }

    private var hasCache: Boolean = false

    protected abstract fun initView(savedInstanceState: Bundle?)

    protected open fun initToolBar(topBar: TopBar) {
        topBar.visibility = View.VISIBLE
        topBar.setLeftImage(R.drawable.app_icon_back)
        topBar.setLeftClickListener { finishAll() }
    }

    fun customTopBar(view: View) {
        mActivity.customTopBar(view)
    }

    fun hideTopBar() {
        mActivity.getTopBar().visibility = View.GONE
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return true
    }

    override fun <T> toBindLifecycle(): SingleTransformer<T, T> {
        return SingleTransformer { upstream ->
            upstream.compose(bindUntilEvent(FragmentEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun <T> toBindLifecycle(e: FragmentEvent): SingleTransformer<T, T> {
        return SingleTransformer { upstream ->
            upstream.compose(bindUntilEvent(e))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun onDetach() {
        super.onDetach()
        mHandler.removeCallbacksAndMessages(null)
    }

    fun newInstance(fragmentName: String, bundle: Bundle): BaseFragment {
        return Fragment.instantiate(activity, fragmentName, bundle) as BaseFragment
    }

    open fun onFragmentBackPressed(): Boolean {
        return false
    }

    @JvmOverloads
    fun setFragmentBackResult(resultCode: Int, data: Bundle? = null) {
        mActivity.setFragmentResult(resultCode, data)
    }

    fun onFragmentResult(resultCode: Int, data: Bundle) {

    }

    fun onNewIntent(bundle: Bundle) {
        val fragments = mFragmentManager.fragments
        if (fragments != null && fragments.size > 0) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                    fragment.onNewIntent(bundle)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fragments = mFragmentManager.fragments
        if (fragments != null && fragments.size > 0) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isAdded && fragment.isVisible && fragment.userVisibleHint) {
                    fragment.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }

    fun jumpFragment(fragment: BaseFragment, tag: String) {
        mActivity.replace(fragment, tag, true)
    }

    fun jumpFragment(fragment: BaseFragment) {
        mActivity.replace(fragment, javaClass.simpleName, true)
    }

    fun jumpFragmentNoBack(fragment: BaseFragment) {
        mActivity.replace(fragment, null, false)
    }


    //--------------------------------------------------------------------------------------------------------------
    fun finishFragment() {
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult() {
        setFragmentBackResult(Activity.RESULT_OK)
        mActivity.onBackPressed()
    }

    fun finishFragmentWithResult(data: Bundle) {
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

    fun finishAllWithResult(data: Bundle) {
        val intent = Intent()
        intent.putExtras(data)
        finishAllWithResult(Activity.RESULT_OK, intent)
    }

    //--------------------------------------------------------------------------------------------------------------

    fun <T : View> findViewById(resId: Int): T? {
        return if (cacheView == null) {
            null
        } else cacheView!!.findViewById<View>(resId) as T
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments = childFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    /** */
    /*********************    操作子fragment开始 */
    /** */

    fun replace(contaninViewId: Int, fragment: Fragment?) {
        if (null != fragment) {
            mFragmentManager.beginTransaction()
                    .replace(contaninViewId, fragment)
                    .commitAllowingStateLoss()
        }
    }

    fun replace(contaninViewId: Int, fragment: Fragment?, tag: String) {
        if (null != fragment) {
            mFragmentManager.beginTransaction()
                    .replace(contaninViewId, fragment, tag)
                    .commitAllowingStateLoss()
        }
    }


    fun removeFragment(fragment: Fragment) {
        mFragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
    }

    fun removeFragment(tag: String) {
        val fragment = mFragmentManager.findFragmentByTag(tag)
        if (fragment != null) {
            removeFragment(fragment)
        }
    }


    fun attachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isDetached) {
            mFragmentManager.beginTransaction().attach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun attachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return attachFragment(fragment)
    }

    fun isAddFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return fragment != null && fragment.isAdded
    }

    fun detachFragment(fragment: Fragment?): Boolean {
        if (fragment != null && fragment.isAdded) {
            mFragmentManager.beginTransaction().detach(fragment).commitAllowingStateLoss()
            return true
        }
        return false
    }

    fun detachFragment(tag: String): Boolean {
        val fragment = findFragmentByTag(tag)
        return detachFragment(fragment)
    }

    fun findFragmentByTag(tag: String): Fragment? {
        return mFragmentManager.findFragmentByTag(tag)
    }

    fun addFragment(containerId: Int, fragment: Fragment, tag: String) {
        mFragmentManager.beginTransaction()
                .add(containerId, fragment, tag)
                .commitAllowingStateLoss()
    }

    fun hideFragment(fragment: Fragment) {
        mFragmentManager.beginTransaction()
                .hide(fragment)
                .commitAllowingStateLoss()
    }

    fun showFragment(fragment: Fragment) {
        mFragmentManager.beginTransaction()
                .show(fragment)
                .commitAllowingStateLoss()
    }

    companion object {

        fun instance(context: Context, clazz: Class<out BaseFragment>, bundle: Bundle): BaseFragment {
            return Fragment.instantiate(context, clazz.name, bundle) as BaseFragment
        }
    }
}
