package cn.yue.base.middle.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import cn.yue.base.common.activity.CommonApplication
import cn.yue.base.middle.module.IBaseModule
import cn.yue.base.middle.module.ModuleType
import cn.yue.base.middle.module.manager.ModuleManager

abstract class MiddleApplication : CommonApplication(), ViewModelStoreOwner {
    override fun init() {
        ModuleManager.register(ModuleType.MODULE_BASE, IBaseModule::class, BaseModuleService())
        registerModule()
        ModuleManager.doInit(this)
    }

    abstract fun registerModule()

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
}