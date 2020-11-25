package cn.yue.base.common.activity

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.debug.LogUtils

/**
 * Description :
 * Created by yue on 2019/3/11
 */
abstract class CommonApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (null != packageName && packageName == getThisProcessName()) {
            //只有进程名和包名一样 才执行初始化操作
            Utils.init(this)
            init()
        } else {
            LogUtils.e("其他进程启动,不做初始化操作:" + Process.myPid())
        }
    }

    protected abstract fun init()

    private fun getThisProcessName(): String? {
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (processInfo in runningApps) {
            if (processInfo.pid == Process.myPid()) {
                return processInfo.processName
            }
        }
        return null
    }
}