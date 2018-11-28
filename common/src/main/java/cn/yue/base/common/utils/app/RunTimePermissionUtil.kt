package cn.yue.base.common.utils.app

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import cn.yue.base.common.activity.BaseActivity
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.activity.PermissionCallBack
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/12
 */
object RunTimePermissionUtil {
    val REQUEST_CODE = 100

    fun requestPermissions(context: BaseFragmentActivity, requestCode: Int, permissionCallBack: PermissionCallBack?, vararg permissions: String) {
        //检查权限是否授权
        context.setPermissionCallBack(permissionCallBack!!)
        if (shouldShowRequestPermissionRationale(context, *permissions)) {
            context.showFailDialog()
        }
        if (RunTimePermissionUtil.checkPermissions(context, *permissions)) {
            if (permissionCallBack != null) {
                for (permission in permissions) {
                    permissionCallBack.requestSuccess(permission)
                }
            }
        } else {
            ActivityCompat.requestPermissions(context, getNeedRequestPermissions(context, *permissions), requestCode)
        }
    }

    fun requestPermissions(context: BaseActivity, requestCode: Int, permissionCallBack: PermissionCallBack?, vararg permissions: String) {
        //检查权限是否授权
        context.setPermissionCallBack(permissionCallBack!!)
        if (shouldShowRequestPermissionRationale(context, *permissions)) {
            context.showFailDialog()
        }
        if (RunTimePermissionUtil.checkPermissions(context, *permissions)) {
            if (permissionCallBack != null) {
                for (permission in permissions) {
                    permissionCallBack.requestSuccess(permission)
                }
            }
        } else {
            ActivityCompat.requestPermissions(context, getNeedRequestPermissions(context, *permissions), requestCode)
        }
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    fun checkPermissions(context: Activity, vararg permissions: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
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
    fun shouldShowRequestPermissionRationale(context: Activity, vararg permissions: String): Boolean {
        var flag = false
        for (p in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, p)) {
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
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                permissionList.add(permission)
            }
        }
        return permissionList.toTypedArray()
    }
}


