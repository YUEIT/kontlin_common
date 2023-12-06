package cn.yue.base.utils.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.yue.base.R
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.utils.code.getString
import cn.yue.base.utils.code.hasValue
import cn.yue.base.utils.debug.ToastUtils
import cn.yue.base.widget.dialog.HintDialog

/**
 * Description :
 * Created by yue on 2018/11/12
 */

object RunTimePermissionUtil {

    fun Context.requestPermissions(success: () -> Unit,
                                   failed: (permission: List<String>) -> Unit,
                                   vararg permissions: String) {
        if (this is BaseFragmentActivity) {
            if (this.checkPermissions(*permissions)) {
                success.invoke()
            } else {
                this.launchPermissions(success, {
                    val noPermission = this.shouldShowToSettingHint(*permissions)
                    if (noPermission.hasValue()) {
                        showFailDialog(this, getPermissionName(noPermission))
                    } else {
                        ToastUtils.showShortToast(R.string.app_permission_request_fail.getString()
                            .replace("%d", getPermissionName(noPermission)))
                    }
                    failed.invoke(it)
                }, *permissions)
            }
        }
    }

    /**
     * 请求权限且必须授权，否则禁止后提示，并finish
     */
    fun Context.requestMustPermissions(success: () -> Unit,
                                       failed: (permission: List<String>) -> Unit,
                                       vararg permissions: String) {
        if (this is BaseFragmentActivity) {
            if (this.checkPermissions(*permissions)) {
                success.invoke()
            } else {
                this.launchPermissions(success, {
                    val noPermission = this.shouldShowToSettingHint(*permissions)
                    if (noPermission.hasValue()) {
                        showFailDialog(this, getPermissionName(noPermission), false)
                    } else {
                        ToastUtils.showShortToast(R.string.app_permission_request_fail.getString()
                            .replace("%d", getPermissionName(noPermission)))
                    }
                    failed.invoke(it)
                }, *permissions)
            }
        }
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    fun Context.checkPermissions(vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 必须在launchPermissions方法调用之后执行，剥离掉其他情况
     * @return 权限被禁止且无法弹出授权框的情况下的权限
     */
    fun Activity.shouldShowToSettingHint(vararg permissions: String): String {
        var limitPermission = ""
        for (p in permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                limitPermission = p
                break
            }
        }
        return limitPermission
    }

    /**
     * 第一次请求权限时，ActivityCompat.shouldShowRequestPermissionRationale=false
     * 第一次请求权限被禁止，但未选择【不再提醒】后续请求ActivityCompat.shouldShowRequestPermissionRationale=true;
     * 允许某权限后，后续请求ActivityCompat.shouldShowRequestPermissionRationale=false;
     * 禁止权限并选中【禁止后不再询问】，后续请求ActivityCompat.shouldShowRequestPermissionRationale=false；
     * @return
     */
    fun Activity.shouldShowRequestPermissionRationaleStr(vararg permissions: String): String {
        var flag = ""
        for (p in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                flag = p
                break
            }
        }
        return flag
    }

    /**
     * 获取需要请求的权限
     * @param permissions
     * @return
     */
    fun getNeedRequestPermissions(context: Activity, vararg permissions: String): Array<String> {
        val permissionList = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                permissionList.add(permission)
            }
        }
        return permissionList.toTypedArray()
    }


    fun showFailDialog(activity: Activity, noPermission: String, canCancel: Boolean = true) {
        HintDialog.Builder(activity)
            .setContentStr(R.string.app_permission_no_granted_and_to_request.getString()
                .replace("%d", noPermission))
            .setCanCanceled(canCancel)
            .setLeftClickStr(R.string.app_cancel.getString())
            .setRightClickStr(R.string.app_confirm.getString())
            .setOnLeftClickListener {
                if (!canCancel) {
                    activity.finish()
                }
            }
            .setOnRightClickListener {
                activity.startSettings()
                if (!canCancel) {
                    activity.finish()
                }
            }
            .build()
            .show()
    }

    fun Activity.startSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        } catch (e : Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
        }
    }

    fun getPermissionName(permission: String): String {
        if (permissionMap.isEmpty()) {
            permissionMap[Manifest.permission.WRITE_EXTERNAL_STORAGE] = R.string.app_permission_write_external_storage.getString()
            permissionMap[Manifest.permission.READ_EXTERNAL_STORAGE] = R.string.app_permission_read_external_storage.getString()
            permissionMap[Manifest.permission.READ_PHONE_STATE] = R.string.app_permission_read_phone_state.getString()
            permissionMap[Manifest.permission.CAMERA] = R.string.app_permission_camera.getString()
            permissionMap[Manifest.permission.ACCESS_FINE_LOCATION] = R.string.app_permission_access_fine_location.getString()
        }
        val permissionName = permissionMap[permission]
        return if (!TextUtils.isEmpty(permissionName)) {
            permissionName!!
        } else ""
    }

    private val permissionMap = HashMap<String, String>()
}


