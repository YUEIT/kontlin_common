package cn.yue.base.common.activity

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import com.trello.rxlifecycle2.components.RxActivity

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseActivity : RxActivity() {

    protected abstract val layoutId: Int

    private var permissionCallBack: PermissionCallBack? = null

    private var failDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        if (intent != null && intent.extras != null) {
            initBundle(intent.extras)
        }
        initView()
    }

    protected abstract fun initView()

    protected fun initBundle(bundle: Bundle?) {}

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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
                    .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which -> return@OnClickListener })
                    .setPositiveButton("确定") { dialog, which -> startSettings() }.create()
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
