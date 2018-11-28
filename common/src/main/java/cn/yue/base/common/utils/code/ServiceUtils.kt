package cn.yue.base.common.utils.code

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import java.util.*

/**
 * 介绍：服务相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object ServiceUtils {

    /**
     * 获取所有运行的服务
     *
     * @param context 上下文
     * @return 服务名集合
     */
    @JvmStatic
    fun getAllRunningService(context: Context): Set<*>? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = activityManager.getRunningServices(0x7FFFFFFF)
        val names = HashSet<String>()
        if (infos == null || infos.size == 0) return null
        for (info in infos) {
            names.add(info.service.className)
        }
        return names
    }

    /**
     * 启动服务
     *
     * @param context   上下文
     * @param className 完整包名的服务类名
     */
    @JvmStatic
    fun startService(context: Context, className: String) {
        try {
            startService(context, Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 启动服务
     *
     * @param context 上下文
     * @param cls     服务类
     */
    @JvmStatic
    fun startService(context: Context, cls: Class<*>) {
        val intent = Intent(context, cls)
        context.startService(intent)
    }

    /**
     * 停止服务
     *
     * @param context   上下文
     * @param className 完整包名的服务类名
     * @return `true`: 停止成功<br></br>`false`: 停止失败
     */
    @JvmStatic
    fun stopService(context: Context, className: String): Boolean {
        try {
            return stopService(context, Class.forName(className))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 停止服务
     *
     * @param context 上下文
     * @param cls     服务类
     * @return `true`: 停止成功<br></br>`false`: 停止失败
     */
    @JvmStatic
    fun stopService(context: Context, cls: Class<*>): Boolean {
        val intent = Intent(context, cls)
        return context.stopService(intent)
    }

    /**
     * 绑定服务
     *
     * @param context   上下文
     * @param className 完整包名的服务类名
     * @param conn      服务连接对象
     * @param flags     绑定选项
     *
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    @JvmStatic
    fun bindService(context: Context, className: String, conn: ServiceConnection, flags: Int) {
        try {
            bindService(context, Class.forName(className), conn, flags)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 绑定服务
     *
     * @param context 上下文
     * @param cls     服务类
     * @param conn    服务连接对象
     * @param flags   绑定选项
     *
     *  * [Context.BIND_AUTO_CREATE]
     *  * [Context.BIND_DEBUG_UNBIND]
     *  * [Context.BIND_NOT_FOREGROUND]
     *  * [Context.BIND_ABOVE_CLIENT]
     *  * [Context.BIND_ALLOW_OOM_MANAGEMENT]
     *  * [Context.BIND_WAIVE_PRIORITY]
     *
     */
    @JvmStatic
    fun bindService(context: Context, cls: Class<*>, conn: ServiceConnection, flags: Int) {
        val intent = Intent(context, cls)
        context.bindService(intent, conn, flags)
    }

    /**
     * 解绑服务
     *
     * @param context 上下文
     * @param conn    服务连接对象
     */
    @JvmStatic
    fun unbindService(context: Context, conn: ServiceConnection) {
        context.unbindService(conn)
    }

    /**
     * 判断服务是否运行
     *
     * @param context   上下文
     * @param className 完整包名的服务类名
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    fun isServiceRunning(context: Context, className: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = activityManager.getRunningServices(0x7FFFFFFF)
        if (infos == null || infos.size == 0) return false
        for (info in infos) {
            if (className == info.service.className) return true
        }
        return false
    }

}