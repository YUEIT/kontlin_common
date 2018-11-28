package cn.yue.base.common.utils.debug

import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.file.FileUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*

/**
 * 介绍：崩溃相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class CrashUtils private constructor() : UncaughtExceptionHandler {

    private var mHandler: UncaughtExceptionHandler? = null

    private var mInitialized: Boolean = false
    private var crashDir: String? = null
    private var versionName: String? = null
    private var versionCode: Int = 0

    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private// 设备厂商
    // 设备型号
    // 系统版本
    // SDK版本
    val crashHead: String
        get() = "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +
                "\nDevice Model       : " + Build.MODEL +
                "\nAndroid Version    : " + Build.VERSION.RELEASE +
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n"

    /**
     * 初始化
     *
     * @return `true`: 成功<br></br>`false`: 失败
     */
    fun init(): Boolean {
        if (mInitialized) return true
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val baseCache = Utils.getContext().externalCacheDir ?: return false
            crashDir = baseCache.path + File.separator + "crash" + File.separator
        } else {
            val baseCache = Utils.getContext().cacheDir ?: return false
            crashDir = baseCache.path + File.separator + "crash" + File.separator
        }
        try {
            val pi = Utils.getContext().packageManager.getPackageInfo(Utils.getContext().packageName, 0)
            versionName = pi.versionName
            versionCode = pi.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

        mHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
        mInitialized = true
        return mInitialized
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val now = SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val fullPath = "$crashDir$now.txt"
        if (!FileUtils.createOrExistsFile(fullPath)) return
        Thread(Runnable {
            var pw: PrintWriter? = null
            try {
                pw = PrintWriter(FileWriter(fullPath, false))
                pw.write(crashHead)
                throwable.printStackTrace(pw)
                var cause: Throwable? = throwable.cause
                while (cause != null) {
                    cause.printStackTrace(pw)
                    cause = cause.cause
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(pw!!)
            }
        }).start()
        if (mHandler != null) {
            mHandler!!.uncaughtException(thread, throwable)
        }
    }

    companion object {

        @Volatile
        private var mInstance: CrashUtils? = null

        /**
         * 获取单例
         *
         * 在Application中初始化`CrashUtils.getInstance().init(this);`
         *
         * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
         *
         * @return 单例
         */
        @JvmStatic
        val instance: CrashUtils?
            get() {
                if (mInstance == null) {
                    synchronized(CrashUtils::class.java) {
                        if (mInstance == null) {
                            mInstance = CrashUtils()
                        }
                    }
                }
                return mInstance
            }
    }
}
