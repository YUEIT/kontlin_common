package cn.yue.base.middle.init

import cn.yue.base.common.activity.CommonApplication
import cn.yue.base.middle.module.IBaseModule
import cn.yue.base.middle.module.ModuleType
import cn.yue.base.middle.module.manager.ModuleManager

abstract class MiddleApplication : CommonApplication() {
    override fun init() {
        ModuleManager.register(ModuleType.MODULE_BASE, IBaseModule::class, BaseModuleService())
        registerModule()
        ModuleManager.doInit(this)
    }

    abstract fun registerModule()
}