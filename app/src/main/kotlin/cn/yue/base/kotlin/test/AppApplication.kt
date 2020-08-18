package cn.yue.base.kotlin.test

import cn.yue.base.common.activity.CommonApplication
import cn.yue.base.middle.init.MiddleApplication
import cn.yue.base.middle.module.IAppModule
import cn.yue.base.middle.module.ModuleType
import cn.yue.base.middle.module.manager.ModuleManager

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class AppApplication : MiddleApplication() {

    override fun registerModule() {
        ModuleManager.register(ModuleType.MODULE_APP, IAppModule::class, AppModuleService())
    }
}