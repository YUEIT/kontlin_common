package cn.yue.base.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.R
import cn.yue.base.activity.rx.ILifecycleProvider
import cn.yue.base.activity.rx.RxLifecycleProvider
import cn.yue.base.utils.app.BarUtils
import cn.yue.base.utils.app.RunTimePermissionUtil
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.widget.TopBar
import cn.yue.base.widget.dialog.HintDialog
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
        initLifecycle(RxLifecycleProvider())
        initView()
    }

    open fun initLifecycle(provider: ILifecycleProvider<Lifecycle.Event>) {
        lifecycleProvider = provider
        lifecycle.addObserver(lifecycleProvider)
    }

    fun getLifecycleProvider(): ILifecycleProvider<Lifecycle.Event>? {
        return lifecycleProvider
    }

    open fun getContentViewLayoutId(): Int = R.layout.activity_base_layout

    open fun getFragment(): Fragment? = null

    open fun initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setStatusBar()
        setContentView(getContentViewLayoutId())
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
        replace(getFragment(), null, false)
    }

    fun setStatusBar(isDarkIcon: Boolean = true, bgColor: Int = Color.WHITE) {
        BarUtils.setStyle(this, true, isDarkIcon, bgColor)
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
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) {
        val noPermission = arrayListOf<String>()
        it.forEach { entry ->
            if (!entry.value) {
                ToastUtils.showShortToast(R.string.app_permission_request_fail.getString()
                    .replace("%d", RunTimePermissionUtil.getPermissionName(entry.key)))
                noPermission.add(entry.key)
            }
        }
        if (noPermission.isEmpty()) {
            permissionSuccess?.invoke()
        } else {
            permissionFailed?.invoke(noPermission)
        }
    }

    fun launchPermissions(success: () -> Unit,
                          failed: (permission: List<String>) -> Unit,
                          vararg permissions: String) {
        permissionSuccess = success
        permissionFailed = failed
        permissionLauncher.launch(permissions as Array<String>)
    }
  
    private var permissionSuccess: (() -> Unit)? = null
    private var permissionFailed: ((permission: List<String>) -> Unit)? = null

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