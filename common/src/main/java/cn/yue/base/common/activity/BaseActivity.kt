package cn.yue.base.common.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import cn.yue.base.common.utils.app.BarUtils
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.debug.ToastUtils
import cn.yue.base.common.widget.dialog.HintDialog
import com.trello.rxlifecycle2.android.ActivityEvent
import com.trello.rxlifecycle2.components.RxActivity
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 介绍：
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/20.
 */

abstract class BaseActivity : RxActivity(), ILifecycleProvider<ActivityEvent> {

    protected abstract val layoutId: Int

    private var permissionCallBack: PermissionCallBack? = null

    private var failDialog: HintDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasContentView()) {
            setContentView(layoutId)
        }
        if (intent != null && intent.extras != null) {
            initBundle(intent.extras)
        }
        initView()
    }

    protected fun hasContentView() : Boolean  = true

    protected abstract fun initView()

    protected fun initBundle(bundle: Bundle?) {}

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
            upstream.compose(bindUntilEvent(e))
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

    fun requestPermission(permission: String, permissionCallBack: PermissionCallBack) {
        RunTimePermissionUtil.requestPermissions(this, RunTimePermissionUtil.REQUEST_CODE, permissionCallBack, permission)
    }

    fun requestPermission(permissions: Array<String>, permissionCallBack: PermissionCallBack) {
        RunTimePermissionUtil.requestPermissions(this, RunTimePermissionUtil.REQUEST_CODE, permissionCallBack, *permissions)
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
                        ToastUtils.showShortToast("获取" + RunTimePermissionUtil.getPermissionName(permissions[i]) + "权限失败~")
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

    override fun onDestroy() {
        super.onDestroy()
        failDialog?.dismiss()
    }
}
