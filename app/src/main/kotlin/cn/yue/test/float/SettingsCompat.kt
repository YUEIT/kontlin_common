package cn.yue.test.float

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.lang.reflect.Method


/**
 * Description :
 * Created by yue on 2022/7/15
 */

object SettingsCompat {
    private const val OP_WRITE_SETTINGS = 23
    private const val OP_SYSTEM_ALERT_WINDOW = 24

    /**
     * 检查悬浮窗权限  当没有权限，跳转到权限设置界面
     *
     * @param context          上下文
     * @param isShowDialog     没有权限，是否弹框提示跳转到权限设置界面
     * @param isShowPermission 是否跳转权限开启界面
     * @return true 有权限   false 没有权限（跳转权限界面、权限失败 提示用户手动设置权限）
     * @by 腾讯云直播 悬浮框判断逻辑
     */
    fun canDrawOverlays(
        context: Context,
        isShowDialog: Boolean = true,
        isShowPermission: Boolean = false
    ): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                if (isShowDialog) {
                    //去授权
                    manageDrawOverlays(context)
                } else if (isShowPermission) {
                    manageDrawOverlays(context)
                }
                return false
            }
            true
        } else {
            if (checkOp(context, OP_SYSTEM_ALERT_WINDOW)) {
                true
            } else {
                if (isShowPermission) {
                    startFloatWindowPermissionErrorToast(context)
                }
                false
            }
        }
    }

    /**
     * 打开 悬浮窗 授权界面
     *
     * @param context
     */
    fun manageDrawOverlays(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:" + context.getPackageName())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                startFloatWindowPermissionErrorToast(context)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!manageDrawOverlaysForRom(context)) {
                startFloatWindowPermissionErrorToast(context)
            }
        }
    }

    /**
     * 权限设置 失败提示。
     *
     * @param context
     */
    fun startFloatWindowPermissionErrorToast(context: Context?) {
        if (context != null) Toast.makeText(context, "进入设置页面失败,请手动开启悬浮窗权限", Toast.LENGTH_SHORT)
            .show()
    }

    private fun manageDrawOverlaysForRom(context: Context): Boolean {
        if (RomUtil.isMiui()) {
            return manageDrawOverlaysForMiui(context)
        }
        if (RomUtil.isEmui()) {
            return manageDrawOverlaysForEmui(context)
        }
        if (RomUtil.isFlyme()) {
            return manageDrawOverlaysForFlyme(context)
        }
        if (RomUtil.isOppo()) {
            return manageDrawOverlaysForOppo(context)
        }
        if (RomUtil.isVivo()) {
            return manageDrawOverlaysForVivo(context)
        }
        if (RomUtil.isQiku()) {
            return manageDrawOverlaysForQihu(context)
        }
        return if (RomUtil.isSmartisan()) {
            manageDrawOverlaysForSmartisan(context)
        } else false
    }

    private fun checkOp(context: Context, op: Int): Boolean {
        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val method: Method = AppOpsManager::class.java.getDeclaredMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            return AppOpsManager.MODE_ALLOWED == method.invoke(
                manager,
                op,
                Binder.getCallingUid(),
                context.getPackageName()
            ) as Int
        } catch (e: Exception) {
        }
        return false
    }

    // 可设置Android 4.3/4.4的授权状态
    private fun setMode(context: Context, op: Int, allowed: Boolean): Boolean {
        val manager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val method: Method = AppOpsManager::class.java.getDeclaredMethod(
                "setMode",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
                Int::class.javaPrimitiveType
            )
            method.invoke(
                manager,
                op,
                Binder.getCallingUid(),
                context.getPackageName(),
                if (allowed) AppOpsManager.MODE_ALLOWED else AppOpsManager.MODE_IGNORED
            )
            return true
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * 跳转界面
     *
     * @param context
     * @param intent
     * @return
     */
    private fun startSafely(context: Context, intent: Intent): Boolean {
        var resolveInfos: List<ResolveInfo?>? = null
        try {
            resolveInfos = context.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfos.isNotEmpty()) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // 小米
    private fun manageDrawOverlaysForMiui(context: Context): Boolean {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.getPackageName())
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
        )
        if (startSafely(context, intent)) {
            return true
        }
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        if (startSafely(context, intent)) {
            return true
        }
        // miui v5 的支持的android版本最高 4.x
        // http://www.romzj.com/list/search?keyword=MIUI%20V5#search_result
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val intent1 = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent1.data = Uri.fromParts("package", context.getPackageName(), null)
            return startSafely(context, intent1)
        }
        return false
    }

    private const val HUAWEI_PACKAGE = "com.huawei.systemmanager"

    // 华为
    private fun manageDrawOverlaysForEmui(context: Context): Boolean {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setClassName(
                HUAWEI_PACKAGE,
                "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"
            )
            if (startSafely(context, intent)) {
                return true
            }
        }
        // Huawei Honor P6|4.4.4|3.0
        intent.setClassName(
            HUAWEI_PACKAGE,
            "com.huawei.notificationmanager.ui.NotificationManagmentActivity"
        )
        intent.putExtra("showTabsNumber", 1)
        if (startSafely(context, intent)) {
            return true
        }
        intent.setClassName(HUAWEI_PACKAGE, "com.huawei.permissionmanager.ui.MainActivity")
        return if (startSafely(context, intent)) {
            true
        } else false
    }

    // VIVO
    private fun manageDrawOverlaysForVivo(context: Context): Boolean {
        // 不支持直接到达悬浮窗设置页，只能到 i管家 首页
        val intent = Intent("com.iqoo.secure")
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.MainActivity")
        // com.iqoo.secure.ui.phoneoptimize.SoftwareManagerActivity
        // com.iqoo.secure.ui.phoneoptimize.FloatWindowManager
        return startSafely(context, intent)
    }

    // OPPO
    private fun manageDrawOverlaysForOppo(context: Context): Boolean {
        val intent = Intent()
        intent.putExtra("packageName", context.getPackageName())
        // OPPO A53|5.1.1|2.1
        intent.action = "com.oppo.safe"
        intent.setClassName(
            "com.oppo.safe",
            "com.oppo.safe.permission.floatwindow.FloatWindowListActivity"
        )
        if (startSafely(context, intent)) {
            return true
        }
        // OPPO R7s|4.4.4|2.1
        intent.action = "com.color.safecenter"
        intent.setClassName(
            "com.color.safecenter",
            "com.color.safecenter.permission.floatwindow.FloatWindowListActivity"
        )
        if (startSafely(context, intent)) {
            return true
        }
        intent.action = "com.coloros.safecenter"
        intent.setClassName(
            "com.coloros.safecenter",
            "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity"
        )
        return startSafely(context, intent)
    }

    // 魅族
    private fun manageDrawOverlaysForFlyme(context: Context): Boolean {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        intent.putExtra("packageName", context.getPackageName())
        return startSafely(context, intent)
    }

    // 360
    private fun manageDrawOverlaysForQihu(context: Context): Boolean {
        val intent = Intent()
        intent.setClassName(
            "com.android.settings",
            "com.android.settings.Settings\$OverlaySettingsActivity"
        )
        if (startSafely(context, intent)) {
            return true
        }
        intent.setClassName(
            "com.qihoo360.mobilesafe",
            "com.qihoo360.mobilesafe.ui.index.AppEnterActivity"
        )
        return startSafely(context, intent)
    }

    // 锤子
    private fun manageDrawOverlaysForSmartisan(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return false
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 锤子 坚果|5.1.1|2.5.3
            val intent = Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS_NEW")
            intent.setClassName(
                "com.smartisanos.security",
                "com.smartisanos.security.SwitchedPermissions"
            )
            intent.putExtra("index", 17) // 不同版本会不一样
            startSafely(context, intent)
        } else {
            // 锤子 坚果|4.4.4|2.1.2
            val intent = Intent("com.smartisanos.security.action.SWITCHED_PERMISSIONS")
            intent.setClassName(
                "com.smartisanos.security",
                "com.smartisanos.security.SwitchedPermissions"
            )
            intent.putExtra("permission", arrayOf<String>(Manifest.permission.SYSTEM_ALERT_WINDOW))

            //        Intent intent = new Intent("com.smartisanos.security.action.MAIN");
            //        intent.setClassName("com.smartisanos.security", "com.smartisanos.security.MainActivity");
            startSafely(context, intent)
        }
    }
    
    /**
     * 获取悬浮窗权限状态
     *
     * @param context
     * @return 1或其他是没有打开，0是打开，该状态的定义和[android.app.AppOpsManager.MODE_ALLOWED]，MODE_IGNORED等值差不多，自行查阅源码
     */
    fun getFloatPermissionStatus(context: Context?): Int {
        requireNotNull(context) { "context is null" }
        val packageName = context.packageName
        val uri = Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(packageName)
        val cursor: Cursor? = context
            .contentResolver
            .query(uri, null, selection, selectionArgs, null)
        return if (cursor != null) {
            cursor.getColumnNames()
            if (cursor.moveToFirst()) {
                val currentmode: Int = cursor.getInt(cursor.getColumnIndex("currentlmode"))
                cursor.close()
                currentmode
            } else {
                cursor.close()
                getFloatPermissionStatus2(context)
            }
        } else {
            getFloatPermissionStatus2(context)
        }
    }
    
    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private fun getFloatPermissionStatus2(context: Context): Int {
        val packageName = context.packageName
        val uri2 =
            Uri.parse("content://com.vivo.permissionmanager.provider.permission/float_window_apps")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(packageName)
        val cursor = context
            .contentResolver
            .query(uri2, null, selection, selectionArgs, null)
        return if (cursor != null) {
            if (cursor.moveToFirst()) {
                val currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"))
                cursor.close()
                currentmode
            } else {
                cursor.close()
                1
            }
        } else 1
    }
}