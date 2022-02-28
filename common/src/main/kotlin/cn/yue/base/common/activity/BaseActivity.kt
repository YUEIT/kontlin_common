package cn.yue.base.common.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.R
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.activity.rx.RxLifecycleProvider
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.code.getString
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.widget.dialog.HintDialog

abstract class BaseActivity : ComponentActivity() {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleProvider = initLifecycleProvider()
        lifecycle.addObserver(lifecycleProvider)
        if (hasContentView()) {
            setStatusBar()
            setContentView(getLayoutId())
        }
        intent.extras?.apply {
            initBundle(this)
        }
        initView()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    open fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return RxLifecycleProvider()
    }

    open fun hasContentView(): Boolean = true

    open fun initBundle(bundle: Bundle) {}

    fun setStatusBar(isFullScreen: Boolean = false, isDarkIcon: Boolean = true, bgColor: Int = Color.WHITE) {
        BarUtils.setStyle(this, isFullScreen, isDarkIcon, bgColor)
    }

    /**
     * 权限请求
     */
    fun requestPermission(permission: String,
                          success: (permission: String) -> Unit,
                          failed: (permission: String) -> Unit) {
        RunTimePermissionUtil.requestPermissions(this, success, failed, permission)
    }

    /**
     * 权限请求
     */
    fun requestPermission(permissions: Array<String>,
                          success: (permission: String) -> Unit,
                          failed: (permission: String) -> Unit) {
        RunTimePermissionUtil.requestPermissions(this, success, failed, *permissions)
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
        failDialog?.show()
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
                    permissionFailed?.invoke(permissions[i])
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
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e : Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        failDialog?.dismiss()
    }

}
