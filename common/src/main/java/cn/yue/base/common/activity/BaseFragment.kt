package cn.yue.base.common.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.yue.base.common.R
import com.trello.rxlifecycle2.components.support.RxFragment
import kotlinx.android.synthetic.main.base_activity_layout.*
import java.util.*

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseFragment : RxFragment() {

    protected var cacheView: View? = null
    protected lateinit var mFragmentManager: FragmentManager
    protected lateinit var mActivity: BaseFragmentActivity
    protected var bundle: Bundle? = null
    protected lateinit var mInflater: LayoutInflater
    protected var mHandler = Handler()
    protected lateinit var topBar: Toolbar
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
        initView(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (cacheView == null || !needCache()) {//如果view没有被初始化或者不需要缓存的情况下，重新初始化控件
            topBar = mActivity.topBar
            initToolBar(topBar)
            cacheView = if (getLayoutId() == 0) null else inflater.inflate(getLayoutId(), container, false)
        } else {
            val v = cacheView!!.parent as ViewGroup
            v?.removeView(cacheView)
        }
        return cacheView
    }

    /**
     * true 避免当前Fragment被repalce后回退回来重走oncreateview，导致重复初始化View和数据
     * @return
     */
    protected fun needCache(): Boolean {
        return true
    }

    protected abstract fun initView(savedInstanceState: Bundle?)

    fun initToolBar(topBar: Toolbar) {
        if (needToolBar()) {
            topBar.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        mHandler.removeCallbacksAndMessages(null)
    }

    fun newInstance(fragmentName: String, bundle: Bundle): BaseFragment {
        return Fragment.instantiate(activity, fragmentName, bundle) as BaseFragment
    }

    fun onFragmentBackPressed(): Boolean {
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


    /**
     * 是否需要持有公共的toolbar，只有当fragment作为一个完整的页面时才需要
     * 暂未处理，默认处理方式：不重写resetToolBar（）
     *
     * @return
     */
    protected fun needToolBar(): Boolean {
        return true
    }

    /***********************
     * 对基础view的一些公共操作
     */
    protected fun setTextColorRid(t: TextView?, rId: Int) {
        if (null != t) {
            if (activity != null) {
                t.setTextColor(activity!!.resources.getColor(rId))
            }
        }
    }

    protected fun setTextColor(t: TextView?, color: Int) {
        t?.setTextColor(color)
    }

    protected fun setTextView(t: TextView?, s: CharSequence?) {
        if (null != t && null != s) {
            t.text = s
        } else if (null != t) {
            t.text = ""
        }
    }

    protected fun setImageResource(img: ImageView?, resId: Int) {
        img?.setImageResource(resId)
    }

    protected fun setBackground(v: View?, resId: Int) {
        v?.setBackgroundResource(resId)
    }

    protected fun setVisible(v: View?, visible: Boolean) {
        if (null != v) {
            if (visible) {
                v.visibility = View.VISIBLE
            } else {
                v.visibility = View.GONE
            }
        }
    }

    protected fun setOnClickListener(v: View?, l: View.OnClickListener) {
        v?.setOnClickListener(l)
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
