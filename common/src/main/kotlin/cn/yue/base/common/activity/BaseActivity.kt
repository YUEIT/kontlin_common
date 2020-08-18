package cn.yue.base.common.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import cn.yue.base.common.activity.rx.ILifecycleProvider
import cn.yue.base.common.activity.rx.RxLifecycleProvider
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.widget.dialog.HintDialog

abstract class BaseActivity : FragmentActivity() {

    private lateinit var lifecycleProvider: ILifecycleProvider<Lifecycle.Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleProvider = initLifecycleProvider()
        lifecycle.addObserver(lifecycleProvider)
        if (hasContentView()) {
            setContentView(getLayoutId())
        }
        if (intent != null && intent.extras != null) {
            initBundle(intent.extras)
        }
        initView()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    open fun initLifecycleProvider(): ILifecycleProvider<Lifecycle.Event> {
        return RxLifecycleProvider()
    }

    open fun hasContentView(): Boolean = true

    open fun initBundle(bundle: Bundle?) {}

    fun setSystemBar(isFillUpTop: Boolean, isDarkIcon: Boolean) {
        setSystemBar(isFillUpTop, isDarkIcon, Color.TRANSPARENT)
    }

    fun setSystemBar(isFillUpTop: Boolean, isDarkIcon: Boolean, bgColor: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        try {
            BarUtils.setStyle(this, isFillUpTop, isDarkIcon, bgColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
                    .setTitleStr("消息")
                    .setContentStr("当前应用无此权限，该功能暂时无法使用。如若需要，请单击确定按钮进行权限授权！")
                    .setLeftClickStr("取消")
                    .setRightClickStr("确定")
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
                    ToastUtils.showShortToast("获取" + RunTimePermissionUtil.getPermissionName(permissions[i]) + "权限失败~")
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
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        failDialog?.dismiss()
    }

}
