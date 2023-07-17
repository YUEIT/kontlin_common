package cn.yue.base.utils.device

import android.Manifest.permission
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresPermission
import cn.yue.base.utils.Utils
import cn.yue.base.utils.debug.LogUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.util.*

object ProcessUtils {
// Access to usage information.
    /**
     * Return the foreground process name.
     *
     * Target APIs greater than 21 must hold
     * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
     *
     * @return the foreground process name
     */
    fun getForegroundProcessName(): String? {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pInfo = am.runningAppProcesses
        if (pInfo != null && pInfo.size > 0) {
            for (aInfo in pInfo) {
                if (aInfo.importance
                        == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return aInfo.processName
                }
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val pm: PackageManager = Utils.getContext().packageManager
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            val list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            LogUtils.i("ProcessUtils", list.toString())
            if (list.size <= 0) {
                LogUtils.i("ProcessUtils",
                        "getForegroundProcessName: noun of access to usage information.")
                return ""
            }
            try { // Access to usage information.
                val info = pm.getApplicationInfo(Utils.getContext().packageName, 0)
                val aom = Utils.getContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName) != AppOpsManager.MODE_ALLOWED) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Utils.getContext().startActivity(intent)
                }
                if (aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                info.uid,
                                info.packageName) != AppOpsManager.MODE_ALLOWED) {
                    LogUtils.i("ProcessUtils",
                            "getForegroundProcessName: refuse to device usage stats.")
                    return ""
                }
                val usageStatsManager = Utils.getContext()
                        .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                var usageStatsList: List<UsageStats>? = null
                if (usageStatsManager != null) {
                    val endTime = System.currentTimeMillis()
                    val beginTime = endTime - 86400000 * 7
                    usageStatsList = usageStatsManager
                            .queryUsageStats(UsageStatsManager.INTERVAL_BEST,
                                    beginTime, endTime)
                }
                if (usageStatsList == null || usageStatsList.isEmpty()) return ""
                var recentStats: UsageStats? = null
                for (usageStats in usageStatsList) {
                    if (recentStats == null
                            || usageStats.lastTimeUsed > recentStats.lastTimeUsed) {
                        recentStats = usageStats
                    }
                }
                return recentStats?.packageName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    /**
     * Return all background processes.
     *
     * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @return all background processes
     */
    //RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    fun getAllBackgroundProcesses(): Set<String> {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = am.runningAppProcesses
        val set: HashSet<String> = HashSet()
        if (info != null) {
            for (aInfo in info) {
                Collections.addAll(set, *aInfo.pkgList)
            }
        }
        return set
    }

    /**
     * Kill all background processes.
     *
     * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @return background processes were killed
     */
    @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    fun killAllBackgroundProcesses(): Set<String> {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var info = am.runningAppProcesses
        val set: MutableSet<String> = HashSet()
        if (info == null) return set
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                am.killBackgroundProcesses(pkg)
                set.add(pkg)
            }
        }
        info = am.runningAppProcesses
        for (aInfo in info) {
            for (pkg in aInfo.pkgList) {
                set.remove(pkg)
            }
        }
        return set
    }

    /**
     * Kill background processes.
     *
     * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
     *
     * @param packageName The name of the package.
     * @return `true`: success<br></br>`false`: fail
     */
    @RequiresPermission(permission.KILL_BACKGROUND_PROCESSES)
    fun killBackgroundProcesses(packageName: String): Boolean {
        val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        var info = am.runningAppProcesses
        if (info == null || info.size == 0) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                am.killBackgroundProcesses(packageName)
            }
        }
        info = am.runningAppProcesses
        if (info == null || info.size == 0) return true
        for (aInfo in info) {
            if (Arrays.asList(*aInfo.pkgList).contains(packageName)) {
                return false
            }
        }
        return true
    }

    /**
     * Return whether app running in the main process.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMainProcess(): Boolean {
        return Utils.getContext().packageName == getCurrentProcessName()
    }

    /**
     * Return whether app running in the main process.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isMainProcess(context: Context?): Boolean {
        return context?.packageName == getCurrentProcessName()
    }

    /**
     * Return the name of current process.
     *
     * @return the name of current process
     */
    fun getCurrentProcessName(): String? {
        var name: String? = getCurrentProcessNameByFile()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByAms()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByReflect()
        return name
    }

    private fun getCurrentProcessNameByFile(): String {
        return try {
            val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader = BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getCurrentProcessNameByAms(): String {
        try {
            val am = Utils.getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    ?: return ""
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) return ""
            val pid = Process.myPid()
            for (aInfo in info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName
                    }
                }
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String? {
        var processName = ""
        try {
            val app: Application = Utils.getContext() as Application
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processName
    }

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

 