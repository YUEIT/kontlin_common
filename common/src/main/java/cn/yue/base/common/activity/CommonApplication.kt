package cn.yue.base.common.activity

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.code.ProcessUtils
import cn.yue.base.common.utils.debug.LogUtils

/**
 * Description :
 * Created by yue on 2019/6/17
 */
open class CommonApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        if (null != packageName && packageName == ProcessUtils.getProcessName(this, android.os.Process.myPid())) {
            initUtils()
        } else {
            LogUtils.e("其他进程启动,不做初始化操作:" + android.os.Process.myPid())
        }
    }

    private fun initUtils() {
        Utils.init(this);
        initPhotoError()
        init()
        FRouter.debug()
        FRouter.init(this)
    }

    protected open fun init() {

    }

    private fun initPhotoError() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure()
        }
    }
}