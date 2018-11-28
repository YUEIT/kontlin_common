package cn.yue.base.common.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Window
import cn.yue.base.common.R
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import com.readystatesoftware.systembartint.SystemBarTintManager
import com.trello.rxlifecycle2.components.support.RxFragmentActivity
import kotlinx.android.synthetic.main.base_activity_layout.*
import java.util.*

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseFragmentActivity : RxFragmentActivity() {

    private lateinit var fragmentManager: FragmentManager
    private var currentFragment: BaseFragment? = null
    private var resultCode: Int = 0
    private var resultBundle: Bundle? = null
    private var permissionCallBack: PermissionCallBack? = null
    private var failDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setStatusBarTintColor(Color.TRANSPARENT)
        }
        fragmentManager = supportFragmentManager
        fragmentManager.addOnBackStackChangedListener {
            currentFragment = getCurrentFragment()
            if (currentFragment != null && resultCode == Activity.RESULT_OK) {
                currentFragment!!.onFragmentResult(resultCode, resultBundle!!)
            }
            resultCode = Activity.RESULT_CANCELED
            resultBundle = null
        }
        setContentView(R.layout.base_activity_layout)
        replace(getFragment(), null, false)
    }

    abstract fun getFragment(): Fragment?

    fun getToolBar(): Toolbar = topBar

    fun instantiate(mClass: Class<Fragment>, args: Bundle): Fragment {
        return Fragment.instantiate(this, mClass.simpleName, args)
    }

    fun replace(fragment: Fragment?, tag: String?, canBack: Boolean) {
        if (fragment == null) return
        var tag = tag
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out, R.anim.left_in, R.anim.right_out)
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

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onStop() {
        super.onStop()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }

    override fun onDestroy() {
        super.onDestroy()
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
        this.overridePendingTransition(R.anim.left_in, R.anim.right_out)
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
            failDialog = AlertDialog.Builder(this)
                    .setTitle("消息")
                    .setMessage("当前应用无此权限，该功能暂时无法使用。如若需要，请单击确定按钮进行权限授权！")
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("确定") { _, _ -> startSettings() }
                    .create()
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
