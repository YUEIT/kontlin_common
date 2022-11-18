package cn.yue.test

import cn.yue.base.middle.init.InitConstant
import cn.yue.base.middle.init.MiddleApplication
import cn.yue.base.middle.module.IAppModule
import cn.yue.base.middle.module.ModuleType
import cn.yue.base.middle.module.manager.ModuleManager
import cn.yue.test.utils.LocalStorage


/**
 * Description :
 * Created by yue on 2018/11/14
 */
class AppApplication : MiddleApplication() {

    override fun init() {
        InitConstant.setDebug(BuildConfig.DEBUG_MODE)
        InitConstant.setVersionName(BuildConfig.VERSION_NAME)
        InitConstant.setServiceEnvironment(LocalStorage.getServiceEnvironment())
        super.init()
        ModuleManager.register(ModuleType.MODULE_APP, IAppModule::class, AppModuleService())
    }

}