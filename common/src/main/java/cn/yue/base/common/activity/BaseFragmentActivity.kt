package cn.yue.base.common.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import cn.yue.base.common.R
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.dialog.HintDialog
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.support.RxFragmentActivity
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.base_activity_layout.*
import java.util.*

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseFragmentActivity : RxFragmentActivity(), ILifecycleProvider<ActivityEvent>{

    private lateinit var fragmentManager: FragmentManager
    private var currentFragment: BaseFragment? = null
    private lateinit var topBar: TopBar
    private var resultCode: Int = 0
    private var resultBundle: Bundle? = null
    private var permissionCallBack: PermissionCallBack? = null
    private var failDialog: HintDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setSystemBar(false, true, Color.WHITE)
        setContentView(getContentViewLayoutId())
        initView()
        replace(getFragment(), null, false)
    }

    protected open fun getContentViewLayoutId() : Int = R.layout.base_activity_layout

    private fun initView() {
        topBar = TopBar(this)
        topFL.addView(topBar)
        content.setBackgroundColor(Color.WHITE)
        fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener {
            currentFragment = getCurrentFragment()
            if (currentFragment != null && resultCode == Activity.RESULT_OK) {
                currentFragment!!.onFragmentResult(resultCode, resultBundle!!)
            }
            resultCode = Activity.RESULT_CANCELED
            resultBundle = null
        }
    }

    fun setSystemBar(isFillTop: Boolean, isDarkIcon: Boolean) {
        setSystemBar(isFillTop, isDarkIcon, Color.TRANSPARENT)
    }

    fun setSystemBar(isFillTop: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        try {
            BarUtils.setStyle(this, isFillTop, isDarkIcon, bgColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (isFillTop) {
            setFillUpTopLayout(isFillTop)
        }
    }

    fun setFillUpTopLayout(isFillTop: Boolean) {
        var systemBarPadding: Int
        var subject: Int
        if (isFillTop) {
            systemBarPadding = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) 0
                else Math.max(BarUtils.getStatusBarHeight(this), resources.getDimensionPixelOffset(R.dimen.q50))
            subject = 0
            getTopBar().setBgColor(Color.TRANSPARENT)
        } else {
            systemBarPadding = 0
            subject = R.id.topFL
        }
        val topBarLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        topBarLayoutParams.topMargin = systemBarPadding
        topFL!!.layoutParams = topBarLayoutParams
        val contentLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentLayoutParams.addRule(RelativeLayout.BELOW, subject)
        content!!.layoutParams = contentLayoutParams
    }

    abstract fun getFragment(): Fragment?

    fun getTopBar(): TopBar = topBar

    fun customTopBar(view: View) {
        topFL.removeAllViews()
        topFL.addView(view)
    }

    fun getCustomTopBar() : View = topFL.getChildAt(0)

    fun removeTopBar() {
        topFL.removeView(topBar)
    }

    fun setContentBackground(@ColorInt color: Int) {
        content.setBackgroundColor(color)
    }

    fun recreateFragment(fragmentName: String) {
        replace(getFragment(), null, false)
    }

    fun instantiate(mClass: Class<Fragment>, args: Bundle): Fragment {
        return Fragment.instantiate(this, mClass.simpleName, args)
    }

    fun replace(fragment: Fragment?, tag: String?, canBack: Boolean) {
        if (fragment == null) return
        var tag = tag
        val transaction = fragmentManager.beginTransaction()
        if (TextUtils.isEmpty(tag)) {
            tag = UUID.randomUUID().toString()
        }
        transaction.replace(R.id.content, fragment, tag)
        if (canBack) {
            transaction.addToBackStack(tag)
        }
        transaction.commitAllowingStateLoss()
    }

    fun getCurrentFragment(): BaseFragment? {
        val fragment = fragmentManager.findFragmentById(R.id.content)
        return if (fragment != null && fragment is BaseFragment) {
            fragment
        } else null
    }

    override fun onStop() {
        super.onStop()
        setExitAnim()
    }

    protected open fun setExitAnim() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onBackPressed() {
        currentFragment = getCurrentFragment()
        if (currentFragment != null && currentFragment!!.onFragmentBackPressed()) {
            return
        }
        superOnBackPressed()
    }

    fun superOnBackPressed() {
        if (fragmentManager.backStackEntryCount == 0 && resultCode != Activity.RESULT_CANCELED) {
            var data: Intent? = null
            if (resultBundle != null) {
                data = Intent()
                data.putExtras(resultBundle!!)
            }
            setResult(resultCode, data)

        }
        super.onBackPressed()
        setExitAnim()
    }

    fun setFragmentResult(resultCode: Int, resultBundle: Bundle?) {
        this.resultCode = resultCode
        this.resultBundle = resultBundle
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null || intent.extras == null) {
            return
        }
        val fragments = fragmentManager.fragments
        if (fragments != null && fragments.size > 0) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                    fragment.onNewIntent(intent.extras!!)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = fragmentManager.fragments
        if (fragments != null && fragments.size > 0) {
            for (fragment in fragments) {
                if (fragment != null && fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                    fragment.onFragmentResult(requestCode, data.extras!!)
                }
            }
        }
    }

    override fun <T> toBindLifecycle(): SingleTransformer<T, T> {
        return SingleTransformer {
            upstream ->
            upstream.compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    override fun <T> toBindLifecycle(e: ActivityEvent): SingleTransformer<T, T> {
        return SingleTransformer {
            upstream ->
            upstream.compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 权限请求
     * @param permissions
     * @param requestCode
     */
    fun requestPermission(permissions: Array<String>, requestCode: Int, permissionCallBack: PermissionCallBack) {
        RunTimePermissionUtil.requestPermissions(this, requestCode, permissionCallBack, *permissions)
    }

    fun setPermissionCallBack(permissionCallBack: PermissionCallBack) {
        this.permissionCallBack = permissionCallBack
    }

    fun showFailDialog() {
        if (failDialog == null) {
            failDialog = HintDialog.Builder(this)
                    .setTitleStr("消息")
                    .setContentStr("当前应用无此权限，该功能暂时无法使用。如若需要，请单击确定按钮进行权限授权！")
                    .setLeftClickStr("取消")
                    .setRightClickStr("确定")
                    .setOnRightClickListener { startSettings() }
                    .build()
        }
        failDialog!!.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RunTimePermissionUtil.REQUEST_CODE) {
            if (permissionCallBack != null) {
                for (i in grantResults.indices) {
                    if (verificationPermissions(grantResults)) {
                        permissionCallBack!!.requestSuccess(permissions[i])
                    } else {
                        permissionCallBack!!.requestFailed(permissions[i])
                    }
                }
            }
        }
    }

    private fun verificationPermissions(results: IntArray): Boolean {
        for (result in results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true

    }

    private fun startSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

}
