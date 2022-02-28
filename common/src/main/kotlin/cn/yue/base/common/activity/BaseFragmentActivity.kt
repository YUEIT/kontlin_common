package cn.yue.base.common.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.R
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.activity.rx.RxLifecycleProvider
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.app.RunTimePermissionUtil.requestPermissions
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.widget.TopBar
import cn.yue.base.common.widget.dialog.HintDialog
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class BaseFragmentActivity : FragmentActivity() {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>
    private var topBar: TopBar? = null
    private var topFL: FrameLayout? = null
    private var content: FrameLayout? = null

    private var resultCode = 0
    private var resultBundle: Bundle? = null
    private var mCurrentFragment: BaseFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleProvider = initLifecycleProvider()
        lifecycle.addObserver(lifecycleProvider)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStatusBar()
        setContentView(getContentViewLayoutId())
        initView()
        replace(getFragment(), null, false)
    }

    open fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return RxLifecycleProvider()
    }

    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event>? {
        return lifecycleProvider
    }

    open fun getContentViewLayoutId(): Int = R.layout.activity_base_layout

    open fun getFragment(): Fragment? = null

    private fun initView() {
        topFL = findViewById(R.id.topBar)
        topFL?.addView(TopBar(this).also { topBar = it })
        content = findViewById(R.id.content)
        content?.setBackgroundColor(Color.WHITE)
        supportFragmentManager.addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener {
            mCurrentFragment = getNowFragment()
            if (mCurrentFragment != null && resultCode == Activity.RESULT_OK) {
                mCurrentFragment?.onFragmentResult(resultCode, resultBundle)
            }
            resultCode = Activity.RESULT_CANCELED
            resultBundle = null
        })
    }

    fun setStatusBar(isFullUpTop: Boolean = false, isDarkIcon: Boolean = true, bgColor: Int = Color.WHITE) {
        BarUtils.setStyle(this, true, isDarkIcon, bgColor)
        setFullUpTopLayout(isFullUpTop)
    }

    private fun setFullUpTopLayout(isFullUpTop: Boolean) {
        if (topBar == null) {
            return
        }
        var subject: Int = R.id.topBar
        if (isFullUpTop) {
            subject = 0
            topBar?.setBgColor(Color.TRANSPARENT)
        }
        val topBarLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        topFL?.layoutParams = topBarLayoutParams
        val contentLayoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentLayoutParams.addRule(RelativeLayout.BELOW, subject)
        content?.layoutParams = contentLayoutParams
    }

    fun getTopBar(): TopBar {
        return topBar ?: TopBar(this)
    }

    fun customTopBar(view: View?) {
        topFL?.removeAllViews()
        topFL?.addView(view)
    }

    fun customTopBar(): View? {
        return topFL?.getChildAt(0)
    }

    fun removeTopBar() {
        topFL?.removeView(topBar)
    }

    fun setContentBackground(@ColorInt color: Int) {
        content?.setBackgroundColor(color)
    }

    fun recreateFragment(fragmentName: String?) {
        replace(getFragment(), null, false)
    }

    fun instantiate(mClass: Class<*>, args: Bundle?): Fragment {
        return Fragment.instantiate(this, mClass.simpleName, args)
    }

    fun replace(fragment: Fragment?, tag: String?, canBack: Boolean) {
        var mTag = tag
        if (null == fragment) {
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        //        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out);
        if (TextUtils.isEmpty(mTag)) {
            mTag = UUID.randomUUID().toString()
        }
        transaction.replace(R.id.content, fragment, mTag)
        if (canBack) {
            transaction.addToBackStack(mTag)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getNowFragment(): BaseFragment? {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        return if (fragment != null && fragment is BaseFragment) {
            fragment
        } else null
    }

    fun getCurrentFragment(): BaseFragment? {
        return mCurrentFragment
    }

    fun setCurrentFragment(fragment: BaseFragment) {
        this.mCurrentFragment = fragment
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        setExitAnim()
    }

    open fun setExitAnim() {
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        mCurrentFragment = getNowFragment()
        if (mCurrentFragment != null && mCurrentFragment!!.onFragmentBackPressed()) {
            return
        }
        superOnBackPressed()
    }

    private fun superOnBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0 && resultCode != Activity.RESULT_CANCELED) {
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
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment: Fragment in fragments) {
            if (fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                fragment.onNewIntent(intent.extras!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments: List<Fragment> = supportFragmentManager.fragments
        for (fragment: Fragment in fragments) {
            if (fragment.isAdded && fragment is BaseFragment && fragment.isVisible) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    /**
     * 权限请求
     * @param permissions
     */
    fun requestPermission(permissions: Array<String>,
                          success: (permission: String) -> Unit,
                          failed: (permission: String) -> Unit) {
        requestPermissions(this, success, failed, *permissions)
    }

    private var permissionSuccess: ((permission: String) -> Unit)? = null
    private var permissionFailed: ((permission: String) -> Unit)? = null

    fun setPermissionCallBack(success: (permission: String) -> Unit,
                              failed: (permission: String) -> Unit) {
        this.permissionSuccess = success
        this.permissionFailed = failed
    }

    private var failDialog: HintDialog? = null
    fun showFailDialog() {
        if (failDialog == null) {
            failDialog = HintDialog.Builder(this)
                .setTitleStr(R.string.app_message.getString())
                .setContentStr(R.string.app_permission_no_granted_and_to_request.getString())
                .setLeftClickStr(R.string.app_cancel.getString())
                .setRightClickStr(R.string.app_confirm.getString())
                .setOnRightClickListener { startSettings() }
                .build()
        }
        failDialog!!.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RunTimePermissionUtil.requestCode) {
            for (i in grantResults.indices) {
                if (verificationPermissions(grantResults)) {
                    permissionSuccess?.invoke(permissions[i])
                } else {
                    ToastUtils.showShortToast(String.format(R.string.app_permission_request_fail.toString(),
                        RunTimePermissionUtil.getPermissionName(permissions[i])))
                    permissionFailed?.invoke((permissions[i]))
                }
            }
        }
    }

    private fun verificationPermissions(results: IntArray): Boolean {
        for (result: Int in results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun startSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e : Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }
}