package cn.yue.base.init

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import cn.yue.base.module.IBaseModule
import cn.yue.base.module.ModuleType
import cn.yue.base.module.manager.ModuleManager
import cn.yue.base.router.FRouter
import cn.yue.base.utils.Utils
import cn.yue.base.utils.debug.LogUtils

open class CommonApplication : Application(), ViewModelStoreOwner {
    
    override fun onCreate() {
        super.onCreate()
        if (null != packageName && packageName == getThisProcessName()) {
            Utils.init(this)
            preInit()
            onInit()
        } else {
            LogUtils.e("其他进程启动,不做初始化操作:" + Process.myPid())
        }
    }
    
    open fun preInit() {}
    
    open fun onInit() {
        FRouter.init(this)
	    BaseUrlAddress.setDebug(InitConstant.isDebug())
        LogUtils.setDebug(InitConstant.isDebug())
        ModuleManager.register(ModuleType.MODULE_BASE, IBaseModule::class, BaseModuleService())
    }

    override fun getViewModelStore(): ViewModelStore {
        return viewModelStore
    }

    private val viewModelStore = ViewModelStore()

    private fun getViewModelProvider(): ViewModelProvider {
        return ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(this))
    }

    fun <VM: ViewModel> createViewModel(cls: Class<VM>): VM {
        return getViewModelProvider()[cls]
    }
    
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