package cn.yue.base.common.activity

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import cn.yue.base.common.utils.Utils.init
import cn.yue.base.common.utils.code.ProcessUtils.getProcessName
import cn.yue.base.common.utils.debug.LogUtils.Companion.e

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
        if (null != packageName && packageName ==
                getProcessName(this, Process.myPid())) {
            //只有进程名和包名一样 才执行初始化操作
            initUtils()
        } else {
            e("其他进程启动,不做初始化操作:" + Process.myPid())
        }
    }

    private fun initUtils() {
        init(this)
        initPhotoError()
        init()
    }

    protected abstract fun init()

    /**
     * android 7.0系统解决拍照的问题
     */
    private fun initPhotoError() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure()
        }
    }
}