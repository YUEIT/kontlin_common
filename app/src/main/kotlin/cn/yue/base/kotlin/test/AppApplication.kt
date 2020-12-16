package cn.yue.base.kotlin.test

import cn.yue.base.kotlin.test.utils.LocalStorage
import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.init.MiddleApplication
import cn.yue.base.middle.module.IAppModule
import cn.yue.base.middle.module.ModuleType
import cn.yue.base.middle.module.manager.ModuleManager

/**
 * Description :
 * Created by yue on 2018/11/14
 */
class AppApplication : MiddleApplication() {

    override fun init() {
        InitConstant.setDebug(BuildConfig.DEBUG_MODE)
        InitConstant.setServiceEnvironment(LocalStorage.getServiceEnvironment())
        InitConstant.setVersionName(BuildConfig.VERSION_NAME)
        super.init()
    }

    override fun registerModule() {
        ModuleManager.register(ModuleType.MODULE_APP, IAppModule::class, AppModuleService())
    }
}