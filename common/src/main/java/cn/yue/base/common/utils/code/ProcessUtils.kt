package cn.yue.base.common.utils.code

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.constant.StringUtils
import cn.yue.base.common.utils.debug.LogUtils
import java.util.*

/**
 * 介绍：进程相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ProcessUtils {

    /**
     * 获取前台线程包名
     *
     * 当不是查看当前App，且SDK大于21时，
     * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
     *
     * @return 前台应用包名
     */
    // 有"有权查看使用权限的应用"选项
    @JvmStatic
    fun getForegroundProcessName(): String? {
        val manager = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        if (infos != null && infos.size != 0) {
            for (info in infos) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return info.processName
                }
            }
        }
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            val packageManager = Utils.getContext().packageManager
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            println(list)
            if (list.size > 0) {
                try {
                    val info = packageManager.getApplicationInfo(Utils.getContext().packageName, 0)
                    val aom = Utils.getContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                        Utils.getContext().startActivity(intent)
                    }
                    if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) != AppOpsManager.MODE_ALLOWED) {
                        LogUtils.d("getForegroundApp", "没有打开\"有权查看使用权限的应用\"选项")
                        return null
                    }
                    val usageStatsManager = Utils.getContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    val usageStatses = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime)
                    if (usageStatses == null || usageStatses.isEmpty()) return null
                    var recentStats: UsageStats? = null
                    for (usageStats in usageStatses) {
                        if (recentStats == null || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                            recentStats = usageStats
                        }
                    }
                    return if (recentStats == null) null else recentStats.packageName
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

            } else {
                LogUtils.d("getForegroundApp", "无\"有权查看使用权限的应用\"选项")
            }
        }
        return null
    }

    /**
     * 获取后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @return 后台服务进程
     */
    @JvmStatic
    fun getAllBackgroundProcesses(): Set<String> {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = am.runningAppProcesses
        val set = HashSet<String>()
        for (info in infos) {
            Collections.addAll(set, *info.pkgList)
        }
        return set
    }

    /**
     * 杀死所有的后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @return 被暂时杀死的服务集合
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun killAllBackgroundProcesses(): Set<String> {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var infos: List<ActivityManager.RunningAppProcessInfo> = am.runningAppProcesses
        val set = HashSet<String>()
        for (info in infos) {
            for (pkg in info.pkgList) {
                am.killBackgroundProcesses(pkg)
                set.add(pkg)
            }
        }
        infos = am.runningAppProcesses
        for (info in infos) {
            for (pkg in info.pkgList) {
                set.remove(pkg)
            }
        }
        return set
    }

    /**
     * 杀死后台服务进程
     *
     * 需添加权限 `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>`
     *
     * @param packageName 包名
     * @return `true`: 杀死成功<br></br>`false`: 杀死失败
     */
    @SuppressLint("MissingPermission")
    @JvmStatic
    fun killBackgroundProcesses(packageName: String): Boolean {
        if (StringUtils.isSpace(packageName)) return false
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var infos: List<ActivityManager.RunningAppProcessInfo>? = am.runningAppProcesses
        if (infos == null || infos.size == 0) return true
        for (info in infos) {
            if (Arrays.asList(*info.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        infos = am.runningAppProcesses
        if (infos == null || infos.size == 0) return true
        for (info in infos) {
            if (Arrays.asList(*info.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }

    /**
     * 获取当前进程名
     * @param cxt
     * @param pid
     * @return
     */
    @JvmStatic
    fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

}
