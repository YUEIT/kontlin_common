package cn.yue.base.common.utils.app

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.code.ProcessUtils
import cn.yue.base.common.utils.code.ShellUtils
import cn.yue.base.common.utils.constant.EncryptUtils
import cn.yue.base.common.utils.constant.StringUtils
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.common.utils.file.CleanUtils
import cn.yue.base.common.utils.file.FileUtils
import java.io.File
import java.util.*

/**
 * 介绍：App相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
class AppUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 封装App信息的Bean类
     */
    data class AppInfo
    /**
     * @param name        名称
     * @param icon        图标
     * @param packageName 包名
     * @param packagePath 包路径
     * @param versionName 版本号
     * @param versionCode 版本码
     * @param isSystem    是否系统应用
     */
    (var packageName: String?, var name: String?, var icon: Drawable?, var packagePath: String?,
     var versionName: String?, var versionCode: Int, var isSystem: Boolean) {

        init {
            this.name = name
            this.icon = icon
            this.packageName = packageName
            this.packagePath = packagePath
            this.versionName = versionName
            this.versionCode = versionCode
            this.isSystem = isSystem
        }

        override fun toString(): String {
            return "App包名：" + packageName +
                    "\nApp名称：" + name +
                    "\nApp图标：" + icon +
                    "\nApp路径：" + packagePath +
                    "\nApp版本号：" + versionName +
                    "\nApp版本码：" + versionCode +
                    "\n是否系统App：" + isSystem
        }
    }

    companion object {

        /**
         * 判断App是否安装
         *
         * @param context     上下文
         * @param packageName 包名
         * @return `true`: 已安装<br></br>`false`: 未安装
         */
        @JvmStatic
        fun isInstallApp(context: Context, packageName: String): Boolean {
            return !StringUtils.isSpace(packageName) && IntentUtils.getLaunchAppIntent(packageName) != null
        }

        /**
         * 安装App(支持6.0)
         *
         * @param context  上下文
         * @param filePath 文件路径
         */
        @JvmStatic
        fun installApp(context: Context, filePath: String) {
            installApp(context, FileUtils.getFileByPath(filePath))
        }

        /**
         * 安装App（支持6.0）
         *
         * @param context 上下文
         * @param file    文件
         */
        @JvmStatic
        fun installApp(context: Context, file: File?) {
            if (!FileUtils.isFileExists(file)) return
            context.startActivity(IntentUtils.getInstallAppIntent(file))
        }

        /**
         * 安装App（支持6.0）
         *
         * @param activity    activity
         * @param filePath    文件路径
         * @param requestCode 请求值
         */
        @JvmStatic
        fun installApp(activity: Activity, filePath: String, requestCode: Int) {
            installApp(activity, FileUtils.getFileByPath(filePath), requestCode)
        }

        /**
         * 安装App(支持6.0)
         *
         * @param activity    activity
         * @param file        文件
         * @param requestCode 请求值
         */
        @JvmStatic
        fun installApp(activity: Activity, file: File?, requestCode: Int) {
            if (!FileUtils.isFileExists(file)) return
            activity.startActivityForResult(IntentUtils.getInstallAppIntent(file), requestCode)
        }

        /**
         * 静默安装App
         *
         * 非root需添加权限 `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`
         *
         * @param filePath 文件路径
         * @return `true`: 安装成功<br></br>`false`: 安装失败
         */
        @JvmStatic
        fun installAppSilent(filePath: String): Boolean {
            val file = FileUtils.getFileByPath(filePath)
            if (!FileUtils.isFileExists(file)) return false
            val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install $filePath"
            val commandResult = ShellUtils.execCmd(command, !isSystemApp(Utils.getContext()), true)
            return commandResult.successMsg != null && commandResult.successMsg!!.toLowerCase().contains("success")
        }

        /**
         * 卸载App
         *
         * @param context     上下文
         * @param packageName 包名
         */
        @JvmStatic
        fun uninstallApp(context: Context, packageName: String) {
            if (StringUtils.isSpace(packageName)) return
            context.startActivity(IntentUtils.getUninstallAppIntent(packageName))
        }

        /**
         * 卸载App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        @JvmStatic
        fun uninstallApp(activity: Activity, packageName: String, requestCode: Int) {
            if (StringUtils.isSpace(packageName)) return
            activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode)
        }

        /**
         * 静默卸载App
         *
         * 非root需添加权限 `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`
         *
         * @param context     上下文
         * @param packageName 包名
         * @param isKeepData  是否保留数据
         * @return `true`: 卸载成功<br></br>`false`: 卸载成功
         */
        @JvmStatic
        fun uninstallAppSilent(context: Context, packageName: String, isKeepData: Boolean): Boolean {
            if (StringUtils.isSpace(packageName)) return false
            val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (if (isKeepData) "-k " else "") + packageName
            val commandResult = ShellUtils.execCmd(command, !isSystemApp(context), true)
            return commandResult.successMsg != null && commandResult.successMsg!!.toLowerCase().contains("success")
        }


        /**
         * 判断App是否有root权限
         *
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isAppRoot(): Boolean {
                val result = ShellUtils.execCmd("echo root", true)
                if (result.result == 0) {
                    return true
                }
                if (result.errorMsg != null) {
                    LogUtils.d("isAppRoot", result.errorMsg?:"")
                }
                return false
            }

        /**
         * 打开App
         *
         * @param packageName 包名
         */
        @JvmStatic
        fun launchApp(packageName: String) {
            if (StringUtils.isSpace(packageName)) return
            Utils.getContext().startActivity(IntentUtils.getLaunchAppIntent(packageName))
        }

        /**
         * 打开App
         *
         * @param activity    activity
         * @param packageName 包名
         * @param requestCode 请求值
         */
        @JvmStatic
        fun launchApp(activity: Activity, packageName: String, requestCode: Int) {
            if (StringUtils.isSpace(packageName)) return
            activity.startActivityForResult(IntentUtils.getLaunchAppIntent(packageName), requestCode)
        }

        /**
         * 获取App包名
         *
         * @param context 上下文
         * @return App包名
         */
        @JvmStatic
        fun getAppPackageName(context: Context): String {
            return context.packageName
        }

        /**
         * 获取App具体设置
         *
         * @param context     上下文
         * @param packageName 包名
         */
        @JvmStatic
        fun getAppDetailsSettings(context: Context, packageName: String = context.packageName) {
            if (StringUtils.isSpace(packageName)) return
            context.startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName))
        }

        /**
         * 获取App名称
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App名称
         */
        @JvmStatic
        fun getAppName(context: Context, packageName: String = context.packageName): String? {
            if (StringUtils.isSpace(packageName)) return null
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.loadLabel(pm)?.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App图标
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App图标
         */
        @JvmStatic
        fun getAppIcon(context: Context, packageName: String = context.packageName): Drawable? {
            if (StringUtils.isSpace(packageName)) return null
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.loadIcon(pm)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App路径
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App路径
         */
        @JvmStatic
        fun getAppPath(context: Context, packageName: String = context.packageName): String? {
            if (StringUtils.isSpace(packageName)) return null
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.applicationInfo?.sourceDir
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App版本号
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App版本号
         */
        @JvmStatic
        fun getAppVersionName(context: Context, packageName: String = context.packageName): String? {
            if (StringUtils.isSpace(packageName)) return null
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取App版本码
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App版本码
         */
        @JvmStatic
        fun getAppVersionCode(context: Context, packageName: String = context.packageName): Int {
            if (StringUtils.isSpace(packageName)) return -1
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return pi?.versionCode ?: -1
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return -1
            }

        }

        /**
         * 判断App是否是系统应用
         *
         * @param context     上下文
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isSystemApp(context: Context, packageName: String = context.packageName): Boolean {
            if (StringUtils.isSpace(packageName)) return false
            try {
                val pm = context.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                return ai != null && ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 判断App是否是Debug版本
         *
         * @param context     上下文
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isAppDebug(context: Context, packageName: String = context.packageName): Boolean {
            if (StringUtils.isSpace(packageName)) return false
            try {
                val pm = context.packageManager
                val ai = pm.getApplicationInfo(packageName, 0)
                return ai != null && ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 获取App签名
         *
         * @param context 上下文
         * @return App签名
         */
        @JvmStatic
        fun getAppSignature(context: Context): Array<Signature>? {
            return getAppSignature(context, context.packageName)
        }

        /**
         * 获取App签名
         *
         * @param context     上下文
         * @param packageName 包名
         * @return App签名
         */
        @JvmStatic
        @SuppressLint("PackageManagerGetSignatures")
        fun getAppSignature(context: Context, packageName: String): Array<Signature>? {
            if (StringUtils.isSpace(packageName)) return null
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                return pi?.signatures
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 获取应用签名的的SHA1值
         *
         * 可据此判断高德，百度地图key是否正确
         *
         * @param context     上下文
         * @param packageName 包名
         * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
         */
        @JvmStatic
        fun getAppSignatureSHA1(context: Context, packageName: String = context.packageName): String? {
            val signature = getAppSignature(context, packageName) ?: return null
            return EncryptUtils.encryptSHA1ToString(signature[0].toByteArray())!!.replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
        }

        /**
         * 判断App是否处于前台
         *
         * @param context 上下文
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isAppForeground(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val infos = manager.runningAppProcesses
            if (infos == null || infos.size == 0) return false
            for (info in infos) {
                if (info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return info.processName == context.packageName
                }
            }
            return false
        }

        /**
         * 判断App是否处于前台
         *
         * 当不是查看当前App，且SDK大于21时，
         * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`
         *
         * @param context     上下文
         * @param packageName 包名
         * @return `true`: 是<br></br>`false`: 否
         */
        @JvmStatic
        fun isAppForeground(context: Context, packageName: String): Boolean {
            return !StringUtils.isSpace(packageName) && packageName == ProcessUtils.getForegroundProcessName()
        }

        /**
         * 获取App信息
         *
         * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）
         *
         * @param context     上下文
         * @param packageName 包名
         * @return 当前应用的AppInfo
         */
        @JvmStatic
        fun getAppInfo(context: Context, packageName: String = context.packageName): AppInfo? {
            try {
                val pm = context.packageManager
                val pi = pm.getPackageInfo(packageName, 0)
                return getBean(pm, pi)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                return null
            }

        }

        /**
         * 得到AppInfo的Bean
         *
         * @param pm 包的管理
         * @param pi 包的信息
         * @return AppInfo类
         */
        @JvmStatic
        private fun getBean(pm: PackageManager?, pi: PackageInfo?): AppInfo? {
            if (pm == null || pi == null) return null
            val ai = pi.applicationInfo
            val packageName = pi.packageName
            val name = ai.loadLabel(pm).toString()
            val icon = ai.loadIcon(pm)
            val packagePath = ai.sourceDir
            val versionName = pi.versionName
            val versionCode = pi.versionCode
            val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
            return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
        }

        /**
         * 获取所有已安装App信息
         *
         * [.getBean]（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）
         *
         * 依赖上面的getBean方法
         *
         * @param context 上下文
         * @return 所有已安装的AppInfo列表
         */
        @JvmStatic
        fun getAppsInfo(context: Context): List<AppInfo> {
            val list = ArrayList<AppInfo>()
            val pm = context.packageManager
            // 获取系统中安装的所有软件信息
            val installedPackages = pm.getInstalledPackages(0)
            for (pi in installedPackages) {
                val ai = getBean(pm, pi) ?: continue
                list.add(ai)
            }
            return list
        }

        /**
         * 清除App所有数据
         *
         * @param context  上下文
         * @param dirPaths 目录路径
         * @return `true`: 成功<br></br>`false`: 失败
         */
        @JvmStatic
        fun cleanAppData(context: Context, vararg dirPaths: String): Boolean {
            val dirs = arrayOfNulls<File>(dirPaths.size)
            var i = 0
            for (dirPath in dirPaths) {
                dirs[i++] = File(dirPath)
            }
            return cleanAppData(*dirs)
        }

        /**
         * 清除App所有数据
         *
         * @param dirs 目录
         * @return `true`: 成功<br></br>`false`: 失败
         */
        @JvmStatic
        fun cleanAppData(vararg dirs: File?): Boolean {
            var isSuccess = CleanUtils.cleanInternalCache()
            isSuccess = isSuccess and CleanUtils.cleanInternalDbs()
            isSuccess = isSuccess and CleanUtils.cleanInternalSP()
            isSuccess = isSuccess and CleanUtils.cleanInternalFiles()
            isSuccess = isSuccess and CleanUtils.cleanExternalCache()
            for (dir in dirs) {
                isSuccess = isSuccess and CleanUtils.cleanCustomCache(dir!!)
            }
            return isSuccess
        }
    }
}