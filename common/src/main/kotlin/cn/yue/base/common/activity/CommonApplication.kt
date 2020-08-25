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
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //        MultiDex.install(this);
    }

    override fun onCreate() {
        super.onCreate()
        if (null != packageName && packageName == getThisProcessName()) {
            //只有进程名和包名一样 才执行初始化操作
            initUtils()
        } else {
            LogUtils.e("其他进程启动,不做初始化操作:" + Process.myPid())
        }
    }

    private fun initUtils() {
        Utils.init(this)
        init()
    }

    protected abstract fun init()

    private fun getThisProcessName(): String? {
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == Process.myPid()) {
                return procInfo.processName
            }
        }
        return null
    }
}