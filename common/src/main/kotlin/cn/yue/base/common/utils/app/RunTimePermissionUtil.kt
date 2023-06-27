package cn.yue.base.common.utils.app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.utils.code.getString

/**
 * Description :
 * Created by yue on 2018/11/12
 */

object RunTimePermissionUtil {
    
    fun Context.requestPermissions(success: () -> Unit,
                                  failed: (permission: List<String>) -> Unit,
                                  vararg permissions: String) {
        if (this is BaseFragmentActivity) {
            if (this.shouldShowRequestPermissionRationale(*permissions)) {
                this.showFailDialog()
            }
            if (this.checkPermissions(*permissions)) {
                success.invoke()
            } else {
                this.launchPermissions(success, failed, *permissions)
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
     * 权限是否被拒绝过
     * @param context
     * @param permissions
     * @return
     */
    fun Activity.shouldShowRequestPermissionRationale(vararg permissions: String): Boolean {
        var flag = false
        for (p in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                flag = true
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


